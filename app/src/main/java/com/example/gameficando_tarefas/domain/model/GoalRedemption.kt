package com.example.gameficando_tarefas.domain.model

data class GoalRedemption(
    val id: Long = 0,
    val goalId: Long,
    val goalDescription: String,
    val pointsCost: Int,
    val redeemedAt: Long = System.currentTimeMillis(),
    val profileId: Long = 0
)
