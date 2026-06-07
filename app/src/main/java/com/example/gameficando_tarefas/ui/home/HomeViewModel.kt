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
    val upcomingGoals: List<Goal> = emptyList(),
    val tasks: List<TaskUiState> = emptyList(),
    val streakDays: Int = 0
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
        taskRepository.getAllTasks(),
        taskRepository.getExecutionHistory()
    ) { values ->
        val taskPoints = values[0] as Int
        val redemptionCost = values[1] as Int
        @Suppress("UNCHECKED_CAST")
        val redemptions = values[2] as List<com.example.gameficando_tarefas.domain.model.GoalRedemption>
        @Suppress("UNCHECKED_CAST")
        val goals = values[3] as List<com.example.gameficando_tarefas.domain.model.Goal>
        @Suppress("UNCHECKED_CAST")
        val tasks = values[4] as List<Task>
        @Suppress("UNCHECKED_CAST")
        val history = values[5] as List<com.example.gameficando_tarefas.data.db.dao.TaskExecutionHistory>

        val netPoints = taskPoints - redemptionCost
        val redeemedIds = redemptions.map { it.goalId }.toSet()
        val sortedGoals = goals.sortedBy { it.pointsRequired }.filter { it.id !in redeemedIds }
        val nextGoal = sortedGoals.firstOrNull()
        val upcomingGoals = sortedGoals.drop(1).take(4)
        val canRedeem = nextGoal != null && netPoints >= nextGoal.pointsRequired
        
        val streak = calculateStreak(history)
        
        data class Intermediate(val netPoints: Int, val nextGoalData: Triple<Goal?, Boolean, List<Goal>>, val tasks: List<Task>, val streak: Int)
        Intermediate(netPoints, Triple(nextGoal, canRedeem, upcomingGoals), tasks, streak)
    }.flatMapLatest { intermediate ->
        val (netPoints, nextGoalData, tasks, streak) = intermediate
        val (nextGoal, canRedeem, upcomingGoals) = nextGoalData
        if (tasks.isEmpty()) {
            flowOf(HomeUiState(netPoints, nextGoal, canRedeem, upcomingGoals, emptyList(), streak))
        } else {
            val executionFlows = tasks.map { task ->
                val since = periodStart(task.frequency)
                taskRepository.getExecutionsSince(task.id, task.profileId, since)
                    .let { flow ->
                        combine(flowOf(task), flow) { t, execs ->
                            val count = execs.size
                            val blocked = !t.isFixed && t.maxExecutions > 0 && count >= t.maxExecutions
                            TaskUiState(t, count, blocked)
                        }
                    }
            }
            combine(executionFlows) { taskStates ->
                HomeUiState(netPoints, nextGoal, canRedeem, upcomingGoals, taskStates.toList(), streak)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    private fun calculateStreak(history: List<com.example.gameficando_tarefas.data.db.dao.TaskExecutionHistory>): Int {
        if (history.isEmpty()) return 0

        val calendar = Calendar.getInstance()
        
        // Formata para String YYYY-MM-DD para agrupar por dia
        val uniqueDates = history.map {
            calendar.timeInMillis = it.executedAt
            val y = calendar.get(Calendar.YEAR)
            val m = calendar.get(Calendar.MONTH)
            val d = calendar.get(Calendar.DAY_OF_MONTH)
            "$y-$m-$d"
        }.toSet()

        var streak = 0
        val checkCalendar = Calendar.getInstance()
        
        // Verifica hoje
        var currentDayKey = "${checkCalendar.get(Calendar.YEAR)}-${checkCalendar.get(Calendar.MONTH)}-${checkCalendar.get(Calendar.DAY_OF_MONTH)}"
        
        if (currentDayKey in uniqueDates) {
            // Se hoje tem pontos, começa a contar de hoje
            while (currentDayKey in uniqueDates) {
                streak++
                checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
                currentDayKey = "${checkCalendar.get(Calendar.YEAR)}-${checkCalendar.get(Calendar.MONTH)}-${checkCalendar.get(Calendar.DAY_OF_MONTH)}"
            }
        } else {
            // Se hoje não tem pontos, verifica se ontem teve
            checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
            currentDayKey = "${checkCalendar.get(Calendar.YEAR)}-${checkCalendar.get(Calendar.MONTH)}-${checkCalendar.get(Calendar.DAY_OF_MONTH)}"
            
            while (currentDayKey in uniqueDates) {
                streak++
                checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
                currentDayKey = "${checkCalendar.get(Calendar.YEAR)}-${checkCalendar.get(Calendar.MONTH)}-${checkCalendar.get(Calendar.DAY_OF_MONTH)}"
            }
        }

        return streak
    }

    fun executeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.recordExecution(TaskExecution(taskId = task.id, profileId = task.profileId))
        }
    }

    fun redeemGoal(goal: Goal) {
        viewModelScope.launch {
            redemptionRepository.redeem(goal.id, goal.description, goal.pointsRequired, goal.profileId)
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
