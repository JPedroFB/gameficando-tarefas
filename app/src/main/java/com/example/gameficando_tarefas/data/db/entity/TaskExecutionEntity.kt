package com.example.gameficando_tarefas.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gameficando_tarefas.domain.model.TaskExecution

@Entity(
    tableName = "task_executions",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("profileId")]
)
data class TaskExecutionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val executedAt: Long = System.currentTimeMillis(),
    val profileId: Long = 1
) {
    fun toDomain() = TaskExecution(
        id = id,
        taskId = taskId,
        executedAt = executedAt,
        profileId = profileId
    )

    companion object {
        fun fromDomain(exec: TaskExecution) = TaskExecutionEntity(
            id = exec.id,
            taskId = exec.taskId,
            executedAt = exec.executedAt,
            profileId = exec.profileId
        )
    }
}
