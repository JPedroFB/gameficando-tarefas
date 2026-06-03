package com.example.gameficando_tarefas.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskFrequency

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val pointsValue: Int,
    val maxExecutions: Int,
    val frequency: TaskFrequency,
    val isFixed: Boolean,
    val sortOrder: Int = 0,
    val iconEmoji: String = "🎯"
) {
    fun toDomain() = Task(
        id = id,
        description = description,
        pointsValue = pointsValue,
        maxExecutions = maxExecutions,
        frequency = frequency,
        isFixed = isFixed,
        sortOrder = sortOrder,
        iconEmoji = iconEmoji
    )

    companion object {
        fun fromDomain(task: Task) = TaskEntity(
            id = task.id,
            description = task.description,
            pointsValue = task.pointsValue,
            maxExecutions = task.maxExecutions,
            frequency = task.frequency,
            isFixed = task.isFixed,
            sortOrder = task.sortOrder,
            iconEmoji = task.iconEmoji
        )
    }
}
