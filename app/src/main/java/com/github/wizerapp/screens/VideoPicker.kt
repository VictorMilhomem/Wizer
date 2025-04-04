package com.github.wizerapp.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun VideoPicker(onVideoSelected: (Uri?) -> Unit) {
    // Cria um launcher para o seletor de arquivos filtrando por vídeos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onVideoSelected(uri)
    }

    Button(onClick = { launcher.launch("video/*") }) {
        Text("Selecionar Vídeo")
    }
}
