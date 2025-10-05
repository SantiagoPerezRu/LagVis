// ui/auth/AuthViewModelFactoryKt.kt
package com.example.lagvis_v1.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.data.repository.AuthRepositoryImplKt
import com.example.lagvis_v1.dominio.repositorio.AuthRepositoryKt
import com.google.firebase.auth.FirebaseAuth

class AuthViewModelFactoryKt : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo: AuthRepositoryKt = AuthRepositoryImplKt(FirebaseAuth.getInstance())
        return AuthViewModelKt(repo) as T
    }
}
