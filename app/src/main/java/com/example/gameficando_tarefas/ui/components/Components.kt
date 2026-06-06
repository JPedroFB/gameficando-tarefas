package com.example.gameficando_tarefas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gameficando_tarefas.domain.model.Goal
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskFrequency
import com.example.gameficando_tarefas.ui.home.TaskUiState
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme
import com.lottiefiles.dotlottie.core.compose.runtime.DotLottieController
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieEventListener
import com.lottiefiles.dotlottie.core.util.DotLottieSource

@Preview(showBackground = true, name = "Home Dashboard Card - In Progress")
@Composable
private fun HomeDashboardCardPreview() {
    GameficandotarefasTheme {
        HomeDashboardCard(
            totalPoints = 2450,
            streakDays = 12,
            currentGoal = Goal(id = 1, description = "Nintendo Switch 2", pointsRequired = 5000)
        )
    }
}

@Preview(showBackground = true, name = "Home Dashboard Card - Achieved")
@Composable
private fun HomeDashboardCardAchievedPreview() {
    GameficandotarefasTheme {
        HomeDashboardCard(
            totalPoints = 5500,
            streakDays = 15,
            currentGoal = Goal(id = 1, description = "Nintendo Switch 2", pointsRequired = 5000)
        )
    }
}

@Composable
fun FullScreenCelebration(
    goalDescription: String,
    onDismiss: () -> Unit
) {
    var animationFinished by remember { mutableStateOf(false) }
    val controller = remember { DotLottieController() }

    // Listener para detectar o fim da animação
    remember(controller) {
        val listener = object : DotLottieEventListener {
            override fun onComplete() {
                animationFinished = true
            }
        }
        controller.addEventListener(listener)
        listener
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false // Evita fechar sem querer antes do botão
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(modifier = Modifier.size(300.dp)) {
                    DotLottieAnimation(
                        source = DotLottieSource.Asset("Trophy.json"),
                        autoplay = true,
                        loop = false,
                        controller = controller,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Parabéns!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Você conquistou:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = goalDescription,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(visible = animationFinished) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Fechar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeDashboardCard(
    totalPoints: Int,
    streakDays: Int,
    currentGoal: Goal?,
    modifier: Modifier = Modifier,
    onRedeemGoal: (Goal) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Lado Esquerdo: Pontos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Seus pontos",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = totalPoints.toString().replace("(?<=\\d)(?=(\\d{3})+(?!\\d))", "."),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "pontos acumulados",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }


            }

            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Lado Direito: Objetivo
            Column(
                modifier = Modifier.weight(1.2f)
            ) {
                Text(
                    text = "Objetivo atual",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (currentGoal != null) {
                    val isGoalAchieved = totalPoints >= currentGoal.pointsRequired
                    val progress = (totalPoints.toFloat() / currentGoal.pointsRequired).coerceIn(0f, 1f)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = currentGoal.iconEmoji, fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentGoal.description,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Meta: ${currentGoal.pointsRequired} pontos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isGoalAchieved) {
                        // Botão de Coletar quando a meta é atingida
                        Button(
                            onClick = { onRedeemGoal(currentGoal) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Coletar prêmio!",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Barra de progresso enquanto não atinge a meta
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$totalPoints / ${currentGoal.pointsRequired}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% concluído",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum objetivo definido",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Task Execution Card")
@Composable
private fun TaskExecutionCardPreview() {
    GameficandotarefasTheme {
        TaskExecutionCard(
            taskState = TaskUiState(
                task = Task(
                    id = 1, description = "Estudar React", pointsValue = 25,
                    maxExecutions = 1, isFixed = false, frequency = TaskFrequency.DAILY,
                    iconEmoji = "📖"
                ),
                executionsInPeriod = 0, isBlocked = false
            ),
            onExecute = {}
        )
    }
}

fun Modifier.bounceClick(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounceScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onPress = { offset ->
                    val press = androidx.compose.foundation.interaction.PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    try {
                        awaitRelease()
                    } finally {
                        interactionSource.emit(androidx.compose.foundation.interaction.PressInteraction.Release(press))
                    }
                },
                onTap = { onClick() }
            )
        }
}

@Composable
fun TaskExecutionCard(
    taskState: TaskUiState,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskState.task

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Bloco do Ícone
        Surface(
            modifier = Modifier
                .size(68.dp)
                .bounceClick { /* Feedback apenas */ },
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = task.iconEmoji, fontSize = 30.sp)
            }
        }

        // 2. Bloco de Texto (Expandível)
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .bounceClick { /* Feedback apenas */ },
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (task.frequency) {
                        TaskFrequency.DAILY -> "Tarefa diária"
                        TaskFrequency.WEEKLY -> "Foco na semana"
                        TaskFrequency.MONTHLY -> "Meta do mês"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 3. Bloco do Botão (Pontos)
        Surface(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .fillMaxHeight()
                .bounceClick(enabled = !taskState.isBlocked) { onExecute() },
            color = if (taskState.isBlocked)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${task.pointsValue}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (taskState.isBlocked)
                        MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Goal Progress Card – with upcoming")
@Composable
private fun GoalProgressCardPreview() {
    GameficandotarefasTheme {
        GoalProgressCard(
            goal = Goal(id = 1, description = "Viagem para a praia", pointsRequired = 500),
            totalPoints = 320,
            upcomingGoals = listOf(
                Goal(id = 2, description = "Novo fone de ouvido", pointsRequired = 800),
                Goal(id = 3, description = "Jantar especial", pointsRequired = 1000)
            )
        )
    }
}

@Preview(showBackground = true, name = "Goal Progress Card – no upcoming")
@Composable
private fun GoalProgressCardSinglePreview() {
    GameficandotarefasTheme {
        GoalProgressCard(
            goal = Goal(id = 1, description = "Último objetivo", pointsRequired = 200),
            totalPoints = 50
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalProgressCard(
    goal: Goal,
    totalPoints: Int,
    modifier: Modifier = Modifier,
    upcomingGoals: List<Goal> = emptyList()
) {
    val progress = (totalPoints.toFloat() / goal.pointsRequired).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "goalProgress"
    )
    val animatedPoints by animateIntAsState(
        targetValue = totalPoints,
        label = "pointsCounter"
    )
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row: ícone + título + botão de expansão
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Próximo Objetivo",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                if (upcomingGoals.isNotEmpty()) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Recolher" else "Expandir",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.rotate(chevronRotation)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(100.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$animatedPoints",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "/ ${goal.pointsRequired}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (totalPoints < goal.pointsRequired) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "faltam ${goal.pointsRequired - totalPoints} pts para o seu objetivo",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Seção expansível com próximos objetivos
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Próximos objetivos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    upcomingGoals.forEach { upcoming ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = upcoming.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${upcoming.pointsRequired} pts",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Task Card – active")
@Composable
private fun TaskCardActivePreview() {
    GameficandotarefasTheme {
        TaskCard(
            taskState = TaskUiState(
                task = Task(
                    id = 1, description = "Beber 2L de água", pointsValue = 10,
                    maxExecutions = 1, isFixed = false, frequency = TaskFrequency.DAILY
                ),
                executionsInPeriod = 0, isBlocked = false
            ),
            onExecute = {}
        )
    }
}

@Preview(showBackground = true, name = "Task Card – blocked")
@Composable
private fun TaskCardBlockedPreview() {
    GameficandotarefasTheme {
        TaskCard(
            taskState = TaskUiState(
                task = Task(
                    id = 2, description = "Exercitar 30min", pointsValue = 20,
                    maxExecutions = 1, isFixed = false, frequency = TaskFrequency.DAILY
                ),
                executionsInPeriod = 1, isBlocked = true
            ),
            onExecute = {}
        )
    }
}

@Composable
fun TaskCard(
    taskState: TaskUiState,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskState.task
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Text(
                            text = "+${task.pointsValue} pts",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val label = when {
                        task.isFixed -> "Fixa"
                        else -> when (task.frequency) {
                            TaskFrequency.DAILY -> "Diária"
                            TaskFrequency.WEEKLY -> "Semanal"
                            TaskFrequency.MONTHLY -> "Mensal"
                        }
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (!task.isFixed && task.maxExecutions > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${taskState.executionsInPeriod}/${task.maxExecutions}x",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onExecute,
                enabled = !taskState.isBlocked
            ) {
                Text(text = "Executar")
            }
        }
    }
}
