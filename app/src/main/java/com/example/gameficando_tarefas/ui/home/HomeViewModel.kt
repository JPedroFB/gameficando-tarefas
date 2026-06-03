package com.example.gameficando_tarefas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gameficando_tarefas.data.repository.GoalRepository
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
    val totalPoints: Int = 0,
    val nextGoal: Goal? = null,
    val achievedGoals: List<Goal> = emptyList(),
    val tasks: List<TaskUiState> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.getTotalPoints(),
        goalRepository.getAllGoals(),
        taskRepository.getAllTasks()
    ) { totalPoints, goals, tasks ->
        val achieved = goals.filter { it.pointsRequired <= totalPoints }
        val nextGoal = goals.firstOrNull { it.pointsRequired > totalPoints }

        Triple(totalPoints, achieved, Pair(nextGoal, tasks))
    }.flatMapLatest { (totalPoints, achieved, rest) ->
        val (nextGoal, tasks) = rest
        if (tasks.isEmpty()) {
            flowOf(HomeUiState(totalPoints, nextGoal, achieved, emptyList()))
        } else {
            val now = System.currentTimeMillis()
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
                HomeUiState(totalPoints, nextGoal, achieved, taskStates.toList())
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
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(taskRepository, goalRepository) as T
    }
}
