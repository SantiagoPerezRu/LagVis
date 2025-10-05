// ui/auth/AdvancedFormRegister.kt
package com.example.lagvis_v1.ui.auth.register

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.core.util.BaseActivity
import com.example.lagvis_v1.core.util.LagVisConstantesKt
import com.example.lagvis_v1.databinding.ActivityAdvancedFormRegisterBinding
import com.example.lagvis_v1.ui.auth.login.AuthViewModelFactoryKt
import com.example.lagvis_v1.ui.auth.login.AuthViewModelKt
import com.example.lagvis_v1.ui.auth.login.LoginOnCompose
import java.util.Calendar

class AdvancedFormRegister : BaseActivity() {

    private lateinit var binding: ActivityAdvancedFormRegisterBinding

    // ViewModels (usando delegados)
    private val vm: AdvancedFormViewModel by viewModels { AdvancedFormViewModelFactory() }
    private val authVm: AuthViewModelKt by viewModels { AuthViewModelFactoryKt() }

    private var fechaNacimiento: String? = null // dd/MM/yyyy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdvancedFormRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Autocomplete adapters (arrays del resources)
        val comunidades = resources.getStringArray(R.array.comunidades_autonomas_registro)
        val sectores = resources.getStringArray(R.array.sectores)

        binding.autoCompleteTextViewComunidadAutonoma.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, comunidades)
        )
        binding.autoCompleteTextViewSectores.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sectores)
        )

        // Fecha
        binding.btnFecha.setOnClickListener { mostrarDatePicker() }

        // Enviar
        binding.btnEnviar.setOnClickListener { registrarNuevoUsuario() }

        // Observa envío
        vm.submit.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.btnEnviar.isEnabled = false
                is UiState.Success -> {
                    binding.btnEnviar.isEnabled = true
                    val ok: Drawable? = getDrawable(R.drawable.ic_check_circle)
                    showCustomToast("Datos registrados correctamente", ok)
                    startActivity(Intent(this, LoginOnCompose::class.java))
                    finish()
                }
                is UiState.Error -> {
                    binding.btnEnviar.isEnabled = true
                    val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
                    showCustomToast(state.message ?: "Error en el registro", err)
                }
                else -> Unit
            }
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH)
        val ano = calendar.get(Calendar.YEAR)

        val dp = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val seleccionada = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                if (seleccionada.after(Calendar.getInstance())) {
                    val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
                    showCustomToast("La fecha no puede ser futura!", err)
                } else {
                    // dd/MM/yyyy
                    fechaNacimiento = "$dayOfMonth/${month + 1}/$year"
                    binding.btnFecha.text = fechaNacimiento
                }
            },
            ano, mes, dia
        )
        dp.show()
    }

    private fun registrarNuevoUsuario() {
        // Validaciones de UI
        val fecha = fechaNacimiento
        if (fecha.isNullOrEmpty()) {
            val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
            showCustomToast("¡Debe seleccionar una fecha de nacimiento!", err)
            return
        }

        val nombre = binding.nombreEditText.text.toString().trim()
        val ape1 = binding.apellidoEditText.text.toString().trim()
        val ape2 = binding.apellido2EditText.text.toString().trim()
        val comunidad = binding.autoCompleteTextViewComunidadAutonoma.text.toString().trim()
        val sector = binding.autoCompleteTextViewSectores.text.toString().trim()

        if (nombre.isEmpty() || ape1.isEmpty() || ape2.isEmpty() ||
            comunidad.isEmpty() || sector.isEmpty()
        ) {
            val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
            showCustomToast("Debe introducir todos los campos!", err)
            return
        }

        // Validación fecha no futura (simple)
        try {
            val partes = fecha.split("/")
            val dia = partes[0].toInt()
            val mes = partes[1].toInt() - 1
            val ano = partes[2].toInt()

            val sel = Calendar.getInstance().apply { set(ano, mes, dia) }
            if (sel.after(Calendar.getInstance())) {
                val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
                showCustomToast("¡La fecha de nacimiento no puede ser futura!", err)
                return
            }
        } catch (_: Exception) {
            val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
            showCustomToast("¡Error al procesar la fecha de nacimiento!", err)
            return
        }

        val uid = authVm.uidOrNull()
        if (uid == null) {
            val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
            showCustomToast("¡Usuario no autenticado!", err)
            return
        }

        // ⚠️ Convertimos NOMBRE -> ID usando tus constantes que devuelven String
        val comunidadId: String = LagVisConstantesKt.getComunidadIdStr(comunidad)
        val sectorId: String = LagVisConstantesKt.getSectorIdStr(sector)

        if (comunidadId == "-1" || sectorId == "-1") {
            val err: Drawable? = getDrawable(R.drawable.ic_error_outline)
            showCustomToast("Comunidad o sector no válidos", err)
            return
        }

        // Enviar por VM (MVVM)
        vm.send(uid, nombre, ape1, ape2, comunidadId, sectorId, fecha)
    }
}
