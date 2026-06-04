package com.example.gameficando_tarefas.domain.model

data class Goal(
    val id: Long = 0,
    val description: String,
    val pointsRequired: Int,
    val profileId: Long = 0
)
