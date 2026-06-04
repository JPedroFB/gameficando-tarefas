package com.example.gameficando_tarefas.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gameficando_tarefas.domain.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: Long,
    val name: String
) {
    fun toDomain() = Profile(id = id, name = name)
}
