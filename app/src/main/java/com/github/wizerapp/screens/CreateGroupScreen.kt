package com.github.wizerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.github.wizerapp.repository.GroupRepository

@Composable
fun CreateGroupScreen(groupRepository: GroupRepository = GroupRepository()) {
    var groupName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var minStudentsText by remember { mutableStateOf("") }
    var maxStudentsText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Criar Grupo", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Nome do Grupo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Matéria") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = minStudentsText,
                onValueChange = { minStudentsText = it },
                label = { Text("Mínimo de Alunos") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = maxStudentsText,
                onValueChange = { maxStudentsText = it },
                label = { Text("Máximo de Alunos") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val minStudents = minStudentsText.toIntOrNull() ?: 0
                    val maxStudents = maxStudentsText.toIntOrNull() ?: 0

                    coroutineScope.launch {
                        val result = groupRepository.createGroup(groupName, minStudents, maxStudents, subject)
                        val message = if (result.isSuccess) {
                            result.getOrNull() ?: "Grupo criado com sucesso!"
                        } else {
                            result.exceptionOrNull()?.message ?: "Erro ao criar grupo"
                        }
                        snackbarHostState.showSnackbar(message)
                    }


                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Criar Grupo") }
        }
    }
}
