package com.example.gameficando_tarefas.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gameficando_tarefas.domain.model.Goal

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val pointsRequired: Int
) {
    fun toDomain() = Goal(id = id, description = description, pointsRequired = pointsRequired)

    companion object {
        fun fromDomain(goal: Goal) = GoalEntity(
            id = goal.id,
            description = goal.description,
            pointsRequired = goal.pointsRequired
        )
    }
}
