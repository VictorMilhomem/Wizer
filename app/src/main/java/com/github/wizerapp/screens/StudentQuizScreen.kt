package com.github.wizerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.wizerapp.model.Quiz
import com.github.wizerapp.repository.QuizRepository
import kotlinx.coroutines.launch

@Composable
fun StudentQuizScreen(
    quizId: String,
    quizRepository: QuizRepository = QuizRepository()
) {
    var quiz by remember { mutableStateOf<Quiz?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Load quiz data when quizId changes
    LaunchedEffect(quizId) {
        coroutineScope.launch {
            val result = quizRepository.getQuizById(quizId)
            result.onSuccess { fetchedQuiz ->
                quiz = fetchedQuiz
            }.onFailure { e ->
                snackbarHostState.showSnackbar("Error: ${e.message}")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (quiz == null) {
                // Show loading indicator while fetching data
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = quiz!!.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn {
                        items(quiz!!.exercises) { exercise ->
                            Text(
                                text = "Exercise: ${exercise.title}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            exercise.options.forEachIndexed { index, option ->
                                Text(
                                    text = "${'A' + index}: $option",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
