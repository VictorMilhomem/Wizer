package com.github.wizerapp.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class GroupRepository {
    private val firestore = Firebase.firestore

    init {
        firestore.useEmulator("10.0.2.2", 8080)
    }

    suspend fun createGroup(
        professorId: String,
        name: String,
        minStudents: Int,
        maxStudents: Int
    ): Result<String> {
        return try {
            // Cria um novo documento para o grupo, com um ID gerado automaticamente
            val newGroupRef = firestore.collection("groups").document()

            // Monta os dados do grupo utilizando FieldValue.serverTimestamp() para o campo createdAt
            val groupData = hashMapOf(
                "id" to newGroupRef.id,
                "name" to name,
                "minStudents" to minStudents,
                "maxStudents" to maxStudents,
                "totalScore" to 0,
                "createdAt" to FieldValue.serverTimestamp()
            )

            // Salva os dados no Firestore na coleção "groups"
            newGroupRef.set(groupData).await()

            // Atualiza o documento do professor na coleção "professors", adicionando o novo grupo ao campo "groups"
            firestore.collection("professors").document(professorId)
                .update("groups", FieldValue.arrayUnion(newGroupRef.id))
                .await()

            Result.success(newGroupRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
