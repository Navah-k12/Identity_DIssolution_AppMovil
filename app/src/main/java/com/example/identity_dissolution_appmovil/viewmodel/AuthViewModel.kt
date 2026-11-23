package com.example.identity_dissolution_appmovil.viewmodel

import android.app.Application
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Estado de la sesión de Firebase que ve la pantalla de Ajustes. */
data class AuthUiState(
    /** false si falta app/google-services.json: Firebase no se pudo iniciar. */
    val available: Boolean = true,
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

/**
 * Inicio de sesión con Firebase Authentication (correo y contraseña).
 * FirebaseAuth guarda la sesión en el teléfono: al reabrir la app sigue iniciada.
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {

    // Si el proyecto no tiene google-services.json, Firebase no existe: la app no debe cerrarse por eso.
    private val auth: FirebaseAuth? =
        runCatching { if (FirebaseApp.getApps(app).isEmpty()) null else FirebaseAuth.getInstance() }.getOrNull()

    private val _state = MutableStateFlow(AuthUiState(available = auth != null, email = auth?.currentUser?.email))
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    // Observer: Firebase avisa cuando cambia la sesión (login, logout, token caducado).
    private val listener = FirebaseAuth.AuthStateListener { a -> _state.update { it.copy(email = a.currentUser?.email) } }

    init {
        auth?.addAuthStateListener(listener)
    }

    fun signIn(email: String, password: String) = run(email, password) { a -> a.signInWithEmailAndPassword(email.trim(), password).await() }

    fun register(email: String, password: String) = run(email, password) { a -> a.createUserWithEmailAndPassword(email.trim(), password).await() }

    /**
     * Continuar con Google: Credential Manager muestra la hoja de cuentas de Google del teléfono,
     * devuelve un token y Firebase lo cambia por una sesión. [context] debe ser la Activity.
     */
    fun signInWithGoogle(context: Context) {
        val a = auth ?: return
        val webClientId = webClientId(context)
        if (webClientId == null) {
            _state.update {
                it.copy(error = "Falta activar Google en Firebase (Authentication → Sign-in method) y volver a descargar google-services.json.")
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = runCatching {
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
                    .build()
                val credential = CredentialManager.create(context).getCredential(context, request).credential
                val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                a.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            }
            // Si el usuario cierra la hoja de cuentas no es un error: no se muestra nada.
            val error = result.exceptionOrNull()?.takeIf { it !is GetCredentialCancellationException }?.localizedMessage
            _state.update { it.copy(loading = false, error = error) }
        }
    }

    /**
     * El plugin de Google Services crea R.string.default_web_client_id solo si Google está activado
     * en Firebase. Se busca por nombre para que la app compile aunque todavía no exista.
     */
    private fun webClientId(context: Context): String? {
        val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (id == 0) null else context.getString(id)
    }

    fun signOut() {
        auth?.signOut()
    }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun run(email: String, password: String, block: suspend (FirebaseAuth) -> Unit) {
        val a = auth ?: return
        if (email.isBlank() || password.length < 6) {
            _state.update { it.copy(error = "Escribe tu correo y una contraseña de al menos 6 caracteres.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = runCatching { block(a) }
            _state.update { it.copy(loading = false, error = result.exceptionOrNull()?.localizedMessage) }
        }
    }

    override fun onCleared() {
        auth?.removeAuthStateListener(listener)
    }
}
