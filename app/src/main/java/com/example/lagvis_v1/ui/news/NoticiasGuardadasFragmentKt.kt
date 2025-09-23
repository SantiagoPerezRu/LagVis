// ui/news/NoticiasGuardadasFragmentKt.kt
package com.example.lagvis_v1.ui.news

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.set
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.util.BaseFragment
import com.example.lagvis_v1.core.util.LagVisConstantes
import com.example.lagvis_v1.dominio.model.NewsItemKt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONArray
import org.json.JSONObject

class NoticiasGuardadasFragmentKt : BaseFragment() {

    private lateinit var tvSavedTituloNoticia: TextView
    private lateinit var tvSavedFechaNoticiaLabel: TextView
    private lateinit var tvSavedFechaNoticia: TextView
    private lateinit var tvSavedCreadorNoticiaLabel: TextView
    private lateinit var tvSavedCreadorNoticia: TextView
    private lateinit var tvSavedEnlaceNoticia: TextView
    private lateinit var btnSavedAnterior: Button
    private lateinit var btnSavedSiguiente: Button

    private var listaNoticiasGuardadas: MutableList<NewsItemKt> = mutableListOf()
    private var indiceNoticiaActual = 0

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_noticias_guardadas, container, false)

        tvSavedTituloNoticia = view.findViewById(R.id.tvSavedTituloNoticia)
        tvSavedFechaNoticiaLabel = view.findViewById(R.id.tvSavedFechaNoticiaLabel)
        tvSavedFechaNoticia = view.findViewById(R.id.tvSavedFechaNoticia)
        tvSavedCreadorNoticiaLabel = view.findViewById(R.id.tvSavedCreadorNoticiaLabel)
        tvSavedCreadorNoticia = view.findViewById(R.id.tvSavedCreadorNoticia)
        tvSavedEnlaceNoticia = view.findViewById(R.id.tvSavedEnlaceNoticia)
        btnSavedAnterior = view.findViewById(R.id.btnSavedAnterior)
        btnSavedSiguiente = view.findViewById(R.id.btnSavedSiguiente)

        progressDialog = ProgressDialog(context).apply {
            setMessage("Cargando noticias guardadas...")
            setCancelable(false)
        }

        btnSavedAnterior.isEnabled = false
        btnSavedSiguiente.isEnabled = false

        btnSavedAnterior.setOnClickListener {
            if (listaNoticiasGuardadas.isNotEmpty()) {
                indiceNoticiaActual = (indiceNoticiaActual - 1).coerceAtLeast(0)
                mostrarNoticiaActual()
                actualizarEstadoBotones()
            }
        }
        btnSavedSiguiente.setOnClickListener {
            if (listaNoticiasGuardadas.isNotEmpty()) {
                indiceNoticiaActual = (indiceNoticiaActual + 1).coerceAtMost(listaNoticiasGuardadas.size - 1)
                mostrarNoticiaActual()
                actualizarEstadoBotones()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarNoticiasGuardadas()
    }

    private fun cargarNoticiasGuardadas() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser == null) {
            val errorIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
            mostrarToastPersonalizado("¡Debes iniciar sesión para ver tus noticias guardadas!", errorIcon)
            tvSavedTituloNoticia.text = "Inicia sesión para ver tus noticias guardadas."
            ocultarDetallesNoticia()
            return
        }

        progressDialog.show()

        val url = LagVisConstantes.ENDPOINT_LISTAR_NOTICIAS
        val req = object : StringRequest(Method.POST, url,
            { response ->
                progressDialog.dismiss()
                runCatching {
                    val json = JSONObject(response)
                    val exito = json.getString("exito")
                    val mensaje = json.optString("mensaje", "")

                    if (exito == "1") {
                        val datos: JSONArray = json.getJSONArray("datos")
                        listaNoticiasGuardadas.clear()
                        for (i in 0 until datos.length()) {
                            val it = datos.getJSONObject(i)
                            val title = it.optString("titulo")
                            val link = it.optString("enlace")
                            val pubDate = it.optString("fecha")
                            val creator = it.optString("creador")
                            listaNoticiasGuardadas.add(NewsItemKt(title, link, pubDate, creator))
                        }
                        if (listaNoticiasGuardadas.isEmpty()) {
                            tvSavedTituloNoticia.text = "No tienes noticias guardadas aún."
                            ocultarDetallesNoticia()
                        } else {
                            indiceNoticiaActual = 0
                            mostrarNoticiaActual()
                            actualizarEstadoBotones()
                            mostrarDetallesNoticia()
                        }
                    } else {
                        tvSavedTituloNoticia.text = "Error al cargar noticias guardadas: $mensaje"
                        ocultarDetallesNoticia()
                        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
                        mostrarToastPersonalizado("Error: $mensaje", icon)
                    }
                }.onFailure {
                    tvSavedTituloNoticia.text = "Error al procesar los datos."
                    ocultarDetallesNoticia()
                    val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
                    mostrarToastPersonalizado("Error al procesar los datos.", icon)
                }
            },
            { error ->
                progressDialog.dismiss()
                val code = error.networkResponse?.statusCode
                val msg = "Error al conectar con el servidor." + (code?.let { " Código: $it" } ?: "")
                tvSavedTituloNoticia.text = msg
                ocultarDetallesNoticia()
                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
                mostrarToastPersonalizado(msg, icon)
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                mutableMapOf("uid" to currentUser.uid)
        }

        Volley.newRequestQueue(requireContext()).add(req)
    }

    private fun mostrarNoticiaActual() {
        if (listaNoticiasGuardadas.isEmpty() || indiceNoticiaActual !in listaNoticiasGuardadas.indices) return
        val n = listaNoticiasGuardadas[indiceNoticiaActual]

        tvSavedTituloNoticia.text = n.title
        tvSavedFechaNoticia.text = n.pubDate
        tvSavedCreadorNoticia.text = n.creator

        val texto = "Pincha aquí para ir al enlace de la noticia!"
        val span = SpannableString(texto).apply {
            this[0, length] = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(n.link))) }
                        .onFailure {
                            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
                            mostrarToastPersonalizado("No se pudo abrir el enlace.", icon)
                        }
                }
                override fun updateDrawState(ds: android.text.TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLUE
                    ds.isUnderlineText = true
                }
            }
        }
        tvSavedEnlaceNoticia.text = span
        tvSavedEnlaceNoticia.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun actualizarEstadoBotones() {
        if (listaNoticiasGuardadas.isEmpty()) {
            btnSavedAnterior.isEnabled = false
            btnSavedSiguiente.isEnabled = false
        } else {
            btnSavedAnterior.isEnabled = indiceNoticiaActual > 0
            btnSavedSiguiente.isEnabled = indiceNoticiaActual < listaNoticiasGuardadas.lastIndex
        }
    }

    private fun ocultarDetallesNoticia() {
        tvSavedFechaNoticiaLabel.visibility = View.GONE
        tvSavedFechaNoticia.visibility = View.GONE
        tvSavedCreadorNoticiaLabel.visibility = View.GONE
        tvSavedCreadorNoticia.visibility = View.GONE
        tvSavedEnlaceNoticia.visibility = View.GONE
        btnSavedAnterior.isEnabled = false
        btnSavedSiguiente.isEnabled = false
    }

    private fun mostrarDetallesNoticia() {
        tvSavedFechaNoticiaLabel.visibility = View.VISIBLE
        tvSavedFechaNoticia.visibility = View.VISIBLE
        tvSavedCreadorNoticiaLabel.visibility = View.VISIBLE
        tvSavedCreadorNoticia.visibility = View.VISIBLE
        tvSavedEnlaceNoticia.visibility = View.VISIBLE
    }
}
