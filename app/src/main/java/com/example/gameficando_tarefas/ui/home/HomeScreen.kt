package com.example.gameficando_tarefas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gameficando_tarefas.domain.model.Goal
import com.example.gameficando_tarefas.domain.model.Profile
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskFrequency
import com.example.gameficando_tarefas.ui.components.HomeDashboardCard
import com.example.gameficando_tarefas.ui.components.TaskExecutionCard
import com.example.gameficando_tarefas.ui.profile.ProfileBubble
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    activeProfile: Profile?,
    onExecute: (Task) -> Unit = {},
    onRedeem: (Goal) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR")) }
    val today = remember { dateFormatter.format(Date()).replaceFirstChar { it.uppercase() } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(top = contentPadding.calculateTopPadding())
    ) {
        // Header: Saudação e Data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Olá, ${activeProfile?.name ?: "João"}! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            activeProfile?.let {
                ProfileBubble(
                    label = it.name.filter { c -> c.isDigit() }.ifBlank { it.name.take(1) },
                    active = true,
                    onClick = {}
                )
            }
        }

        // Dashboard de Pontos e Objetivo
        HomeDashboardCard(
            totalPoints = state.netPoints,
            streakDays = 12, // mock por enquanto
            currentGoal = state.nextGoal,
            onRedeemGoal = onRedeem,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // "Sheet" de Tarefas
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Tarefas de hoje",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.tasks.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma tarefa para hoje! 🎉",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }

                items(state.tasks, key = { it.task.id }) { taskState ->
                    TaskExecutionCard(
                        taskState = taskState,
                        onExecute = { onExecute(taskState.task) }
                    )
                }
                
                item { 
                    Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding() + 40.dp)) 
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Home – New Style")
@Composable
private fun HomeScreenPreview() {
    val tasks = listOf(
        TaskUiState(Task(1, "Estudar React", 25, 1, TaskFrequency.DAILY, false, iconEmoji = "📖"), 0, false),
        TaskUiState(Task(2, "Caminhar 30 minutos", 20, 1, TaskFrequency.DAILY, false, iconEmoji = "🏃"), 0, false),
        TaskUiState(Task(3, "Beber 2L de água", 15, 1, TaskFrequency.DAILY, false, iconEmoji = "💧"), 0, false),
        TaskUiState(Task(4, "Escrever no diário", 10, 1, TaskFrequency.DAILY, false, iconEmoji = "📝"), 0, false),
    )
    GameficandotarefasTheme {
        HomeScreenContent(
            state = HomeUiState(
                netPoints = 2450,
                nextGoal = Goal(1, "Nintendo Switch 2", 5000),
                tasks = tasks
            ),
            activeProfile = Profile(1, "João")
        )
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    activeProfile: Profile?,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(
        state = state,
        activeProfile = activeProfile,
        onExecute = viewModel::executeTask,
        onRedeem = viewModel::redeemGoal,
        contentPadding = contentPadding,
        modifier = modifier
    )
}
