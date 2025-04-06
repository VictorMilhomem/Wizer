package com.github.wizerapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.wizerapp.model.Exercise
import com.github.wizerapp.model.Quiz
import com.github.wizerapp.repository.QuizRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CreateQuizViewModel(
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {
    // Estados observáveis
    var subject by mutableStateOf("")
        private set

    var countText by mutableStateOf("")
        private set

    var title by mutableStateOf("")
        private set

    var generatedQuiz by mutableStateOf<Quiz?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Eventos de UI
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // Funções para atualizar os estados
    fun updateSubject(newSubject: String) {
        subject = newSubject
    }

    fun updateCount(newCount: String) {
        countText = newCount
    }

    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    // Função para gerar o quiz
    fun generateQuiz() {
        val count = countText.toIntOrNull() ?: 0

        if (!validateInputs(count)) {
            return
        }

        viewModelScope.launch {
            isLoading = true

            try {
                val result = quizRepository.generateQuizFromExercises(subject, count, title)

                result.onSuccess { quiz ->
                    generatedQuiz = quiz
                    _uiEvent.emit(UiEvent.Success("Quiz gerado com sucesso!"))
                }.onFailure { e ->
                    _uiEvent.emit(UiEvent.Error("Erro ao gerar quiz: ${e.message}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error("Erro ao gerar quiz: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateInputs(count: Int): Boolean {
        when {
            subject.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Matéria não pode ser vazia"))
                }
                return false
            }
            count <= 0 -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Número de exercícios deve ser maior que zero"))
                }
                return false
            }
            title.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Título do quiz não pode ser vazio"))
                }
                return false
            }
        }
        return true
    }

    fun clearInputs() {
        subject = ""
        countText = ""
        title = ""
        generatedQuiz = null
    }

    // Sealed class para eventos de UI
    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}