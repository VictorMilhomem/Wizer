package com.github.wizerapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.wizerapp.repository.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CreateGroupViewModel(
    private val groupRepository: GroupRepository = GroupRepository(),
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {
    // Estados observáveis
    var groupName by mutableStateOf("")
        private set

    var subject by mutableStateOf("")
        private set

    var minStudentsText by mutableStateOf("")
        private set

    var maxStudentsText by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Eventos de UI
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // Funções para atualizar os estados
    fun updateGroupName(name: String) {
        groupName = name
    }

    fun updateSubject(newSubject: String) {
        subject = newSubject
    }

    fun updateMinStudents(min: String) {
        minStudentsText = min
    }

    fun updateMaxStudents(max: String) {
        maxStudentsText = max
    }

    // Função para criar o grupo
    fun createGroup() {
        val minStudents = minStudentsText.toIntOrNull() ?: 0
        val maxStudents = maxStudentsText.toIntOrNull() ?: 0

        if (!validateInputs(minStudents, maxStudents)) {
            return
        }

        viewModelScope.launch {
            isLoading = true

            try {
                val result = groupRepository.createGroup(
                    groupName,
                    minStudents,
                    maxStudents,
                    subject
                )

                if (result.isSuccess) {
                    _uiEvent.emit(UiEvent.Success(result.getOrNull() ?: "Grupo criado com sucesso!"))
                    clearInputs()
                } else {
                    _uiEvent.emit(UiEvent.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error("Erro ao criar grupo: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateInputs(minStudents: Int, maxStudents: Int): Boolean {
        when {
            groupName.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Nome do grupo não pode ser vazio"))
                }
                return false
            }
            subject.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Matéria não pode ser vazia"))
                }
                return false
            }
            minStudents <= 0 -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Mínimo de alunos deve ser maior que zero"))
                }
                return false
            }
            maxStudents <= 0 -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Máximo de alunos deve ser maior que zero"))
                }
                return false
            }
            minStudents > maxStudents -> {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.Error("Mínimo de alunos não pode ser maior que o máximo"))
                }
                return false
            }
        }
        return true
    }

    private fun clearInputs() {
        groupName = ""
        subject = ""
        minStudentsText = ""
        maxStudentsText = ""
    }

    // Sealed class para eventos de UI
    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}