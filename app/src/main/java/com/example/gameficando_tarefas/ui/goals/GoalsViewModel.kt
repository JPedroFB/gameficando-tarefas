package com.example.gameficando_tarefas.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gameficando_tarefas.data.repository.GoalRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.domain.model.Goal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalUiState(
    val goal: Goal,
    val isAchieved: Boolean
)

class GoalsViewModel(
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val goals: StateFlow<List<GoalUiState>> = combine(
        goalRepository.getAllGoals(),
        taskRepository.getTotalPoints()
    ) { goals, totalPoints ->
        goals.map { GoalUiState(it, it.pointsRequired <= totalPoints) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun save(goal: Goal) {
        viewModelScope.launch { goalRepository.save(goal) }
    }

    fun delete(goal: Goal) {
        viewModelScope.launch { goalRepository.delete(goal) }
    }

    class Factory(
        private val goalRepository: GoalRepository,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GoalsViewModel(goalRepository, taskRepository) as T
    }
}
