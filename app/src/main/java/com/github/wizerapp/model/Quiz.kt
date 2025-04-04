package com.github.wizerapp.model

import com.google.firebase.Timestamp

data class Exercise(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val options: List<String> = emptyList(),
    val correct: Int = 0,
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "subject" to subject,
            "options" to options,
            "correct" to correct,
            "createdAt" to createdAt
        )
    }
}

data class Quiz(
    val id: String = "",
    val title: String = "",
    val exercises: List<Exercise> = emptyList(),
    val qrCode: String = "",
    val createdAt: Timestamp? = null
)
