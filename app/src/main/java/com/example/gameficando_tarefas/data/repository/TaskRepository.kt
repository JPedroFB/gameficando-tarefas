package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.ProfileStateDao
import com.example.gameficando_tarefas.data.db.dao.TaskDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionHistory
import com.example.gameficando_tarefas.data.db.entity.TaskEntity
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskExecution
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepository(
    private val taskDao: TaskDao,
    private val executionDao: TaskExecutionDao,
    private val profileStateDao: ProfileStateDao
) {
    fun getAllTasks(): Flow<List<Task>> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            taskDao.getAllTasks(profileId).map { list -> list.map { it.toDomain() } }
        }

    fun getTotalPoints(): Flow<Int> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            executionDao.getTotalPoints(profileId)
        }

    fun getExecutionsSince(taskId: Long, profileId: Long, since: Long): Flow<List<TaskExecution>> =
        executionDao.getExecutionsSince(taskId, profileId, since)
            .map { list -> list.map { it.toDomain() } }

    fun getExecutionHistory(): Flow<List<TaskExecutionHistory>> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            executionDao.getAllExecutionHistory(profileId)
        }

    suspend fun save(task: Task) {
        val profileId = task.profileId.takeIf { it != 0L } ?: profileStateDao.getActiveProfileIdOnce()
        if (task.id == 0L) {
            val nextOrder = taskDao.getMaxSortOrder(profileId) + 1
            taskDao.insert(TaskEntity.fromDomain(task.copy(sortOrder = nextOrder, profileId = profileId)))
        } else {
            taskDao.update(TaskEntity.fromDomain(task.copy(profileId = profileId)))
        }
    }

    suspend fun delete(task: Task) {
        taskDao.delete(TaskEntity.fromDomain(task))
    }

    suspend fun recordExecution(execution: TaskExecution) {
        val profileId = execution.profileId.takeIf { it != 0L } ?: profileStateDao.getActiveProfileIdOnce()
        executionDao.insert(TaskExecutionEntity.fromDomain(execution.copy(profileId = profileId)))
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
