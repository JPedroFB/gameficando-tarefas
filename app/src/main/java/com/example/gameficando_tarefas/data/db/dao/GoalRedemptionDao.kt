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

    @Query("SELECT COALESCE(SUM(pointsCost), 0) FROM goal_redemptions")
    fun getTotalRedemptionCost(): Flow<Int>

    @Query("SELECT COUNT(*) > 0 FROM goal_redemptions WHERE goalId = :goalId")
    fun isRedeemed(goalId: Long): Flow<Boolean>

    @Query("SELECT * FROM goal_redemptions ORDER BY redeemedAt DESC")
    fun getAllRedemptions(): Flow<List<GoalRedemptionEntity>>
}
