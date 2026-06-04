package com.example.gameficando_tarefas.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gameficando_tarefas.data.repository.ProfileRepository
import com.example.gameficando_tarefas.domain.model.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profiles: List<Profile> = emptyList(),
    val activeProfileId: Long = 1
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        profileRepository.getProfiles(),
        profileRepository.getActiveProfileId()
    ) { profiles, activeProfileId ->
        ProfileUiState(profiles = profiles, activeProfileId = activeProfileId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun setActiveProfile(profileId: Long) {
        viewModelScope.launch {
            profileRepository.setActiveProfile(profileId)
        }
    }

    class Factory(
        private val profileRepository: ProfileRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(profileRepository) as T
    }
}
