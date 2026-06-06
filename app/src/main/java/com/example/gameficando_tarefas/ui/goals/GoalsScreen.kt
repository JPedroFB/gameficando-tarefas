package com.example.gameficando_tarefas.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gameficando_tarefas.domain.model.Goal
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme

private val GOAL_EMOJIS = listOf(
    "🎯", "🎮", "🏖️", "✈️", "🚗", "🚲", "🏠", "💻", "📱", "🎧",
    "👟", "👕", "⌚", "📚", "🍳", "🎸", "🎨", "🎁", "💰", "🌟"
)

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
                        onDelete = { viewModel.delete(goalState.goal) },
                        onRedeem = { viewModel.redeemGoal(goalState.goal) }
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
    onDelete: () -> Unit,
    onRedeem: () -> Unit
) {
    val goal = goalState.goal
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when {
                goalState.isRedeemed -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                goalState.isAchieved -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (goalState.isAchieved || goalState.isRedeemed) null else CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = goal.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = "${goal.pointsRequired} pts",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                if (goalState.isRedeemed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Coletado ✅",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                } else if (goalState.canRedeem) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRedeem,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Coletar", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir", modifier = Modifier.size(20.dp))
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
    var selectedEmoji by remember { mutableStateOf(initial?.iconEmoji ?: "🎯") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Novo Objetivo" else "Editar Objetivo") },
        text = {
            Column {
                Text(
                    text = "Ícone",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(GOAL_EMOJIS) { emoji ->
                        val isSelected = emoji == selectedEmoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedEmoji = emoji }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    leadingIcon = { Text(selectedEmoji, fontSize = 18.sp) },
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
                    onConfirm(
                        Goal(
                            id = initial?.id ?: 0,
                            description = description.trim(),
                            pointsRequired = pts,
                            iconEmoji = selectedEmoji,
                            profileId = initial?.profileId ?: 0
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ────────────────────────────────
// Previews
// ────────────────────────────────

@Preview(showBackground = true, name = "Goal Item – locked")
@Composable
private fun GoalItemLockedPreview() {
    GameficandotarefasTheme {
        GoalItem(
            goalState = GoalUiState(Goal(1, "Viagem para a praia", 500, "🏖️"), canRedeem = false, isAchieved = false, isRedeemed = false),
            onEdit = {}, onDelete = {}, onRedeem = {}
        )
    }
}
