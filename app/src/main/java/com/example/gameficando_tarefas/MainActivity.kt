package com.example.gameficando_tarefas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.gameficando_tarefas.data.db.AppDatabase
import com.example.gameficando_tarefas.data.repository.GoalRepository
import com.example.gameficando_tarefas.data.repository.GoalRedemptionRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.notification.createNotificationChannel
import com.example.gameficando_tarefas.notification.showCounterNotification
import com.example.gameficando_tarefas.ui.navigation.AppNavigation
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showCounterNotification(this, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val taskRepository = TaskRepository(db.taskDao(), db.taskExecutionDao())
        val goalRepository = GoalRepository(db.goalDao())
        val redemptionRepository = GoalRedemptionRepository(db.goalRedemptionDao())

        // Canal deve ser criado antes de exibir qualquer notificação
        createNotificationChannel(this)
        requestNotificationPermissionAndShow()

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

    private fun requestNotificationPermissionAndShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    showCounterNotification(this, 0)
                }
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 12 ou inferior não precisa de permissão em runtime
            showCounterNotification(this, 0)
        }
    }
}