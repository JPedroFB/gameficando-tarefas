package com.example.gameficando_tarefas.domain.model

sealed class HistoryItem {
    abstract val id: Long
    abstract val timestamp: Long

    data class TaskCompleted(
        override val id: Long,
        val taskDescription: String,
        val pointsValue: Int,
        override val timestamp: Long
    ) : HistoryItem()

    data class GoalRedeemed(
        override val id: Long,
        val goalDescription: String,
        val pointsCost: Int,
        override val timestamp: Long
    ) : HistoryItem()
}
