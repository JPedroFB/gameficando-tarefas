package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.TaskDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionHistory
import com.example.gameficando_tarefas.data.db.entity.TaskEntity
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskExecution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    private val executionDao: TaskExecutionDao
) {
    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }

    fun getTotalPoints(): Flow<Int> = executionDao.getTotalPoints()

    fun getExecutionsSince(taskId: Long, since: Long): Flow<List<TaskExecution>> =
        executionDao.getExecutionsSince(taskId, since).map { list -> list.map { it.toDomain() } }

    fun getExecutionHistory(): Flow<List<TaskExecutionHistory>> =
        executionDao.getAllExecutionHistory()

    suspend fun save(task: Task) {
        if (task.id == 0L) {
            val nextOrder = taskDao.getMaxSortOrder() + 1
            taskDao.insert(TaskEntity.fromDomain(task.copy(sortOrder = nextOrder)))
        } else {
            taskDao.update(TaskEntity.fromDomain(task))
        }
    }

    suspend fun delete(task: Task) {
        taskDao.delete(TaskEntity.fromDomain(task))
    }

    suspend fun recordExecution(execution: TaskExecution) {
        executionDao.insert(TaskExecutionEntity.fromDomain(execution))
    }

    suspend fun move(taskId: Long, direction: Int, allTasks: List<Task>) {
        val sorted = allTasks.sortedWith(compareBy({ it.sortOrder }, { it.id })).toMutableList()
        val index = sorted.indexOfFirst { it.id == taskId }
        val targetIndex = index + direction
        if (index < 0 || targetIndex < 0 || targetIndex >= sorted.size) return
        val tmp = sorted[index]
        sorted[index] = sorted[targetIndex]
        sorted[targetIndex] = tmp
        sorted.forEachIndexed { i, task -> taskDao.updateSortOrder(task.id, i) }
    }
}
