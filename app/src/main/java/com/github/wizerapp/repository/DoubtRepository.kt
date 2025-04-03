package com.github.wizerapp.repository

import com.github.wizerapp.model.Doubt
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DoubtRepository {
    private val firestore = Firebase.firestore

    init {
        // Configura o emulador para testes, se necessário (no Android Emulator use "10.0.2.2")
        firestore.useEmulator("10.0.2.2", 8080)
    }

    // Função para obter todas as dúvidas
    suspend fun getDoubts(): List<Doubt> {
        return firestore.collection("doubts")
            .get()
            .await()
            .toObjects(Doubt::class.java)
    }

    // Função para atualizar a dúvida com a resposta do professor
    suspend fun updateDoubtSolution(
        doubtId: String,
        solution: String,
        resolvedBy: String
    ): Result<String> {
        return try {
            firestore.collection("doubts")
                .document(doubtId)
                .update(
                    mapOf(
                        "solution" to solution,
                        "resolved" to true,
                        "resolvedBy" to resolvedBy,
                        "createdAt" to FieldValue.serverTimestamp() // opcional, se quiser atualizar a data de resposta
                    )
                )
                .await()
            Result.success("Dúvida respondida com sucesso!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
