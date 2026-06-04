package com.example.gameficando_tarefas

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.gameficando_tarefas.data.db.AppDatabase
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

val TaskIdKey = ActionParameters.Key<Long>("task_id")
val TaskProfileIdKey = ActionParameters.Key<Long>("task_profile_id")

@OptIn(ExperimentalCoroutinesApi::class)
class TasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)

        db.profileStateDao().observeActiveProfileId().flatMapLatest { profileId ->
            combine(
                db.taskDao().getAllTasks(profileId),
                db.taskExecutionDao().getTotalPoints(profileId),
                db.goalRedemptionDao().getTotalRedemptionCost(profileId)
            ) { tasks, totalPoints, totalRedemptions ->
                Pair(tasks.take(3), totalPoints - totalRedemptions)
            }
        }.collectLatest { (tasks, netPoints) ->
            provideContent {
                GlanceTheme {
                    TasksWidgetContent(tasks.map { it.toDomain() }, netPoints)
                }
            }
        }
    }
}

@Composable
fun TasksWidgetContent(tasks: List<com.example.gameficando_tarefas.domain.model.Task>, netPoints: Int) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
    ) {
        // Header com fundo colorido
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.primaryContainer)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Destaques",
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            // Badge de pontos
            Box(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.primary)
                    .cornerRadius(1.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$netPoints pontos",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Lista de tarefas
        if (tasks.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma tarefa cadastrada",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp
                    )
                )
            }
        } else {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                tasks.forEachIndexed { index, task ->
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = task.iconEmoji,
                                    style = TextStyle(fontSize = 30.sp)
                                )
                                Spacer(modifier = GlanceModifier.width(10.dp))
                                Text(
                                    text = task.description,
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurface,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            // Chip de pontos
                            Box(
                                modifier = GlanceModifier
                                    .background(GlanceTheme.colors.secondaryContainer)
                                    .cornerRadius(8.dp)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "+${task.pointsValue} pts",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSecondaryContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        androidx.glance.Button(
                            modifier = GlanceModifier.padding(30.dp, vertical = 10.dp),
                            text = "✓",
                            onClick = actionRunCallback<ExecuteTaskAction>(
                                parameters = androidx.glance.action.actionParametersOf(
                                    TaskIdKey to task.id,
                                    TaskProfileIdKey to task.profileId
                                )
                            )
                        )
                    }
                    // Divisor entre tarefas
                    if (index < tasks.size - 1) {
                        Spacer(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(GlanceTheme.colors.surfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

class ExecuteTaskAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskIdKey] ?: return
        val profileId = parameters[TaskProfileIdKey] ?: return
        val db = AppDatabase.getInstance(context)
        db.taskExecutionDao().insert(TaskExecutionEntity(taskId = taskId, profileId = profileId))
        // O Flow em provideGlance detecta a mudança e atualiza o widget automaticamente
    }
}

class TasksWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TasksWidget()
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun TasksWidgetPreview() {
    GlanceTheme {
        TasksWidgetContent(
            tasks = listOf(
                com.example.gameficando_tarefas.domain.model.Task(
                    id = 1, description = "Beber água", pointsValue = 5,
                    maxExecutions = 0, isFixed = false, sortOrder = 0,
                    frequency = com.example.gameficando_tarefas.domain.model.TaskFrequency.DAILY
                ),
                com.example.gameficando_tarefas.domain.model.Task(
                    id = 2, description = "Exercitar 30min", pointsValue = 20,
                    maxExecutions = 0, isFixed = false, sortOrder = 1,
                    frequency = com.example.gameficando_tarefas.domain.model.TaskFrequency.DAILY
                ),
                com.example.gameficando_tarefas.domain.model.Task(
                    id = 3, description = "Leitura", pointsValue = 10,
                    maxExecutions = 0, isFixed = false, sortOrder = 2,
                    frequency = com.example.gameficando_tarefas.domain.model.TaskFrequency.DAILY
                ),
            ),
            netPoints = 150
        )
    }
}
