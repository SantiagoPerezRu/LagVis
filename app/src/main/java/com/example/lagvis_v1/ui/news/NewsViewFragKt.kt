// ui/news/NewsViewFragKt.kt
package com.example.lagvis_v1.ui.news

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
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.core.util.BaseFragment
import com.example.lagvis_v1.databinding.FragmentSecondBinding
import com.example.lagvis_v1.dominio.model.NewsItemKt
import com.google.firebase.auth.FirebaseAuth

class NewsViewFragKt : BaseFragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: NewsViewModelKt
    private val auth by lazy { FirebaseAuth.getInstance() }

    private var listaNoticias: List<NewsItemKt> = emptyList()
    private var indiceNoticiaActual: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estado inicial UI
        binding.btnAnterior.isEnabled = false
        binding.btnSiguiente.isEnabled = false
        binding.btnGuardarNoticia.isEnabled = false

        // ViewModel
        vm = ViewModelProvider(this, NewsViewModelFactoryKt())[NewsViewModelKt::class.java]

        // Observa noticias
        vm.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnAnterior.isEnabled = false
                    binding.btnSiguiente.isEnabled = false
                    binding.btnGuardarNoticia.isEnabled = false
                    binding.tituloNoticia.text = "Cargando noticias…"
                    binding.enlaceNoticia.text = ""
                    binding.fechaNoticia2.text = ""
                    binding.creadorNoticia2.text = ""
                }
                is UiState.Success -> {
                    listaNoticias = state.data.orEmpty()
                    indiceNoticiaActual = 0
                    if (listaNoticias.isEmpty()) {
                        binding.tituloNoticia.text = "No se encontraron noticias."
                        binding.enlaceNoticia.text = ""
                        binding.fechaNoticia2.text = ""
                        binding.creadorNoticia2.text = ""
                        binding.btnAnterior.isEnabled = false
                        binding.btnSiguiente.isEnabled = false
                        binding.btnGuardarNoticia.isEnabled = false
                    } else {
                        mostrarNoticiaActual()
                        actualizarEstadoBotones()
                    }
                }
                is UiState.Error -> {
                    val msg = state.message ?: ""
                    binding.tituloNoticia.text = "Error al cargar las noticias: $msg"
                    binding.enlaceNoticia.text = ""
                    binding.fechaNoticia2.text = ""
                    binding.creadorNoticia2.text = ""
                    binding.btnAnterior.isEnabled = false
                    binding.btnSiguiente.isEnabled = false
                    binding.btnGuardarNoticia.isEnabled = false
                }
            }
        }

        // Observa guardado
        vm.save.observe(viewLifecycleOwner) { s ->
            when (s) {
                is UiState.Loading -> binding.btnGuardarNoticia.isEnabled = false
                is UiState.Success -> {
                    binding.btnGuardarNoticia.isEnabled = true
                    val ok: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle)
                    mostrarToastPersonalizado("¡Noticia guardada con éxito!", ok)
                }
                is UiState.Error -> {
                    binding.btnGuardarNoticia.isEnabled = true
                    val err: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
                    mostrarToastPersonalizado("¡Error al guardar la noticia!", err)
                }
            }
        }

        // Carga inicial
        vm.load(query = "Boe", country = "es", category = "business")

        // Listeners
        binding.btnAnterior.setOnClickListener {
            if (listaNoticias.isNotEmpty()) {
                indiceNoticiaActual = (indiceNoticiaActual - 1).coerceAtLeast(0)
                mostrarNoticiaActual()
                actualizarEstadoBotones()
            }
        }
        binding.btnSiguiente.setOnClickListener {
            if (listaNoticias.isNotEmpty()) {
                indiceNoticiaActual = (indiceNoticiaActual + 1).coerceAtMost(listaNoticias.size - 1)
                mostrarNoticiaActual()
                actualizarEstadoBotones()
            }
        }
        binding.btnGuardarNoticia.setOnClickListener { guardarNoticiaActual() }
    }

    private fun mostrarNoticiaActual() {
        if (listaNoticias.isEmpty()) return
        if (indiceNoticiaActual !in listaNoticias.indices) return

        val n = listaNoticias[indiceNoticiaActual]

        binding.tituloNoticia.text = n.title.ifBlank { "Sin título" }
        binding.fechaNoticia2.text = n.pubDate
        binding.creadorNoticia2.text = n.creator

        val texto = "Pincha aquí para ir al enlace de la noticia!"
        val span = SpannableString(texto).apply {
            this[0, length] = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (n.link.isNotBlank()) {
                        runCatching {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(n.link)))
                        }
                    }
                    // Puedes mostrar error si falla, pero lo omitimos para simplificar
                }
                override fun updateDrawState(ds: android.text.TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLUE
                    ds.isUnderlineText = true
                }
            }
        }
        binding.enlaceNoticia.text = span
        binding.enlaceNoticia.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun actualizarEstadoBotones() {
        if (listaNoticias.isEmpty()) {
            binding.btnAnterior.isEnabled = false
            binding.btnSiguiente.isEnabled = false
            binding.btnGuardarNoticia.isEnabled = false
            return
        }
        binding.btnAnterior.isEnabled = indiceNoticiaActual > 0
        binding.btnSiguiente.isEnabled = indiceNoticiaActual < listaNoticias.lastIndex
        binding.btnGuardarNoticia.isEnabled = true
    }

    private fun guardarNoticiaActual() {
        val user = auth.currentUser
        if (user == null) {
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
            mostrarToastPersonalizado("¡Usuario no autenticado!", icon)
            return
        }
        if (listaNoticias.isEmpty()) {
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline)
            mostrarToastPersonalizado("¡No hay noticia para guardar!", icon)
            return
        }
        vm.save(user.uid, listaNoticias[indiceNoticiaActual])
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
