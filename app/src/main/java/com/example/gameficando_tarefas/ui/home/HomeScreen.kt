package com.example.gameficando_tarefas.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gameficando_tarefas.domain.model.Goal
import com.example.gameficando_tarefas.domain.model.Profile
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskFrequency
import com.example.gameficando_tarefas.ui.components.AnimatedEmoji
import com.example.gameficando_tarefas.ui.components.FullScreenCelebration
import com.example.gameficando_tarefas.ui.components.GoalProgressCard
import com.example.gameficando_tarefas.ui.components.TaskExecutionCard
import com.example.gameficando_tarefas.ui.profile.ProfileBubble
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    var showCelebration by remember { mutableStateOf(false) }
    var redeemedGoalName by remember { mutableStateOf("") }

    if (showCelebration) {
        FullScreenCelebration(
            goalDescription = redeemedGoalName,
            onDismiss = { showCelebration = false }
        )
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val localDensity = LocalDensity.current
    var topContentHeight by remember { mutableStateOf(0.dp) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val totalHeight = maxHeight
        
        // Calcula o peek height para que o sheet comece logo abaixo do card de pontos
        val calculatedPeekHeight = remember(totalHeight, topContentHeight) {
            if (topContentHeight > 0.dp) {
                (totalHeight - topContentHeight + 16.dp).coerceAtLeast(300.dp)
            } else {
                460.dp // Fallback inicial
            }
        }

        BottomSheetScaffold(
            modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()),
            scaffoldState = scaffoldState,
            sheetPeekHeight = calculatedPeekHeight,
            sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetTonalElevation = 12.dp,
            sheetDragHandle = { BottomSheetDefaults.DragHandle() },
            sheetContent = {
                // Conteúdo do "Sheet" de Tarefas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Tarefas de hoje",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.tasks.isEmpty()) {
                        Column(
                            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhuma tarefa para hoje!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.size(64.dp)) {
                                AnimatedEmoji(emoji = "🎉", modifier = Modifier.fillMaxSize())
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(state.tasks, key = { it.task.id }) { taskState ->
                                TaskExecutionCard(
                                    taskState = taskState,
                                    onExecute = { onExecute(taskState.task) }
                                )
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.onPrimary // Fundo principal
        ) { innerScaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        topContentHeight = with(localDensity) { coordinates.size.height.toDp() }
                    }
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
                            text = "Olá, ${activeProfile?.name ?: "João"}!",
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

                if (state.nextGoal != null) {
                    GoalProgressCard(
                        goal = state.nextGoal,
                        totalPoints = state.netPoints,
                        streakDays = state.streakDays,
                        upcomingGoals = state.upcomingGoals,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onRedeem = {
                            redeemedGoalName = state.nextGoal.description
                            onRedeem(state.nextGoal)
                            showCelebration = true
                        }
                    )
                } else {
                    // Card simples apenas com pontos se não houver objetivo
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Seus pontos",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            val formattedPoints = state.netPoints.toString().replace("(?<=\\d)(?=(\\d{3})+(?!\\d))", ".")
                            Text(
                                text = "$formattedPoints pts",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhum objetivo definido no momento.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
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
