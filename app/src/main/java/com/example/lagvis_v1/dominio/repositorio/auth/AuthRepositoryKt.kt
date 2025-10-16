// dominio/repositorio/AuthRepositoryKt.kt
package com.example.lagvis_v1.dominio.repositorio.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepositoryKt {
    fun currentUser(): FirebaseUser?            // puede ser null
    fun isSignedIn(): Boolean
    fun signOut()
    fun uidOrNull(): String?
    fun currentEmail(): String?                 // puede devolver null

    interface AuthCallback { fun onSuccess(); fun onError(msg: String) }

    fun signIn(email: String, password: String, cb: AuthCallback)
    fun sendPasswordReset(email: String, cb: AuthCallback)
    fun signUp(email: String, password: String, cb: AuthCallback)
    fun sendEmailVerification(cb: AuthCallback)
}
