package com.github.wizerapp.repository

import com.github.wizerapp.model.Quiz
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class QuizRepository {
    private val firestore = Firebase.firestore

    init {
        // For testing on the Android emulator, use "10.0.2.2"
        firestore.useEmulator("10.0.2.2", 8080)
    }

    suspend fun generateQuizFromExercises(subject: String, count: Int, title: String): Result<Quiz> {
        return try {
            val querySnapshot = firestore.collection("exercises")
                .whereEqualTo("subject", subject)
                .get()
                .await()
            val allExercises = querySnapshot.toObjects(com.github.wizerapp.model.Exercise::class.java)
            if (allExercises.isEmpty()) {
                return Result.failure(Exception("No exercises found for subject: $subject"))
            }
            val selectedExercises = allExercises.shuffled().take(count)
            val newQuizRef = firestore.collection("quizzes").document()
            val qrCode = java.util.UUID.randomUUID().toString()  // Unique code for QR code generation
            val quizData = hashMapOf(
                "id" to newQuizRef.id,
                "title" to title,
                "exercises" to selectedExercises.map { it.toMap() },
                "qrCode" to qrCode,
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            newQuizRef.set(quizData).await()
            val quiz = Quiz(
                id = newQuizRef.id,
                title = title,
                exercises = selectedExercises,
                qrCode = qrCode,
                createdAt = com.google.firebase.Timestamp.now()
            )
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuizById(quizId: String): Result<Quiz> {
        return try {
            val document = firestore.collection("quizzes").document(quizId).get().await()
            if (document.exists()) {
                val quiz = document.toObject(Quiz::class.java)
                quiz?.let { Result.success(it) } ?: Result.failure(Exception("Failed to parse quiz"))
            } else {
                Result.failure(Exception("Quiz not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
