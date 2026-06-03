package com.example.gameficando_tarefas.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_redemptions")
data class GoalRedemptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val goalDescription: String,
    val pointsCost: Int,
    val redeemedAt: Long = System.currentTimeMillis()
)
