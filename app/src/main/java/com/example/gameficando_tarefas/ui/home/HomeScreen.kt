package com.example.gameficando_tarefas.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gameficando_tarefas.domain.model.Goal
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskFrequency
import com.example.gameficando_tarefas.ui.components.GoalProgressCard
import com.example.gameficando_tarefas.ui.components.PointsBanner
import com.example.gameficando_tarefas.ui.components.TaskCard
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme

// ────────────────────────────────
// Previews (stateless content)
// ────────────────────────────────

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onExecute: (Task) -> Unit = {},
    onRedeem: (Goal) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { PointsBanner(totalPoints = state.netPoints) }
        item { Spacer(modifier = Modifier.height(12.dp)) }

        state.nextGoal?.let { goal ->
            item {
                GoalProgressCard(
                    goal = goal,
                    totalPoints = state.netPoints,
                    upcomingGoals = state.upcomingGoals,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                if (state.canRedeemNextGoal) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onRedeem(goal) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("Coletar objetivo (−${goal.pointsRequired} pts)") }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Tarefas Disponíveis",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.tasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma tarefa cadastrada.\nAdicione tarefas na aba Tarefas.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        items(state.tasks, key = { it.task.id }) { taskState ->
            TaskCard(
                taskState = taskState,
                onExecute = { onExecute(taskState.task) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Home – with data")
@Composable
private fun HomeScreenPreview() {
    val tasks = listOf(
        TaskUiState(Task(1, "Beber água 💧", 5, 1, TaskFrequency.DAILY, false), 0, false),
        TaskUiState(Task(2, "Exercitar 30min 🏃", 20, 1, TaskFrequency.DAILY, false), 0, false),
        TaskUiState(Task(3, "Leitura 📚", 10, 0, TaskFrequency.DAILY, true), 0, false),
    )
    GameficandotarefasTheme {
        HomeScreenContent(
            state = HomeUiState(
                netPoints = 320,
                nextGoal = Goal(1, "Viagem para a praia", 500),
                canRedeemNextGoal = false,
                upcomingGoals = listOf(Goal(2, "Novo fone", 800)),
                tasks = tasks
            )
        )
    }
}

@Preview(showBackground = true, name = "Home – empty tasks")
@Composable
private fun HomeScreenEmptyPreview() {
    GameficandotarefasTheme {
        HomeScreenContent(state = HomeUiState(netPoints = 0))
    }
}

@Preview(showBackground = true, name = "Home – can redeem")
@Composable
private fun HomeScreenCanRedeemPreview() {
    val tasks = listOf(
        TaskUiState(Task(1, "Meditação 🧘", 30, 0, TaskFrequency.DAILY, true), 0, false)
    )
    GameficandotarefasTheme {
        HomeScreenContent(
            state = HomeUiState(
                netPoints = 600,
                nextGoal = Goal(1, "Viagem para a praia", 500),
                canRedeemNextGoal = true,
                tasks = tasks
            )
        )
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(
        state = state,
        onExecute = viewModel::executeTask,
        onRedeem = viewModel::redeemGoal,
        contentPadding = contentPadding,
        modifier = modifier
    )
}
