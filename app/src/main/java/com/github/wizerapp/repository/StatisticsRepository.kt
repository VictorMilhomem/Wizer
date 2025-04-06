package com.github.wizerapp.repository

import com.github.wizerapp.model.Exercise
import com.github.wizerapp.model.ExercisesStatistics
import com.github.wizerapp.model.Grade
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class StatisticsRepository {
    private val firestore = Firebase.firestore

    suspend fun getStatisticsForSubject(subject: String): Result<List<ExercisesStatistics>> {
        return try {
            // Consulta os exercícios que pertencem à matéria informada
            val exerciseQuery = firestore.collection("exercises")
                .whereEqualTo("subject", subject)
                .get()
                .await()

            // Converte os documentos em objetos Exercise, garantindo que o id seja definido
            val exercises = exerciseQuery.documents.mapNotNull { document ->
                document.toObject(Exercise::class.java)?.copy(id = document.id)
            }

            // Cria uma lista para armazenar as estatísticas de cada exercício
            val statsList = mutableListOf<ExercisesStatistics>()

            // Para cada exercício, consulta os documentos na coleção "grades"
            for (exercise in exercises) {
                val gradeQuery = firestore.collection("grades")
                    .whereEqualTo("exerciseId", exercise.id)
                    .get()
                    .await()

                var correctCount = 0
                var incorrectCount = 0

                // Para cada grade, compara a resposta do aluno com a resposta correta do exercício
                for (gradeDoc in gradeQuery.documents) {
                    val grade = gradeDoc.toObject(Grade::class.java)
                    if (grade != null) {
                        if (grade.studentAnswer == exercise.correct) {
                            correctCount++
                        } else {
                            incorrectCount++
                        }
                    }
                }
                statsList.add(
                    ExercisesStatistics(
                        exerciseId = exercise.id,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount
                    )
                )
            }
            Result.success(statsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
