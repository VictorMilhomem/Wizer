package com.github.wizerapp.model

import com.google.firebase.Timestamp

data class Doubt(
    val id: String = "",
    val studentId: String = "",
    val text: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val resolved: Boolean = false,
    val resolvedBy: String? = null,
    val solution: String? = null
)
