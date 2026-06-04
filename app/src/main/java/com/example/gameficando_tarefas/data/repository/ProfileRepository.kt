package com.example.gameficando_tarefas.data.repository

import com.example.gameficando_tarefas.data.db.dao.ProfileDao
import com.example.gameficando_tarefas.data.db.dao.ProfileStateDao
import com.example.gameficando_tarefas.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val profileDao: ProfileDao,
    private val profileStateDao: ProfileStateDao
) {
    fun getProfiles(): Flow<List<Profile>> =
        profileDao.getAllProfiles().map { list -> list.map { it.toDomain() } }

    fun getActiveProfileId(): Flow<Long> = profileStateDao.observeActiveProfileId()

    suspend fun setActiveProfile(profileId: Long) {
        profileStateDao.setActiveProfileId(profileId)
    }
}
