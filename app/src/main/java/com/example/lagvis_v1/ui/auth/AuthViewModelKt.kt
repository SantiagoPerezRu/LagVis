// ui/auth/AuthViewModelKt.kt
package com.example.lagvis_v1.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.repositorio.AuthRepositoryKt

class AuthViewModelKt(
    private val repo: AuthRepositoryKt
) : ViewModel() {

    private val _signedIn = MutableLiveData<Boolean>()
    val signedIn: LiveData<Boolean> = _signedIn
    fun refresh() { _signedIn.postValue(repo.isSignedIn()) }
    fun uidOrNull(): String? = repo.uidOrNull()
    fun currentEmailOrNull(): String? = repo.currentEmail()
    fun logout() { repo.signOut(); refresh() }

    private val _reset = MutableLiveData<UiState<Void>>()
    val reset: LiveData<UiState<Void>> = _reset

    private val _login = MutableLiveData<UiState<Void>>()
    val login: LiveData<UiState<Void>> = _login

    private val _signup = MutableLiveData<UiState<Void>>()
    val signup: LiveData<UiState<Void>> = _signup

    fun signIn(email: String, password: String) {
        _login.postValue(UiState.Loading())
        repo.signIn(email, password, object : AuthRepositoryKt.AuthCallback {
            override fun onSuccess() { _login.postValue(UiState.Success(null)); refresh() }
            override fun onError(msg: String) { _login.postValue(UiState.Error(msg)) }
        })
    }

    fun resetPassword(email: String) {
        _reset.postValue(UiState.Loading())
        repo.sendPasswordReset(email, object : AuthRepositoryKt.AuthCallback {
            override fun onSuccess() { _reset.postValue(UiState.Success(null)) }
            override fun onError(msg: String) { _reset.postValue(UiState.Error(msg)) }
        })
    }

    fun signUp(email: String, password: String) {
        _signup.postValue(UiState.Loading())
        repo.signUp(email, password, object : AuthRepositoryKt.AuthCallback {
            override fun onSuccess() {
                repo.sendEmailVerification(object : AuthRepositoryKt.AuthCallback {
                    override fun onSuccess() { _signup.postValue(UiState.Success(null)) }
                    override fun onError(msg: String) { _signup.postValue(UiState.Error(msg)) }
                })
            }
            override fun onError(msg: String) { _signup.postValue(UiState.Error(msg)) }
        })
    }

    fun resetPasswordForCurrentUser() {
        val email = repo.currentEmail()
        if (email == null) {
            _reset.postValue(UiState.Error("Sin email de usuario"))
        } else resetPassword(email)
    }
}
