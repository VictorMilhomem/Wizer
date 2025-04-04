package com.github.wizerapp.repository

import com.github.wizerapp.model.Student
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class StudentRepository(private val db: FirebaseFirestore) {

    private val studentsRef = db.collection("students")
    private val groupsRef = db.collection("groups")

    suspend fun createStudent(student: Student): Result<Unit> {
        return try {
            studentsRef.document(student.id).set(student).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentById(studentId: String): Result<Student> {
        return try {
            val snapshot = studentsRef.document(studentId).get().await()
            val student = snapshot.toObject<Student>()
            if (student != null) Result.success(student)
            else Result.failure(Exception("Student not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(studentId: String, updatedData: Map<String, Any>): Result<Unit> {
        return try {
            studentsRef.document(studentId).update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(studentId: String): Result<Unit> {
        return try {
            studentsRef.document(studentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllStudents(): Result<List<Student>> {
        return try {
            val snapshot = studentsRef.get().await()
            val students = snapshot.documents.mapNotNull { it.toObject<Student>() }
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignStudentToRandomGroup(studentId: String): Result<Unit> {
        return try {
            val groupsSnapshot = groupsRef.get().await()

            val availableGroups = groupsSnapshot.documents.filter { doc ->
                val members = doc.get("members") as? List<*> ?: emptyList<Any>()
                val maxStudents = doc.getLong("maxStudents") ?: 5
                members.size < maxStudents
            }

            if (availableGroups.isEmpty()) {
                return Result.failure(Exception("No available groups to assign student"))
            }

            val selectedGroup = availableGroups.random()
            val groupId = selectedGroup.id

            // Add student to the group
            selectedGroup.reference.update("members", FieldValue.arrayUnion(studentId)).await()

            // Update student's groupId field
            studentsRef.document(studentId).update("groupId", groupId).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
