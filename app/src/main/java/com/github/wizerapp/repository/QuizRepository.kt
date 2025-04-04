package com.github.wizerapp.repository

import com.github.wizerapp.model.Exercise
import com.github.wizerapp.model.Quiz
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import java.util.UUID

class QuizRepository {
    private val firestore = Firebase.firestore

    init {
        // Se estiver testando no emulador Android, use "10.0.2.2"
        firestore.useEmulator("10.0.2.2", 8080)
    }

    suspend fun generateQuizFromExercises(subject: String, count: Int, title: String): Result<Quiz> {
        return try {
            // Consulta os exercícios que correspondem à matéria
            val querySnapshot = firestore.collection("exercises")
                .whereEqualTo("subject", subject)
                .get()
                .await()
            val allExercises = querySnapshot.toObjects(Exercise::class.java)
            if (allExercises.isEmpty()) {
                return Result.failure(Exception("Nenhum exercício encontrado para a matéria: $subject"))
            }
            // Embaralha e seleciona a quantidade desejada (ou todos, se count for maior que o total)
            val selectedExercises = allExercises.shuffled().take(count)
            // Cria um novo documento para o quiz
            val newQuizRef = firestore.collection("quizzes").document()
            val qrCode = UUID.randomUUID().toString()  // Código único para gerar o QR Code posteriormente
            val quizData = hashMapOf(
                "id" to newQuizRef.id,
                "title" to title,
                "exercises" to selectedExercises.map { it.toMap() },
                "qrCode" to qrCode,
                "createdAt" to FieldValue.serverTimestamp()
            )
            // Salva o quiz no Firestore
            newQuizRef.set(quizData).await()

            // Cria o objeto Quiz para retornar (o campo createdAt pode ser atualizado depois)
            val quiz = Quiz(
                id = newQuizRef.id,
                title = title,
                exercises = selectedExercises,
                qrCode = qrCode,
                createdAt = Timestamp.now()
            )
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
