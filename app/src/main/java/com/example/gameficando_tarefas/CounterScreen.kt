package com.example.gameficando_tarefas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    // rememberSaveable preserva o estado em rotações de tela
    var count by rememberSaveable { mutableIntStateOf(0) }

    CounterContent(
        count = count,
        onIncrement = { count++ },
        onDecrement = { count-- },
        onReset = { count = 0 },
        modifier = modifier
    )
}

// State hoisting: CounterContent é stateless e facilmente testável/reutilizável
@Composable
fun CounterContent(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Contador",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "$count",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onDecrement,
                modifier = Modifier.size(56.dp)
            ) {
                Text(text = "−", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onIncrement,
                modifier = Modifier.size(56.dp)
            ) {
                Text(text = "+", style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onReset) {
            Text(text = "Resetar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterScreenPreview() {
    GameficandotarefasTheme {
        Surface {
            CounterContent(
                count = 42,
                onIncrement = {},
                onDecrement = {},
                onReset = {}
            )
        }
    }
}
