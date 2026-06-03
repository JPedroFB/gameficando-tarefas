package com.example.gameficando_tarefas.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskExecutionDao {
    @Query("SELECT * FROM task_executions WHERE taskId = :taskId AND executedAt >= :since")
    fun getExecutionsSince(taskId: Long, since: Long): Flow<List<TaskExecutionEntity>>

    @Query("SELECT COALESCE(SUM(t.pointsValue), 0) FROM task_executions te INNER JOIN tasks t ON te.taskId = t.id")
    fun getTotalPoints(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(execution: TaskExecutionEntity): Long
}
