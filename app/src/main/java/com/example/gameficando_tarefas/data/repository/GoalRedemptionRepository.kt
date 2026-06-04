package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.GoalRedemptionDao
import com.example.gameficando_tarefas.data.db.dao.ProfileStateDao
import com.example.gameficando_tarefas.data.db.entity.GoalRedemptionEntity
import com.example.gameficando_tarefas.domain.model.GoalRedemption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class GoalRedemptionRepository(
    private val dao: GoalRedemptionDao,
    private val profileStateDao: ProfileStateDao
) {

    fun getTotalRedemptionCost(): Flow<Int> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            dao.getTotalRedemptionCost(profileId)
        }

    fun isRedeemed(goalId: Long): Flow<Boolean> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            dao.isRedeemed(goalId, profileId)
        }

    fun getAllRedemptions(): Flow<List<GoalRedemption>> =
        profileStateDao.observeActiveProfileId().flatMapLatest { profileId ->
            dao.getAllRedemptions(profileId).map { list ->
                list.map { it.toDomain() }
            }
        }

    suspend fun redeem(goalId: Long, goalDescription: String, pointsCost: Int, profileId: Long = 0) {
        val resolvedProfileId = profileId.takeIf { it != 0L } ?: profileStateDao.getActiveProfileIdOnce()
        dao.insert(
            GoalRedemptionEntity(
                goalId = goalId,
                goalDescription = goalDescription,
                pointsCost = pointsCost,
                profileId = resolvedProfileId
            )
        )
    }

    private fun GoalRedemptionEntity.toDomain() = GoalRedemption(
        id = id,
        goalId = goalId,
        goalDescription = goalDescription,
        pointsCost = pointsCost,
        redeemedAt = redeemedAt,
        profileId = profileId
    )
}
