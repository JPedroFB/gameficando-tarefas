package com.example.gameficando_tarefas.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gameficando_tarefas.domain.model.HistoryItem
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT) }
    val monthYearFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")) }

    // Agrupa histórico por data formatada
    val historyByDate = remember(history) {
        history.groupBy { dateFormatter.format(Date(it.timestamp)) }
    }
    
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = contentPadding.calculateTopPadding())
    ) {
        // Cabeçalho do Calendário
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
            }

            Text(
                text = monthYearFormatter.format(currentMonth.time).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo mês")
            }
        }

        CalendarGrid(
            currentMonth = currentMonth,
            historyByDate = historyByDate,
            selectedDate = selectedDate,
            onDateClick = { date -> selectedDate = if (selectedDate == date) null else date }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Detalhes do dia selecionado
        AnimatedVisibility(visible = selectedDate != null) {
            val dateKey = selectedDate ?: ""
            val items = historyByDate[dateKey] ?: emptyList()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Atividades do dia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (items.isEmpty()) {
                    Text(
                        text = "Nenhuma atividade neste dia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            items.forEachIndexed { index, item ->
                                HistoryItemDetailRow(item = item)
                                if (index < items.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        if (selectedDate == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Toque em um dia para ver os detalhes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: Calendar,
    historyByDate: Map<String, List<HistoryItem>>,
    selectedDate: String?,
    onDateClick: (String) -> Unit
) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (currentMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
    
    // Dias da semana (Iniciais)
    val weekDays = listOf("D", "S", "T", "Q", "Q", "S", "S")
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        val totalCells = 42 // 6 semanas
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(300.dp)
        ) {
            items(totalCells) { index ->
                val dayOfMonth = index - (firstDayOfWeek - 2)
                if (dayOfMonth in 1..daysInMonth) {
                    val dateCal = (currentMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, dayOfMonth) }
                    val dateKey = dateFormatter.format(dateCal.time)
                    val items = historyByDate[dateKey] ?: emptyList()
                    
                    DayCell(
                        day = dayOfMonth,
                        items = items,
                        isSelected = selectedDate == dateKey,
                        onClick = { onDateClick(dateKey) }
                    )
                } else {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    items: List<HistoryItem>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val totalPoints = items.filterIsInstance<HistoryItem.TaskCompleted>().sumOf { it.pointsValue }
    val hasRedemption = items.any { it is HistoryItem.GoalRedeemed }
    
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        hasRedemption -> MaterialTheme.colorScheme.secondaryContainer
        totalPoints > 0 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        else -> Color.Transparent
    }
    
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        hasRedemption -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (totalPoints > 0 || hasRedemption) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
            if (totalPoints > 0) {
                Text(
                    text = totalPoints.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            if (hasRedemption && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                )
            }
        }
    }
}

@Composable
fun HistoryItemDetailRow(item: HistoryItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (item is HistoryItem.TaskCompleted) Icons.Filled.CheckCircle else Icons.Filled.Star
        val color = if (item is HistoryItem.TaskCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
        val points = if (item is HistoryItem.TaskCompleted) "+${item.pointsValue}" else "−${(item as HistoryItem.GoalRedeemed).pointsCost}"
        val desc = if (item is HistoryItem.TaskCompleted) item.taskDescription else (item as HistoryItem.GoalRedeemed).goalDescription

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$points pts",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}
