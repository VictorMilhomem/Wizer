package com.github.wizerapp.viewmodels

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Estados observáveis - removendo 'private set'
    var currentUser by mutableStateOf<FirebaseUser?>(null)

    var isLoading by mutableStateOf(false)

    // Eventos de UI
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // Inicialização
    init {
        // Verificar se há um usuário autenticado
        currentUser = Firebase.auth.currentUser
    }

    fun signInWithGoogle(intent: Intent, launcher: ActivityResultLauncher<Intent>) {
        isLoading = true
        try {
            launcher.launch(intent)
        } catch (e: Exception) {
            isLoading = false
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.Error("Erro ao iniciar login: ${e.message}"))
            }
        }
    }

    // Função para fazer logout
    fun signOut() {
        isLoading = true
        try {
            Firebase.auth.signOut()
            currentUser = null
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.Success("Logout realizado com sucesso"))
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.Error("Erro ao fazer logout: ${e.message}"))
            }
        } finally {
            isLoading = false
        }
    }

    // Função para verificar o estado da autenticação atual
    fun checkAuthState() {
        currentUser = Firebase.auth.currentUser
    }

    // Método público para emitir eventos
    fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    // Sealed class para eventos de UI
    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}