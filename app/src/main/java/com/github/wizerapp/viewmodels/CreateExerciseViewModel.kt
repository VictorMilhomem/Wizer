package com.github.wizerapp.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.wizerapp.model.Exercise
import com.github.wizerapp.repository.ExerciseRepository
import com.github.wizerapp.repository.VideoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CreateExerciseViewModel(
    private val exerciseRepository: ExerciseRepository = ExerciseRepository(),
    private val videoRepository: VideoRepository = VideoRepository()
) : ViewModel() {
    // Estados do exercício
    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var subject by mutableStateOf("")
        private set

    var optionsText by mutableStateOf("")
        private set

    var correctText by mutableStateOf("")
        private set

    // Estados da resolução
    var resolutionText by mutableStateOf("")
        private set

    var resolutionVideoUrl by mutableStateOf<String?>(null)
        private set

    var selectedVideoUri by mutableStateOf<Uri?>(null)
        private set

    var isUploading by mutableStateOf(false)
        private set

    var isCreatingExercise by mutableStateOf(false)
        private set

    // Eventos de UI
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // Funções para atualizar estados
    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
    }

    fun updateSubject(newSubject: String) {
        subject = newSubject
    }

    fun updateOptionsText(newOptions: String) {
        optionsText = newOptions
    }

    fun updateCorrectText(newCorrect: String) {
        correctText = newCorrect
    }

    fun updateResolutionText(newResolution: String) {
        resolutionText = newResolution
    }

    fun updateSelectedVideo(uri: Uri?) {
        selectedVideoUri = uri
    }

    // Função para fazer upload do vídeo
    fun uploadVideo(tempExerciseId: String = "EXERCISE_TEMP_ID") {
        val videoUri = selectedVideoUri ?: return

        viewModelScope.launch {
            isUploading = true
            try {
                val result = videoRepository.uploadVideo(videoUri, tempExerciseId)
                result.onSuccess { videoUrl ->
                    resolutionVideoUrl = videoUrl
                    _uiEvent.emit(UiEvent.VideoUploaded("Vídeo enviado com sucesso!"))
                }.onFailure { e ->
                    _uiEvent.emit(UiEvent.Error("Erro no upload: ${e.message}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error("Erro ao fazer upload: ${e.message}"))
            } finally {
                isUploading = false
            }
        }
    }

    // Função para criar o exercício
    fun createExercise() {
        if (!validateInputs()) {
            return
        }

        val options = optionsText.split(",").map { it.trim() }
        val correct = correctText.toIntOrNull() ?: 0

        viewModelScope.launch {
            isCreatingExercise = true
            try {
                val result = exerciseRepository.createExercise(
                    title,
                    description,
                    subject,
                    options,
                    correct
                )

                if (result.isSuccess) {
                    _uiEvent.emit(UiEvent.Success("Exercício criado com sucesso!"))
                    clearInputs()
                } else {
                    _uiEvent.emit(UiEvent.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error("Erro ao criar exercício: ${e.message}"))
            } finally {
                isCreatingExercise = false
            }
        }
    }

    private fun validateInputs(): Boolean {
        when {
            title.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Título não pode ser vazio"))
                }
                return false
            }
            description.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Descrição não pode ser vazia"))
                }
                return false
            }
            subject.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Matéria não pode ser vazia"))
                }
                return false
            }
            optionsText.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Opções não podem ser vazias"))
                }
                return false
            }
            correctText.isBlank() || correctText.toIntOrNull() == null -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Índice da opção correta inválido"))
                }
                return false
            }
        }
        return true
    }

    private fun clearInputs() {
        title = ""
        description = ""
        subject = ""
        optionsText = ""
        correctText = ""
        resolutionText = ""
        resolutionVideoUrl = null
        selectedVideoUri = null
    }

    // Sealed class para eventos de UI
    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
        data class VideoUploaded(val message: String) : UiEvent()
    }
}