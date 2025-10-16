package com.example.lagvis_v1.data.repository.ia

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.lagvis_v1.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GeminiConvenioRepository(private val context: Context) {
    private val TAG = "GeminiRepo"

    // ⚠️ REEMPLAZAR CON TU CLAVE REAL DE GEMINI API
    private val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY
    // ¡CORREGIDO! Eliminamos el formato Markdown que causaba el error "no protocol".
    private val API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    // Configuración de reintentos
    private val MAX_RETRIES = 3
    private val INITIAL_DELAY_MS = 2000L // 2 segundos de espera inicial

    /**
     * Helper para leer el contenido de una Uri de archivo y codificarlo en Base64.
     */
    private fun uriToBase64(uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        requireNotNull(inputStream) { "No se pudo abrir el archivo PDF desde la URI." }

        val bytes = inputStream.readBytes()
        // Usamos NO_WRAP para asegurar que la cadena Base64 no tenga saltos de línea
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Llama a la API de Gemini para obtener el resumen XML del PDF.
     */
    suspend fun summarizePdfToXml(pdfUri: Uri): String = withContext(Dispatchers.IO) {
        if (GEMINI_API_KEY.contains("YOUR_GEMINI_API_KEY")) {
            throw IllegalStateException("API Key no configurada. Por favor, reemplaza 'YOUR_GEMINI_API_KEY_HERE' en GeminiConvenioRepository.kt")
        }

        Log.d(TAG, "Iniciando procesamiento de PDF con Gemini.")

        val base64Pdf = uriToBase64(pdfUri)

        // 1. Definición del System Prompt para forzar el formato XML
        val systemInstruction = """
            Eres un experto legal y resumidor de convenios colectivos. Tu única tarea es analizar el documento PDF adjunto 
            y extraer la información clave. DEBES DEVOLVER TU RESPUESTA EXCLUSIVAMENTE EN FORMATO XML. NO ME PONGAS CARACTERES RAROS SIMPLEMENTE DEVUELVEME UN STRING CON EL FORMATO DE XML QUE TE HE MANDADO POR FAVOR NO ME FALLES SOLO QUIERO EL XML NO ME DIGAS NI HOLA SOLO QUIERO EL XML
            
            El XML debe seguir la siguiente estructura, sin incluir ningún texto adicional (Markdown, explicación, etc.):
            <convenio_colectivo>
                <titulo>Título del Convenio Extraído del PDF</titulo>
                <resumen_general>Breve resumen general (máx 3 frases)</resumen_general>
                <vacaciones>
                    <dias>Número de días naturales/laborables</dias>
                    <observaciones>Detalles sobre solicitud o disfrute</observaciones>
                </vacaciones>
                <festivos>
                    <numero_dias>Total de festivos</numero_dias>
                    <detalle>Detalles sobre compensación</detalle>
                </festivos>
                <horas_extraordinarias>
                    <regulacion>Regulación y compensación de horas extra</regulacion>
                </horas_extraordinarias>
                <salario>
                    <informacion_importante>Información sobre tablas o subidas salariales</informacion_importante>
                    <salario_aproximado>Rango salarial aproximado (ej: 1.500€ - 2.200€ brutos/mes</salario_aproximado>
                </salario>
                <licencias>
                    <retribuidas>
                        <matrimonio>Detalle licencia matrimonio</matrimonio>
                        <fallecimiento_familiares>Detalle fallecimiento</fallecimiento_familiares>
                    </retribuidas>
                    <no_retribuidas>
                        <formacion>Detalle licencia formación</formacion>
                        <otros>Otras licencias importantes</otros>
                    </no_retribuidas>
                </licencias>
                <seguro>
                    <cobertura>Detalle si es obligatorio o no</cobertura>
                    <importe>Importe cubierto (ej: 20.000€)</importe>
                </seguro>
                <derechos_generales>
                    <igualdad>Regulación de planes de igualdad</igualdad>
                    <salud_laboral>Regulación de salud laboral</salud_laboral>
                    <conciliacion>Medidas de conciliación</conciliacion>
                    <representacion>Representación sindical</representacion>
                </derechos_generales>
                <manutencion>
                    <detalleManun>Información sobre dietas, transporte o manutención</detalleManun>
                </manutencion>
            </convenio_colectivo>
            Si un campo no aplica o no se encuentra, deja el contenido de esa etiqueta vacío (ej: <importe></importe>).
        """.trimIndent()

        // 2. Construcción del Payload JSON con configuración de generación
        val payload = """
        {
          "contents": [
            {
              "parts": [
                {
                  "inlineData": {
                    "mimeType": "application/pdf",
                    "data": "$base64Pdf"
                  }
                },
                {
                  "text": "Analiza el PDF adjunto y devuelve el resumen del convenio en formato XML."
                }
              ]
            }
          ],
          "systemInstruction": {
            "parts": [
              {
                "text": "$systemInstruction"
              }
            ]
          },
          "generationConfig": {
            "maxOutputTokens": 65536,
            "temperature": 0.2,
            "topP": 0.95,
            "topK": 40
          }
        }
        """.trimIndent()

        // Variables para el bucle de reintento
        var attempt = 0
        var currentDelay = INITIAL_DELAY_MS

        while (attempt < MAX_RETRIES) {
            attempt++
            var connection: HttpURLConnection? = null

            try {
                Log.d(TAG, "Intento de llamada a Gemini $attempt de $MAX_RETRIES.")

                val url = URL("$API_ENDPOINT?key=$GEMINI_API_KEY")
                connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Mantenemos los timeouts de 60 segundos
                connection.connectTimeout = 10000000 // 60 segundos para establecer la conexión
                connection.readTimeout = 10000000 // 60 segundos para leer la respuesta de la IA

                connection.outputStream.use { os ->
                    os.write(payload.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Éxito: Leer la respuesta JSON
                    val responseJson = connection.inputStream.bufferedReader().use(BufferedReader::readText)

                    // 4. Extracción ROBUSTA del texto generado (el XML) del JSON
                    // Usamos una estrategia más agresiva para extraer el contenido
                    Log.d(TAG, "Extrayendo texto de JSON (longitud respuesta: ${responseJson.length})")

                    // Estrategia 1: Buscar el patrón "text": "..." pero de forma más robusta
                    val textPattern = """"text"\s*:\s*"((?:[^"\\]|\\.)*)"""".toRegex(RegexOption.DOT_MATCHES_ALL)
                    val textMatch = textPattern.find(responseJson)

                    // Estrategia 2: Si falla, intentar con split manual
                    var rawText = textMatch?.groupValues?.get(1)

                    if (rawText == null) {
                        Log.w(TAG, "Regex falló, intentando extracción manual...")
                        val textIndex = responseJson.indexOf("\"text\":")
                        if (textIndex != -1) {
                            val startQuote = responseJson.indexOf("\"", textIndex + 7)
                            if (startQuote != -1) {
                                // Buscar el cierre considerando escapes
                                var endQuote = startQuote + 1
                                while (endQuote < responseJson.length) {
                                    if (responseJson[endQuote] == '"' && responseJson[endQuote - 1] != '\\') {
                                        break
                                    }
                                    endQuote++
                                }
                                rawText = responseJson.substring(startQuote + 1, endQuote)
                                Log.d(TAG, "Extracción manual exitosa: ${rawText.length} caracteres")
                            }
                        }
                    }

                    if (rawText == null) {
                        Log.e(TAG, "❌ No se pudo extraer el campo 'text' del JSON")
                        Log.e(TAG, "JSON recibido (primeros 1000 chars): ${responseJson.take(1000)}")
                        throw RuntimeException("No se encontró el campo 'text' en la respuesta JSON de Gemini")
                    }

                    Log.d(TAG, "Texto extraído (${rawText.length} caracteres). Deserializando...")

                    // Deserialización completa
                    var generatedText = rawText
                        .let { text ->
                            // Deserializar TODAS las secuencias Unicode \uXXXX
                            val unicodePattern = "\\\\u([0-9a-fA-F]{4})".toRegex()
                            unicodePattern.replace(text) { matchResult ->
                                matchResult.groupValues[1].toInt(16).toChar().toString()
                            }
                        }
                        .replace("\\n", "\n")       // Saltos de línea
                        .replace("\\r", "\r")       // Retornos de carro
                        .replace("\\t", "\t")       // Tabulaciones
                        .replace("\\\"", "\"")      // Comillas
                        .replace("\\\\/", "/")      // Barras
                        .replace("\\\\", "\\")      // Backslashes (DEBE SER EL ÚLTIMO)

                    Log.d(TAG, "Deserialización completa (${generatedText.length} caracteres)")

                    // --- INICIO: VALIDACIÓN NAZI DE XML ---
                    Log.d(TAG, "Texto completo recibido (${generatedText.length} caracteres): ${generatedText.take(500)}...")

                    val startTag = "<convenio_colectivo>"
                    val endTag = "</convenio_colectivo>"

                    val startIndex = generatedText.indexOf(startTag)
                    val endIndex = generatedText.lastIndexOf(endTag)

                    // VALIDACIÓN 1: Verificar que existen ambas etiquetas
                    if (startIndex == -1) {
                        Log.e(TAG, "❌ FALLO CRÍTICO: No se encontró la etiqueta de apertura <convenio_colectivo>")
                        Log.e(TAG, "Respuesta completa: $generatedText")
                        throw RuntimeException("XML inválido: Falta <convenio_colectivo>. La IA no siguió el formato. Respuesta: ${generatedText.take(500)}")
                    }

                    if (endIndex == -1) {
                        Log.e(TAG, "❌ FALLO CRÍTICO: No se encontró la etiqueta de cierre </convenio_colectivo>")
                        Log.e(TAG, "Respuesta TRUNCADA o INCOMPLETA. Longitud: ${generatedText.length}")
                        Log.e(TAG, "Últimos 200 caracteres: ${generatedText.takeLast(200)}")
                        throw RuntimeException("XML truncado: Falta </convenio_colectivo>. La respuesta de la IA fue cortada o está incompleta. Longitud: ${generatedText.length} caracteres. Final: ${generatedText.takeLast(100)}")
                    }

                    // VALIDACIÓN 2: Verificar orden correcto de etiquetas
                    if (startIndex >= endIndex) {
                        Log.e(TAG, "❌ FALLO: Etiquetas en orden incorrecto. Inicio: $startIndex, Fin: $endIndex")
                        throw RuntimeException("XML malformado: Las etiquetas están en orden incorrecto")
                    }

                    // VALIDACIÓN 3: Extraer el XML
                    val xmlContent = generatedText.substring(startIndex, endIndex + endTag.length).trim()

                    // VALIDACIÓN 4: Verificar longitud mínima (un XML válido debe tener al menos 500 caracteres)
                    if (xmlContent.length < 500) {
                        Log.e(TAG, "❌ FALLO: XML demasiado corto (${xmlContent.length} caracteres). Probablemente truncado.")
                        Log.e(TAG, "XML extraído: $xmlContent")
                        throw RuntimeException("XML sospechosamente corto (${xmlContent.length} chars). Puede estar truncado: $xmlContent")
                    }

                    // VALIDACIÓN 5: Contar etiquetas requeridas (al menos las principales)
                    val requiredTags = listOf("titulo", "resumen_general", "vacaciones", "salario", "licencias")
                    val missingTags = requiredTags.filter { !xmlContent.contains("<$it>") }

                    if (missingTags.isNotEmpty()) {
                        Log.w(TAG, "⚠️ ADVERTENCIA: Faltan etiquetas esperadas: $missingTags")
                        // No lanzamos error, solo advertencia, porque algunos campos pueden estar vacíos
                    }

                    // VALIDACIÓN 6: Verificar que no termine abruptamente (sin cerrar etiqueta)
                    val lastClosingTag = xmlContent.lastIndexOf("</")
                    val distanceToEnd = xmlContent.length - lastClosingTag
                    if (distanceToEnd > 50) {
                        Log.e(TAG, "❌ FALLO: El XML parece terminar abruptamente. Distancia desde última etiqueta de cierre: $distanceToEnd")
                        Log.e(TAG, "Últimos 100 caracteres: ${xmlContent.takeLast(100)}")
                        throw RuntimeException("XML posiblemente truncado. La última etiqueta de cierre está muy lejos del final ($distanceToEnd chars)")
                    }

                    // VALIDACIÓN 7: Buscar caracteres de escape sin procesar (\\n, \\", etc.)
                    if (xmlContent.contains("\\\\n") || xmlContent.contains("\\\\\"") || xmlContent.contains("\\\\u")) {
                        Log.e(TAG, "❌ FALLO: El XML contiene secuencias de escape sin deserializar")
                        throw RuntimeException("XML con caracteres de escape sin procesar. Revisa la deserialización.")
                    }

                    Log.i(TAG, "✅ VALIDACIÓN EXITOSA: XML completo y válido (${xmlContent.length} caracteres)")
                    Log.i(TAG, "✅ Gemini exitoso tras $attempt intento(s). Response code: $responseCode")

                    return@withContext xmlContent
                    // --- FIN: VALIDACIÓN NAZI DE XML ---

                } else {
                    // Error de la API (ej: 400 Bad Request, 500 Internal Server Error)
                    val errorStream = connection.errorStream ?: connection.inputStream
                    val errorResponse = errorStream.bufferedReader().use(BufferedReader::readText)
                    Log.e(TAG, "Gemini API Error ($responseCode): $errorResponse")

                    // Para errores que no sean timeout, no reintentamos
                    throw RuntimeException("Error de la API de Gemini: Código $responseCode. Respuesta: $errorResponse")
                }
            } catch (e: Exception) {
                // Capturamos el timeout o cualquier otra excepción
                if (e.cause is SocketTimeoutException && attempt < MAX_RETRIES) {
                    Log.w(TAG, "Timeout en el intento $attempt. Reintentando en $currentDelay ms.", e)
                    connection?.disconnect() // Aseguramos que la conexión se cierre antes de reintentar
                    delay(currentDelay)
                    currentDelay *= 2 // Backoff exponencial: 2s -> 4s -> 8s
                } else {
                    // Si es el último intento o un error que no sea timeout, lanzamos la excepción
                    Log.e(TAG, "Error fatal en la API o último intento fallido.", e)
                    throw e
                }
            } finally {
                connection?.disconnect()
            }
        }

        // Si el bucle termina sin un 'return', significa que todos los reintentos fallaron
        throw RuntimeException("Fallo en la comunicación con la API de Gemini después de $MAX_RETRIES reintentos.")
    }
}