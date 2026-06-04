package com.example.gameficando_tarefas.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gameficando_tarefas.data.db.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>
}
