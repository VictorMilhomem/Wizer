package com.github.wizerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.wizerapp.model.Doubt
import com.github.wizerapp.repository.DoubtRepository
import kotlinx.coroutines.launch

@Composable
fun DoubtsScreen(doubtRepository: DoubtRepository = DoubtRepository()) {
    var doubts by remember { mutableStateOf<List<Doubt>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Carrega as dúvidas na inicialização
    LaunchedEffect(Unit) {
        try {
            doubts = doubtRepository.getDoubts()
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Erro ao carregar dúvidas: ${e.message}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(doubts) { doubt ->
                DoubtItem(doubt = doubt, onAnswer = { solution ->
                    coroutineScope.launch {
                        // Aqui, "ProfessorID" pode ser substituído pelo ID real do professor autenticado
                        val result = doubtRepository.updateDoubtSolution(doubt.id, solution, "ProfessorID")
                        snackbarHostState.showSnackbar(result.getOrElse { "Erro: ${it.message}" })
                        // Atualiza a lista após a resposta
                        doubts = doubtRepository.getDoubts()
                    }
                })
            }
        }
    }
}

@Composable
fun DoubtItem(doubt: Doubt, onAnswer: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Aluno: ${doubt.studentId}", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dúvida: ${doubt.text}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Criada em: ${doubt.createdAt}")
            if (doubt.resolved) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Resolvido por: ${doubt.resolvedBy}")
                Text(text = "Solução: ${doubt.solution}")
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDialog = true }) {
                    Text("Responder")
                }
            }
        }
    }

    if (showDialog) {
        var solutionText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Responder Dúvida") },
            text = {
                OutlinedTextField(
                    value = solutionText,
                    onValueChange = { solutionText = it },
                    label = { Text("Digite a resposta") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAnswer(solutionText)
                    showDialog = false
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
