package com.github.wizerapp.repository

import android.net.Uri
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VideoRepository {
    private val storage = Firebase.storage

    /**
     * Faz o upload do vídeo para o Firebase Storage.
     * @param videoUri: URI do vídeo selecionado.
     * @param exerciseId: ID do exercício ao qual o vídeo se relaciona.
     * @return Result contendo a URL de download ou uma exceção.
     */
    suspend fun uploadVideo(videoUri: Uri, exerciseId: String): Result<String> {
        return try {
            // Gera um nome único para o arquivo, combinando o exerciseId e um UUID
            val fileName = "$exerciseId-${UUID.randomUUID()}"
            // Cria uma referência para o local de armazenamento do vídeo
            val videoRef = storage.reference.child("videos/$fileName")
            // Faz o upload do arquivo
            videoRef.putFile(videoUri).await()
            // Obtém a URL de download
            val downloadUrl = videoRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
