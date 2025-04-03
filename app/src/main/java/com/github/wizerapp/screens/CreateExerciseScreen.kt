package com.github.wizerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.wizerapp.repository.ExerciseRepository
import kotlinx.coroutines.launch

@Composable
fun CreateExerciseScreen(exerciseRepository: ExerciseRepository = ExerciseRepository()) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // Para simplificar, o usuário insere as opções separadas por vírgula
    var optionsText by remember { mutableStateOf("") }
    var correctText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Criar Exercício", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = optionsText,
                onValueChange = { optionsText = it },
                label = { Text("Opções (separadas por vírgula)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = correctText,
                onValueChange = { correctText = it },
                label = { Text("Índice da opção correta (número)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val options = optionsText.split(",").map { it.trim() }
                    val correct = correctText.toIntOrNull() ?: 0
                    coroutineScope.launch {
                        val result = exerciseRepository.createExercise(title, description, options, correct)
                        snackbarHostState.showSnackbar(
                            result.getOrElse { it.message ?: "Erro desconhecido" }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Exercício")
            }
        }
    }
}
