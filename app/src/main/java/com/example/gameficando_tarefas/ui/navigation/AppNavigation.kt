package com.example.gameficando_tarefas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gameficando_tarefas.data.repository.GoalRepository
import com.example.gameficando_tarefas.data.repository.GoalRedemptionRepository
import com.example.gameficando_tarefas.data.repository.TaskRepository
import com.example.gameficando_tarefas.ui.goals.GoalsScreen
import com.example.gameficando_tarefas.ui.goals.GoalsViewModel
import com.example.gameficando_tarefas.ui.history.HistoryScreen
import com.example.gameficando_tarefas.ui.history.HistoryViewModel
import com.example.gameficando_tarefas.ui.home.HomeScreen
import com.example.gameficando_tarefas.ui.home.HomeViewModel
import com.example.gameficando_tarefas.ui.tasks.TasksScreen
import com.example.gameficando_tarefas.ui.tasks.TasksViewModel
import kotlinx.serialization.Serializable

@Serializable object Home
@Serializable object Goals
@Serializable object Tasks
@Serializable object History

@Composable
fun AppNavigation(
    taskRepository: TaskRepository,
    goalRepository: GoalRepository,
    redemptionRepository: GoalRedemptionRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val navItems = listOf(
        Triple(Home, "Início", Icons.Filled.Home),
        Triple(Goals, "Objetivos", Icons.Filled.Star),
        Triple(Tasks, "Tarefas", Icons.Filled.List),
        Triple(History, "Histórico", Icons.Filled.History)
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                navItems.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(route::class) == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home
        ) {
            composable<Home> {
                val vm: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(taskRepository, goalRepository, redemptionRepository)
                )
                HomeScreen(viewModel = vm, contentPadding = innerPadding)
            }
            composable<Goals> {
                val vm: GoalsViewModel = viewModel(
                    factory = GoalsViewModel.Factory(goalRepository, taskRepository, redemptionRepository)
                )
                GoalsScreen(viewModel = vm, contentPadding = innerPadding)
            }
            composable<Tasks> {
                val vm: TasksViewModel = viewModel(
                    factory = TasksViewModel.Factory(taskRepository)
                )
                TasksScreen(viewModel = vm, contentPadding = innerPadding)
            }
            composable<History> {
                val vm: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(taskRepository, redemptionRepository)
                )
                HistoryScreen(viewModel = vm, contentPadding = innerPadding)
            }
        }
    }
}
