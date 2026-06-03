package com.example.gameficando_tarefas.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gameficando_tarefas.data.repository.GoalRepository
import com.example.gameficando_tarefas.data.repository.GoalRedemptionRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.domain.model.Goal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalUiState(
    val goal: Goal,
    val isAchieved: Boolean,
    val isRedeemed: Boolean,
    val canRedeem: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
class GoalsViewModel(
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val redemptionRepository: GoalRedemptionRepository
) : ViewModel() {

    val goals: StateFlow<List<GoalUiState>> = combine(
        goalRepository.getAllGoals(),
        taskRepository.getTotalPoints(),
        redemptionRepository.getTotalRedemptionCost()
    ) { goals, taskPoints, redemptionCost ->
        val netPoints = taskPoints - redemptionCost
        Triple(goals, netPoints, Unit)
    }.flatMapLatest { (goals, netPoints, _) ->
        if (goals.isEmpty()) {
            flowOf(emptyList())
        } else {
            val redeemedFlows = goals.map { goal ->
                redemptionRepository.isRedeemed(goal.id).let { flow ->
                    combine(flowOf(goal), flow) { g, redeemed ->
                        GoalUiState(
                            goal = g,
                            isAchieved = netPoints >= g.pointsRequired,
                            isRedeemed = redeemed,
                            canRedeem = !redeemed && netPoints >= g.pointsRequired
                        )
                    }
                }
            }
            combine(redeemedFlows) { it.toList() }
        }
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

    fun redeemGoal(goal: Goal) {
        viewModelScope.launch {
            redemptionRepository.redeem(goal.id, goal.description, goal.pointsRequired)
        }
    }

    class Factory(
        private val goalRepository: GoalRepository,
        private val taskRepository: TaskRepository,
        private val redemptionRepository: GoalRedemptionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GoalsViewModel(goalRepository, taskRepository, redemptionRepository) as T
    }
}
