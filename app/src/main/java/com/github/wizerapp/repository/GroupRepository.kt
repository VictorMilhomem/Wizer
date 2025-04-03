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
        name: String,
        minStudents: Int,
        maxStudents: Int,
        subject: String
    ): Result<String> {
        return try {
            // Cria um novo documento para o grupo, com um ID gerado automaticamente
            val newGroupRef = firestore.collection("groups").document()

            // Monta os dados do grupo utilizando FieldValue.serverTimestamp()
            val groupData = hashMapOf(
                "id" to newGroupRef.id,
                "name" to name,
                "minStudents" to minStudents,
                "maxStudents" to maxStudents,
                "subject" to subject,
                "totalScore" to 0,
                "createdAt" to FieldValue.serverTimestamp()
            )

            // Salva os dados no Firestore
            newGroupRef.set(groupData).await()
            Result.success("Grupo criado com sucesso!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
