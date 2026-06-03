package com.example.gameficando_tarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.gameficando_tarefas.data.db.AppDatabase
import com.example.gameficando_tarefas.data.repository.GoalRepository
import com.example.gameficando_tarefas.data.repository.GoalRedemptionRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.ui.navigation.AppNavigation
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val taskRepository = TaskRepository(db.taskDao(), db.taskExecutionDao())
        val goalRepository = GoalRepository(db.goalDao())
        val redemptionRepository = GoalRedemptionRepository(db.goalRedemptionDao())

        setContent {
            GameficandotarefasTheme {
                AppNavigation(
                    taskRepository = taskRepository,
                    goalRepository = goalRepository,
                    redemptionRepository = redemptionRepository,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}