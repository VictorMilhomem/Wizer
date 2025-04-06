package com.github.wizerapp.repository

import com.github.wizerapp.model.Professor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class ProfessorRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val professorsRef = db.collection("professors")

    // Cria ou atualiza o perfil do professor
    suspend fun createProfessor(professor: Professor): Result<Unit> {
        return try {
            val docRef = if (professor.id.isEmpty()) {
                professorsRef.document()
            } else {
                professorsRef.document(professor.id)
            }
            val professorData = hashMapOf(
                "id" to docRef.id,
                "name" to professor.name,
                "email" to professor.email,
                "groups" to professor.groups
            )
            docRef.set(professorData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Busca o perfil do professor pelo id
    suspend fun getProfessorById(professorId: String): Result<Professor> {
        return try {
            val snapshot = professorsRef.document(professorId).get().await()
            val professor = snapshot.toObject<Professor>()
            if (professor != null) {
                Result.success(professor)
            } else {
                Result.failure(Exception("Professor not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualiza alguns campos do perfil do professor
    suspend fun updateProfessor(professorId: String, updatedData: Map<String, Any>): Result<Unit> {
        return try {
            professorsRef.document(professorId).update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Remove o perfil do professor
    suspend fun deleteProfessor(professorId: String): Result<Unit> {
        return try {
            professorsRef.document(professorId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtém todos os professores
    suspend fun getAllProfessors(): Result<List<Professor>> {
        return try {
            val snapshot = professorsRef.get().await()
            val professors = snapshot.documents.mapNotNull { it.toObject<Professor>() }
            Result.success(professors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Adiciona um novo grupo ao campo "groups" do professor
    suspend fun addGroupToProfessor(professorId: String, groupId: String): Result<Unit> {
        return try {
            professorsRef.document(professorId)
                .update("groups", com.google.firebase.firestore.FieldValue.arrayUnion(groupId))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
