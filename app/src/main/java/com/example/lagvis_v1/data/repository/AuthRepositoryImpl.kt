// data/repository/AuthRepositoryImplKt.kt
package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.dominio.repositorio.auth.AuthRepositoryKt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepositoryImplKt(
    private val auth: FirebaseAuth
) : AuthRepositoryKt {

    override fun currentUser(): FirebaseUser? = auth.currentUser

    override fun isSignedIn(): Boolean = auth.currentUser != null

    override fun signOut() { auth.signOut() }

    override fun uidOrNull(): String? = auth.currentUser?.uid

    override fun currentEmail(): String? = auth.currentUser?.email

    override fun signIn(email: String, password: String, cb: AuthRepositoryKt.AuthCallback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { t ->
            if (t.isSuccessful) cb.onSuccess()
            else cb.onError(t.exception?.message ?: "Error de autenticación")
        }
    }

    override fun sendPasswordReset(email: String, cb: AuthRepositoryKt.AuthCallback) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { t ->
            if (t.isSuccessful) cb.onSuccess()
            else cb.onError(t.exception?.message ?: "No se pudo enviar el email")
        }
    }

    override fun signUp(email: String, password: String, cb: AuthRepositoryKt.AuthCallback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { t ->
            if (t.isSuccessful) cb.onSuccess()
            else cb.onError(t.exception?.message ?: "Registro fallido")
        }
    }

    override fun sendEmailVerification(cb: AuthRepositoryKt.AuthCallback) {
        val u = auth.currentUser ?: return cb.onError("Usuario no autenticado")
        u.sendEmailVerification().addOnCompleteListener { t ->
            if (t.isSuccessful) cb.onSuccess()
            else cb.onError(t.exception?.message ?: "No se pudo enviar verificación")
        }
    }
}
