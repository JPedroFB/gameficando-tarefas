package com.example.gameficando_tarefas.ui.tasks

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.example.gameficando_tarefas.domain.model.Task
import com.example.gameficando_tarefas.domain.model.TaskFrequency

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma tarefa cadastrada.\nToque em + para adicionar.",
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
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onEdit = {
                            editingTask = task
                            showDialog = true
                        },
                        onDelete = { viewModel.delete(task) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingTask = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp
                )
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adicionar tarefa")
        }
    }

    if (showDialog) {
        TaskDialog(
            initial = editingTask,
            onDismiss = { showDialog = false },
            onConfirm = { task ->
                viewModel.save(task)
                showDialog = false
            }
        )
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                        Text(
                            text = "+${task.pointsValue} pts",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    val freqLabel = when {
                        task.isFixed -> "Fixa"
                        else -> when (task.frequency) {
                            TaskFrequency.DAILY -> "Diária"
                            TaskFrequency.WEEKLY -> "Semanal"
                            TaskFrequency.MONTHLY -> "Mensal"
                        }
                    }
                    Text(
                        text = freqLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (!task.isFixed && task.maxExecutions > 0) {
                        Text(
                            text = "max ${task.maxExecutions}x",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDialog(
    initial: Task?,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var pointsText by remember { mutableStateOf(initial?.pointsValue?.toString() ?: "") }
    var maxExecText by remember { mutableStateOf(initial?.maxExecutions?.toString() ?: "1") }
    var frequency by remember { mutableStateOf(initial?.frequency ?: TaskFrequency.DAILY) }
    var isFixed by remember { mutableStateOf(initial?.isFixed ?: false) }
    var frequencyExpanded by remember { mutableStateOf(false) }

    val frequencyLabels = mapOf(
        TaskFrequency.DAILY to "Diária",
        TaskFrequency.WEEKLY to "Semanal",
        TaskFrequency.MONTHLY to "Mensal"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova Tarefa" else "Editar Tarefa") },
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
                    label = { Text("Pontos por execução") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tarefa Fixa", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isFixed, onCheckedChange = { isFixed = it })
                }

                if (!isFixed) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = frequencyExpanded,
                        onExpandedChange = { frequencyExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = frequencyLabels[frequency] ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Frequência") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = frequencyExpanded,
                            onDismissRequest = { frequencyExpanded = false }
                        ) {
                            frequencyLabels.forEach { (freq, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        frequency = freq
                                        frequencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = maxExecText,
                        onValueChange = { maxExecText = it.filter { c -> c.isDigit() } },
                        label = { Text("Limite de execuções no período") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val pts = pointsText.toIntOrNull() ?: return@TextButton
                    if (description.isBlank()) return@TextButton
                    val maxExec = if (isFixed) 0 else (maxExecText.toIntOrNull() ?: 1)
                    onConfirm(
                        Task(
                            id = initial?.id ?: 0,
                            description = description.trim(),
                            pointsValue = pts,
                            maxExecutions = maxExec,
                            frequency = frequency,
                            isFixed = isFixed
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
