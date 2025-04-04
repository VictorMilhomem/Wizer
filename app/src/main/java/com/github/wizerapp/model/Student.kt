package com.github.wizerapp.model


data class Student(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val groupId: String? = null,
    val meetings: List<String> = emptyList(),
    val doubts: List<String> = emptyList(),
    val solvedDoubts: List<String> = emptyList(),
    val grades: Map<String, Int> = emptyMap()
)
