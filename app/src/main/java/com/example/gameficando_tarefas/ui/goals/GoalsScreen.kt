package com.example.gameficando_tarefas.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gameficando_tarefas.domain.model.Goal

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<Goal?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum objetivo cadastrado.\nToque em + para adicionar.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(goals, key = { it.goal.id }) { goalState ->
                    GoalItem(
                        goalState = goalState,
                        onEdit = {
                            editingGoal = goalState.goal
                            showDialog = true
                        },
                        onDelete = { viewModel.delete(goalState.goal) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingGoal = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp
                )
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adicionar objetivo")
        }
    }

    if (showDialog) {
        GoalDialog(
            initial = editingGoal,
            onDismiss = { showDialog = false },
            onConfirm = { goal ->
                viewModel.save(goal)
                showDialog = false
            }
        )
    }
}

@Composable
private fun GoalItem(
    goalState: GoalUiState,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val goal = goalState.goal
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (goalState.isAchieved)
                MaterialTheme.colorScheme.tertiaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (goalState.isAchieved) {
                        Text("✅ ", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = "${goal.pointsRequired} pts",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir")
                }
            }
        }
    }
}

@Composable
private fun GoalDialog(
    initial: Goal?,
    onDismiss: () -> Unit,
    onConfirm: (Goal) -> Unit
) {
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var pointsText by remember { mutableStateOf(initial?.pointsRequired?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Novo Objetivo" else "Editar Objetivo") },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it.filter { c -> c.isDigit() } },
                    label = { Text("Pontos necessários") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val pts = pointsText.toIntOrNull() ?: return@TextButton
                    if (description.isBlank()) return@TextButton
                    onConfirm(Goal(id = initial?.id ?: 0, description = description.trim(), pointsRequired = pts))
                }
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
