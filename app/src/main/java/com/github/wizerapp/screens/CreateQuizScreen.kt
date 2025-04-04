package com.github.wizerapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.wizerapp.model.Quiz
import com.github.wizerapp.repository.QuizRepository
import com.github.wizerapp.utils.generateQRCodeBitmap
import kotlinx.coroutines.launch

@Composable
fun CreateQuizScreen(quizRepository: QuizRepository = QuizRepository()) {
    var subject by remember { mutableStateOf("") }
    var countText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var generatedQuiz by remember { mutableStateOf<Quiz?>(null) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Gerar Quiz a partir de Exercícios", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Matéria") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = countText,
                onValueChange = { countText = it },
                label = { Text("Número de Exercícios") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título do Quiz") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val count = countText.toIntOrNull() ?: 0
                    if (subject.isNotBlank() && count > 0 && title.isNotBlank()) {
                        coroutineScope.launch {
                            val result = quizRepository.generateQuizFromExercises(subject, count, title)
                            result.onSuccess { quiz ->
                                generatedQuiz = quiz
                                snackbarHostState.showSnackbar("Quiz gerado com sucesso!")
                            }.onFailure { e ->
                                snackbarHostState.showSnackbar("Erro: ${e.message}")
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Preencha todos os campos corretamente.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gerar Quiz")
            }
            Spacer(modifier = Modifier.height(16.dp))
            generatedQuiz?.let { quiz ->
                Text("Quiz Gerado:", style = MaterialTheme.typography.titleSmall)
                Text("Título: ${quiz.title}")
                Text("QR Code: ${quiz.qrCode}")
                // Aqui, geramos e exibimos o QR Code:
                val qrBitmap = generateQRCodeBitmap(quiz.qrCode)
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code do Quiz",
                        modifier = Modifier.size(300.dp)
                    )
                }
                LazyColumn {
                    items(quiz.exercises) { exercise ->
                        Text("Exercício: ${exercise.title}")
                    }
                }
            }
        }
    }
}
