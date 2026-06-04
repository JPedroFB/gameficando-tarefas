package com.example.gameficando_tarefas.domain.model

data class Task(
    val id: Long = 0,
    val description: String,
    val pointsValue: Int,
    val maxExecutions: Int,
    val frequency: TaskFrequency,
    val isFixed: Boolean,
    val sortOrder: Int = 0,
    val iconEmoji: String = "🎯",
    val profileId: Long = 0
)
