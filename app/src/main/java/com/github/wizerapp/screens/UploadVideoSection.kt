package com.github.wizerapp.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.wizerapp.repository.VideoRepository
import kotlinx.coroutines.launch

@Composable
fun UploadVideoSection(
    exerciseId: String, // ID do exercício que receberá o vídeo
    videoRepository: VideoRepository = VideoRepository(),
    onUploadComplete: (String) -> Unit // Callback com a URL do vídeo após o upload
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VideoPicker { uri ->
            selectedUri = uri
        }
        if (selectedUri != null) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = videoRepository.uploadVideo(selectedUri!!, exerciseId)
                        result.onSuccess { videoUrl ->
                            snackbarHostState.showSnackbar("Upload realizado com sucesso!")
                            onUploadComplete(videoUrl)
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
        // Exibe um Snackbar para feedback ao usuário
        SnackbarHost(hostState = snackbarHostState)
    }
}
