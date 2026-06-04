package com.example.gameficando_tarefas.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile_state",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["activeProfileId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("activeProfileId")]
)
data class ProfileStateEntity(
    @PrimaryKey val singletonId: Int = 1,
    val activeProfileId: Long = 1
)
