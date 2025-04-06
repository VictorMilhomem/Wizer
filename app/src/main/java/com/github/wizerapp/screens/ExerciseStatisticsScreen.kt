package com.github.wizerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.wizerapp.model.ExercisesStatistics
import com.github.wizerapp.repository.StatisticsRepository
import kotlinx.coroutines.launch

@Composable
fun ExerciseStatisticsScreen(
    subject: String, // A matéria para filtrar os exercícios
    statisticsRepository: StatisticsRepository = StatisticsRepository()
) {
    var statistics by remember { mutableStateOf<List<ExercisesStatistics>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(subject) {
        coroutineScope.launch {
            val result = statisticsRepository.getStatisticsForSubject(subject)
            result.onSuccess { stats ->
                statistics = stats
            }.onFailure { e ->
                snackbarHostState.showSnackbar("Erro ao carregar estatísticas: ${e.message}")
            }
        }
    }

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
            Text("Estatísticas para a matéria: $subject", style = MaterialTheme.typography.headlineMedium)
            if (statistics.isEmpty()) {
                Text("Nenhum exercício encontrado ou sem respostas.")
            } else {
                LazyColumn {
                    items(statistics) { stat ->
                        Text("Exercício ID: ${stat.exerciseId}")
                        Text("Acertos: ${stat.correctCount}  -  Erros: ${stat.incorrectCount}")
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
