package com.example.gameficando_tarefas.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gameficando_tarefas.data.repository.GoalRedemptionRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.domain.model.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val taskRepository: TaskRepository,
    private val redemptionRepository: GoalRedemptionRepository
) : ViewModel() {

    val history: StateFlow<List<HistoryItem>> = combine(
        taskRepository.getExecutionHistory(),
        redemptionRepository.getAllRedemptions()
    ) { executions, redemptions ->
        val items = mutableListOf<HistoryItem>()
        executions.forEach {
            items.add(HistoryItem.TaskCompleted(it.id, it.taskDescription, it.pointsValue, it.executedAt))
        }
        redemptions.forEach {
            items.add(HistoryItem.GoalRedeemed(it.id, it.goalDescription, it.pointsCost, it.redeemedAt))
        }
        items.sortedByDescending { it.timestamp }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    class Factory(
        private val taskRepository: TaskRepository,
        private val redemptionRepository: GoalRedemptionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HistoryViewModel(taskRepository, redemptionRepository) as T
    }
}
