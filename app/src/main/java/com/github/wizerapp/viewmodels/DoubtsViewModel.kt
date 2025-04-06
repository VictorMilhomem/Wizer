package com.github.wizerapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.wizerapp.model.Doubt
import com.github.wizerapp.repository.DoubtRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class DoubtsViewModel(
    private val doubtRepository: DoubtRepository = DoubtRepository()
) : ViewModel() {
    // Estados observáveis
    var doubts by mutableStateOf<List<Doubt>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Eventos de UI
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // Carregar dúvidas ao inicializar o ViewModel
    init {
        loadDoubts()
    }

    // Função para carregar dúvidas
    fun loadDoubts() {
        viewModelScope.launch {
            isLoading = true

            try {
                doubts = doubtRepository.getDoubts()
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error("Erro ao carregar dúvidas: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    // Função para responder uma dúvida
    fun answerDoubt(doubtId: String, solution: String, professorId: String? = Firebase.auth.currentUser?.uid) {
        // Se não tiver ID de professor, usa o padrão para testes
        val finalProfessorId = professorId ?: "ProfessorID"

        if (solution.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.Error("A resposta não pode ser vazia"))
            }
            return
        }

        viewModelScope.launch {
            isLoading = true

            try {
                val result = doubtRepository.updateDoubtSolution(doubtId, solution, finalProfessorId)

                if (result.isSuccess) {
                    _uiEvent.emit(UiEvent.Success("Dúvida respondida com sucesso!"))
                    // Recarregar a lista de dúvidas após responder
                    loadDoubts()
                } else {
                    _uiEvent.emit(UiEvent.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error("Erro ao responder dúvida: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    // Sealed class para eventos de UI
    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}