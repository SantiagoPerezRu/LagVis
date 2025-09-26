package com.example.lagvis_v1.auth

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class GoogleUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

class GoogleAuthUiClient(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val cm by lazy { CredentialManager.create(context) }

    suspend fun signIn(): Result<GoogleUser> = runCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(com.example.lagvis_v1.R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response: GetCredentialResponse = cm.getCredential(context, request)
        val credential: Credential = response.credential

        require(credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Tipo de credential no soportado: ${credential::class.java.simpleName}"
        }

        // ¡OJO! A createFrom se le pasa el Bundle (credential.data), no el Credential entero
        val googleToken = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleToken.idToken ?: error("idToken nulo")

        val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(firebaseCred).await()
        val user = authResult.user ?: error("Usuario Firebase nulo")

        GoogleUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString()
        )
    }
}

suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
        addOnCanceledListener { cont.cancel() }
    }
