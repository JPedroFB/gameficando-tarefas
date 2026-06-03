package com.example.gameficando_tarefas.domain.model

data class TaskExecution(
    val id: Long = 0,
    val taskId: Long,
    val executedAt: Long = System.currentTimeMillis()
)
