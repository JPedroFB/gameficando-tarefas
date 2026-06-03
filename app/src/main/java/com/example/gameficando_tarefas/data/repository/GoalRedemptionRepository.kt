package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.GoalRedemptionDao
import com.example.gameficando_tarefas.data.db.entity.GoalRedemptionEntity
import com.example.gameficando_tarefas.domain.model.GoalRedemption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRedemptionRepository(private val dao: GoalRedemptionDao) {

    fun getTotalRedemptionCost(): Flow<Int> = dao.getTotalRedemptionCost()

    fun isRedeemed(goalId: Long): Flow<Boolean> = dao.isRedeemed(goalId)

    fun getAllRedemptions(): Flow<List<GoalRedemption>> =
        dao.getAllRedemptions().map { list ->
            list.map { it.toDomain() }
        }

    suspend fun redeem(goalId: Long, goalDescription: String, pointsCost: Int) {
        dao.insert(GoalRedemptionEntity(goalId = goalId, goalDescription = goalDescription, pointsCost = pointsCost))
    }

    private fun GoalRedemptionEntity.toDomain() = GoalRedemption(
        id = id,
        goalId = goalId,
        goalDescription = goalDescription,
        pointsCost = pointsCost,
        redeemedAt = redeemedAt
    )
}
