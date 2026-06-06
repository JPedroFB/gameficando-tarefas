package com.example.gameficando_tarefas

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.gameficando_tarefas.data.db.AppDatabase
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity
import com.example.gameficando_tarefas.ui.theme.*
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
    val formattedPoints = netPoints.toString().replace("(?<=\\d)(?=(\\d{3})+(?!\\d))", ".")

    // Usamos ColorProvider(day, night) da biblioteca Glance
    val primaryColor = ColorProvider(day = Primary40, night = DarkPrimary)
    val backgroundColor = ColorProvider(day = BackgroundLight, night = BackgroundDark)
    val onBackgroundColor = ColorProvider(day = OnBackgroundLight, night = OnBackgroundDark)
    val surfaceColor = ColorProvider(day = SurfaceLight, night = SurfaceDark)
    val onSurfaceColor = ColorProvider(day = OnSurfaceLight, night = OnSurfaceDark)
    val surfaceVariantColor = ColorProvider(day = SurfaceVariantLight, night = SurfaceVariantDark)
    val onSurfaceVariantColor = ColorProvider(day = OnSurfaceVariantLight, night = OnSurfaceVariantDark)
    val secondaryContainerColor = ColorProvider(day = SecondaryContainer40, night = DarkSecondaryContainer)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Destaques",
                    style = TextStyle(
                        color = onBackgroundColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "$formattedPoints pts acumulados",
                    style = TextStyle(
                        color = primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(16.dp))

        // Lista de tarefas seguindo a estrutura de "três blocos" (Idêntico ao App)
        if (tasks.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sem tarefas hoje! 🎉",
                    style = TextStyle(color = onSurfaceVariantColor, fontSize = 12.sp)
                )
            }
        } else {
            Column(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {
                tasks.forEach { task ->
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Bloco do Ícone
                        Box(
                            modifier = GlanceModifier
                                .size(56.dp)
                                .background(surfaceVariantColor)
                                .cornerRadius(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = task.iconEmoji, style = TextStyle(fontSize = 24.sp))
                        }

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        // 2. Bloco de Texto
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .height(56.dp)
                                .background(surfaceColor)
                                .cornerRadius(16.dp)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    text = task.description,
                                    style = TextStyle(
                                        color = onSurfaceColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "Tarefa diária",
                                    style = TextStyle(
                                        color = onSurfaceVariantColor,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        // 3. Bloco do Botão
                        Box(
                            modifier = GlanceModifier
                                .width(56.dp)
                                .height(56.dp)
                                .background(secondaryContainerColor)
                                .cornerRadius(16.dp)
                                .clickable(
                                    onClick = actionRunCallback<ExecuteTaskAction>(
                                        parameters = actionParametersOf(
                                            TaskIdKey to task.id,
                                            TaskProfileIdKey to task.profileId
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${task.pointsValue}",
                                style = TextStyle(
                                    color = primaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
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
    }
}

class TasksWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TasksWidget()
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 400)
@Composable
fun TasksWidgetPreview() {
    GlanceTheme {
        TasksWidgetContent(
            tasks = listOf(
                com.example.gameficando_tarefas.domain.model.Task(
                    id = 1, description = "Estudar React", pointsValue = 25,
                    maxExecutions = 0, isFixed = false, sortOrder = 0, iconEmoji = "📖",
                    frequency = com.example.gameficando_tarefas.domain.model.TaskFrequency.DAILY
                ),
                com.example.gameficando_tarefas.domain.model.Task(
                    id = 2, description = "Caminhar 30 min", pointsValue = 20,
                    maxExecutions = 0, isFixed = false, sortOrder = 1, iconEmoji = "🏃",
                    frequency = com.example.gameficando_tarefas.domain.model.TaskFrequency.DAILY
                ),
                com.example.gameficando_tarefas.domain.model.Task(
                    id = 3, description = "Beber água", pointsValue = 15,
                    maxExecutions = 0, isFixed = false, sortOrder = 2, iconEmoji = "💧",
                    frequency = com.example.gameficando_tarefas.domain.model.TaskFrequency.DAILY
                ),
            ),
            netPoints = 2450
        )
    }
}
