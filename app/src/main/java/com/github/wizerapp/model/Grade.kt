package com.github.wizerapp.model

import com.google.firebase.Timestamp

data class Grade(
    val id: String = "",
    val studentId: String = "",
    val exerciseId: String = "",
    val studentAnswer: Int = -1,
    val score: Int = 0,
    val submittedAt: Timestamp = Timestamp.now()
)