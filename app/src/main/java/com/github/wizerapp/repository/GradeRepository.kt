package com.github.wizerapp.repository

import android.util.Log
import com.github.wizerapp.model.Grade
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class GradeRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = db.collection("grades")

    suspend fun addGrade(grade: Grade): Result<String> {
        return try {
            val gradeData = hashMapOf(
                "studentId" to grade.studentId,
                "exerciseId" to grade.exerciseId,
                "studentAnswer" to grade.studentAnswer,
                "score" to grade.score,
                "submittedAt" to grade.submittedAt
            )

            val documentReference = collection.add(gradeData).await()
            Log.d("Firestore", "Grade saved with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving grade", e)
            Result.failure(e)
        }
    }

    suspend fun getGrade(gradeId: String): Result<Grade> {
        return try {
            val document = collection.document(gradeId).get().await()
            if (document.exists()) {
                val grade = document.toObject<Grade>()
                grade?.let { Result.success(it) } ?: Result.failure(Exception("Invalid Grade data"))
            } else {
                Result.failure(Exception("Grade not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGrade(gradeId: String, newScore: Int): Result<Unit> {
        return try {
            collection.document(gradeId).update("score", newScore).await()
            Log.d("Firestore", "Grade updated to $newScore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating grade", e)
            Result.failure(e)
        }
    }

    suspend fun deleteGrade(gradeId: String): Result<Unit> {
        return try {
            collection.document(gradeId).delete().await()
            Log.d("Firestore", "Grade deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting grade", e)
            Result.failure(e)
        }
    }

    suspend fun getGradesByStudent(studentId: String): Result<List<Grade>> {
        return try {
            val result = collection.whereEqualTo("studentId", studentId).get().await()
            val grades = result.documents.mapNotNull { it.toObject<Grade>() }
            Result.success(grades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
