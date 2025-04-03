package com.github.wizerapp.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ExerciseRepository {
    private val firestore = Firebase.firestore

    init {
        // Configura o emulador; em um dispositivo Android, use "10.0.2.2" para apontar para o localhost do computador.
        firestore.useEmulator("10.0.2.2", 8080)
    }

    suspend fun createExercise(
        title: String,
        description: String,
        options: List<String>,
        correct: Int
    ): Result<String> {
        return try {
            // Cria um novo documento para o exercício
            val newExerciseRef = firestore.collection("exercises").document()

            // Monta os dados do exercício, usando FieldValue.serverTimestamp() para o campo createdAt
            val exerciseData = hashMapOf(
                "id" to newExerciseRef.id,
                "title" to title,
                "description" to description,
                "options" to options,
                "correct" to correct,
                "createdAt" to FieldValue.serverTimestamp()
            )

            // Salva os dados no Firestore
            newExerciseRef.set(exerciseData).await()
            Result.success("Exercício criado com sucesso!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
