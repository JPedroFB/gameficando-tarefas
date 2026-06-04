package com.example.gameficando_tarefas.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameficando_tarefas.data.db.entity.GoalRedemptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalRedemptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(redemption: GoalRedemptionEntity): Long

    @Query("SELECT COALESCE(SUM(pointsCost), 0) FROM goal_redemptions WHERE profileId = :profileId")
    fun getTotalRedemptionCost(profileId: Long): Flow<Int>

    @Query("SELECT COUNT(*) > 0 FROM goal_redemptions WHERE goalId = :goalId AND profileId = :profileId")
    fun isRedeemed(goalId: Long, profileId: Long): Flow<Boolean>

    @Query("SELECT * FROM goal_redemptions WHERE profileId = :profileId ORDER BY redeemedAt DESC")
    fun getAllRedemptions(profileId: Long): Flow<List<GoalRedemptionEntity>>
}
