package com.example.gameficando_tarefas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gameficando_tarefas.data.repository.GoalRepository
import com.example.gameficando_tarefas.data.repository.GoalRedemptionRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.domain.model.Goal
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskExecution
import com.example.gameficando_tarefas.domain.model.TaskFrequency
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class TaskUiState(
    val task: Task,
    val executionsInPeriod: Int,
    val isBlocked: Boolean
)

data class HomeUiState(
    val netPoints: Int = 0,
    val nextGoal: Goal? = null,
    val canRedeemNextGoal: Boolean = false,
    val tasks: List<TaskUiState> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository,
    private val redemptionRepository: GoalRedemptionRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.getTotalPoints(),
        redemptionRepository.getTotalRedemptionCost(),
        redemptionRepository.getAllRedemptions(),
        goalRepository.getAllGoals(),
        taskRepository.getAllTasks()
    ) { values ->
        val taskPoints = values[0] as Int
        val redemptionCost = values[1] as Int
        @Suppress("UNCHECKED_CAST")
        val redemptions = values[2] as List<com.example.gameficando_tarefas.domain.model.GoalRedemption>
        @Suppress("UNCHECKED_CAST")
        val goals = values[3] as List<com.example.gameficando_tarefas.domain.model.Goal>
        @Suppress("UNCHECKED_CAST")
        val tasks = values[4] as List<Task>
        val netPoints = taskPoints - redemptionCost
        val redeemedIds = redemptions.map { it.goalId }.toSet()
        val nextGoal = goals.sortedBy { it.pointsRequired }.firstOrNull { it.id !in redeemedIds }
        val canRedeem = nextGoal != null && netPoints >= nextGoal.pointsRequired
        Triple(netPoints, Pair(nextGoal, canRedeem), tasks)
    }.flatMapLatest { (netPoints, nextGoalData, tasks) ->
        val (nextGoal, canRedeem) = nextGoalData
        if (tasks.isEmpty()) {
            flowOf(HomeUiState(netPoints, nextGoal, canRedeem, emptyList()))
        } else {
            val executionFlows = tasks.map { task ->
                val since = periodStart(task.frequency)
                taskRepository.getExecutionsSince(task.id, since)
                    .let { flow ->
                        combine(flowOf(task), flow) { t, execs ->
                            val count = execs.size
                            val blocked = !t.isFixed && t.maxExecutions > 0 && count >= t.maxExecutions
                            TaskUiState(t, count, blocked)
                        }
                    }
            }
            combine(executionFlows) { taskStates ->
                HomeUiState(netPoints, nextGoal, canRedeem, taskStates.toList())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun executeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.recordExecution(TaskExecution(taskId = task.id))
        }
    }

    fun redeemGoal(goal: Goal) {
        viewModelScope.launch {
            redemptionRepository.redeem(goal.id, goal.description, goal.pointsRequired)
        }
    }

    private fun periodStart(frequency: TaskFrequency): Long {
        val cal = Calendar.getInstance()
        return when (frequency) {
            TaskFrequency.DAILY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            TaskFrequency.WEEKLY -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            TaskFrequency.MONTHLY -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
        }
    }

    class Factory(
        private val taskRepository: TaskRepository,
        private val goalRepository: GoalRepository,
        private val redemptionRepository: GoalRedemptionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(taskRepository, goalRepository, redemptionRepository) as T
    }
}
