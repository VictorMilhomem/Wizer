package com.github.wizerapp.viewmodels

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.wizerapp.auth.GoogleSignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Estados observáveis
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Eventos de UI
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // Inicialização
    init {
        // Verificar se há um usuário autenticado
        currentUser = Firebase.auth.currentUser
    }

    // Função para iniciar login via Google
    fun signInWithGoogle(context: Context) {
        if (context is Activity) {
            isLoading = true
            try {
                val intent = GoogleSignInActivity::class.java
                context.startActivity(intent)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Erro ao iniciar login: ${e.message}"))
                }
            } finally {
                isLoading = false
            }
        } else {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.Error("Contexto inválido para login"))
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

    // Sealed class para eventos de UI
    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}