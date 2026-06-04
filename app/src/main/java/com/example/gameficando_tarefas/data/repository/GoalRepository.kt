package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.ProfileStateDao
import com.example.gameficando_tarefas.data.db.dao.GoalDao
import com.example.gameficando_tarefas.data.db.entity.GoalEntity
import com.example.gameficando_tarefas.domain.model.Goal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class GoalRepository(
    private val dao: GoalDao,
    private val profileStateDao: ProfileStateDao
) {

    fun getAllGoals(): Flow<List<Goal>> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            dao.getAllGoals(profileId).map { list -> list.map { it.toDomain() } }
        }

    suspend fun save(goal: Goal) {
        val profileId = goal.profileId.takeIf { it != 0L } ?: profileStateDao.getActiveProfileIdOnce()
        if (goal.id == 0L) {
            dao.insert(GoalEntity.fromDomain(goal.copy(profileId = profileId)))
        } else {
            dao.update(GoalEntity.fromDomain(goal.copy(profileId = profileId)))
        }
    }

    suspend fun delete(goal: Goal) {
        dao.delete(GoalEntity.fromDomain(goal))
    }
}
