package com.example.gameficando_tarefas.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import kotlinx.coroutines.flow.Flow

data class TaskExecutionHistory(
    val id: Long,
    val taskDescription: String,
    val pointsValue: Int,
    val executedAt: Long
)

@Dao
interface TaskExecutionDao {
    @Query("SELECT * FROM task_executions WHERE taskId = :taskId AND profileId = :profileId AND executedAt >= :since")
    fun getExecutionsSince(taskId: Long, profileId: Long, since: Long): Flow<List<TaskExecutionEntity>>

    @Query(
        "SELECT COALESCE(SUM(t.pointsValue), 0) " +
            "FROM task_executions te INNER JOIN tasks t ON te.taskId = t.id " +
            "WHERE te.profileId = :profileId"
    )
    fun getTotalPoints(profileId: Long): Flow<Int>

    @Query("""
        SELECT te.id AS id, t.description AS taskDescription, t.pointsValue AS pointsValue, te.executedAt AS executedAt
        FROM task_executions te
        INNER JOIN tasks t ON te.taskId = t.id
        WHERE te.profileId = :profileId
        ORDER BY te.executedAt DESC
    """)
    fun getAllExecutionHistory(profileId: Long): Flow<List<TaskExecutionHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(execution: TaskExecutionEntity): Long
}
