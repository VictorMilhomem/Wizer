package com.github.wizerapp.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.wizerapp.repository.ExerciseRepository
import com.github.wizerapp.repository.VideoRepository
import kotlinx.coroutines.launch

@Composable
fun CreateExerciseScreen(
    // Você pode adicionar outros parâmetros ou repositórios se necessário
    onExerciseCreated: () -> Unit = {}
) {
    // Estados para os campos do exercício
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var optionsText by remember { mutableStateOf("") } // opções separadas por vírgula
    var correctText by remember { mutableStateOf("") } // índice da resposta correta

    // Estados para resolução
    var resolutionText by remember { mutableStateOf("") }
    var resolutionVideoUrl by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // VideoRepository para upload do vídeo
    val videoRepository = VideoRepository()

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
            // Campos básicos do exercício
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título do Exercício") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição do Exercício") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Matéria") },
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
                label = { Text("Índice da opção correta") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()

            // Seção para adicionar resolução em texto
            Text("Adicionar Resolução (Texto):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = resolutionText,
                onValueChange = { resolutionText = it },
                label = { Text("Explicação da Resolução") },
                modifier = Modifier.fillMaxWidth()
            )

            // Seção para upload do vídeo de resolução
            Text("Upload do Vídeo de Resolução:", style = MaterialTheme.typography.titleMedium)
            // Usa o composable VideoPicker ou UploadVideoSection para selecionar e enviar o vídeo
            var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
            VideoPicker { uri ->
                selectedVideoUri = uri
            }
            selectedVideoUri?.let { uri ->
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val result = videoRepository.uploadVideo(uri, exerciseId = "EXERCISE_TEMP_ID")
                            result.onSuccess { videoUrl ->
                                resolutionVideoUrl = videoUrl
                                snackbarHostState.showSnackbar("Vídeo enviado com sucesso!")
                            }.onFailure { e ->
                                snackbarHostState.showSnackbar("Erro no upload: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fazer Upload do Vídeo")
                }
            }

            // Botão para criar o exercício (salvar todos os dados no Firestore)
            Button(
                onClick = {
                    val options = optionsText.split(",").map { it.trim() }
                    val correct = correctText.toIntOrNull() ?: 0
                    coroutineScope.launch {
                        val exerciseRepository = ExerciseRepository()
                        val result = exerciseRepository.createExercise(title, description, subject,options, correct)
                        snackbarHostState.showSnackbar(
                            result.getOrElse { it.message ?: "Erro desconhecido" }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Exercício")
            }

            resolutionVideoUrl?.let {
                Text("Vídeo de Resolução: $it")
            }
        }
    }
}
