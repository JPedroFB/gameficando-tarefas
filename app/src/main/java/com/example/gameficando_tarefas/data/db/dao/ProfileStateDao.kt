package com.example.gameficando_tarefas.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gameficando_tarefas.data.db.entity.ProfileStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileStateDao {
    @Query("SELECT COALESCE((SELECT activeProfileId FROM profile_state WHERE singletonId = 1), 1)")
    fun observeActiveProfileId(): Flow<Long>

    @Query("SELECT COALESCE((SELECT activeProfileId FROM profile_state WHERE singletonId = 1), 1)")
    suspend fun getActiveProfileIdOnce(): Long

    @Query("UPDATE profile_state SET activeProfileId = :profileId WHERE singletonId = 1")
    suspend fun setActiveProfileId(profileId: Long)

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun upsert(state: ProfileStateEntity)
}
