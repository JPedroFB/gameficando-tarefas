package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.GoalDao
import com.example.gameficando_tarefas.data.db.entity.GoalEntity
import com.example.gameficando_tarefas.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRepository(private val dao: GoalDao) {

    fun getAllGoals(): Flow<List<Goal>> =
        dao.getAllGoals().map { list -> list.map { it.toDomain() } }

    suspend fun save(goal: Goal) {
        if (goal.id == 0L) {
            dao.insert(GoalEntity.fromDomain(goal))
        } else {
            dao.update(GoalEntity.fromDomain(goal))
        }
    }

    suspend fun delete(goal: Goal) {
        dao.delete(GoalEntity.fromDomain(goal))
    }
}
