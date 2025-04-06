package com.github.wizerapp.model


data class Professor(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val groups: List<String> = emptyList()
)
