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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.gameficando_tarefas.data.db.AppDatabase
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import kotlinx.coroutines.flow.first

val TaskIdKey = ActionParameters.Key<Long>("task_id")

class TasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)
        val tasks = db.taskDao().getAllTasks().first().take(3)
        val totalPoints = db.taskExecutionDao().getTotalPoints().first()
        val totalRedemptions = db.goalRedemptionDao().getTotalRedemptionCost().first()
        val netPoints = totalPoints - totalRedemptions

        provideContent {
            GlanceTheme {
                TasksWidgetContent(tasks.map { it.toDomain() }, netPoints)
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
                text = "🎮 Destaques",
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            // Badge de pontos
            Box(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.primary)
                    .cornerRadius(12.dp)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐ $netPoints pts",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimary,
                        fontSize = 11.sp,
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
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = task.description,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
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
                            text = "✓",
                            onClick = actionRunCallback<ExecuteTaskAction>(
                                parameters = androidx.glance.action.actionParametersOf(
                                    TaskIdKey to task.id
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
        val db = AppDatabase.getInstance(context)
        db.taskExecutionDao().insert(TaskExecutionEntity(taskId = taskId))
        TasksWidget().update(context, glanceId)
    }
}

class TasksWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TasksWidget()
}
