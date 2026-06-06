package com.example.gameficando_tarefas.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gameficando_tarefas.domain.model.Profile
import com.example.gameficando_tarefas.ui.theme.GameficandotarefasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    profiles: List<Profile>,
    activeProfileId: Long,
    onSelectProfile: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text("Gameficando Tarefas") },
        actions = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profiles.forEach { profile ->
                    val isActive = profile.id == activeProfileId
                    ProfileBubble(
                        label = profile.name.filter { it.isDigit() }.ifBlank { profile.name.take(1) },
                        active = isActive,
                        onClick = { onSelectProfile(profile.id) }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    )
}

@Composable
fun ProfileBubble(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (active) 0.dp else 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (active) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ────────────────────────────────
// Previews
// ────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Profile Top Bar – profile 1 active")
@Composable
private fun ProfileTopBarP1Preview() {
    val profiles = listOf(Profile(1, "Perfil 1"), Profile(2, "Perfil 2"))
    GameficandotarefasTheme {
        ProfileTopBar(profiles = profiles, activeProfileId = 1L, onSelectProfile = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Profile Top Bar – profile 2 active")
@Composable
private fun ProfileTopBarP2Preview() {
    val profiles = listOf(Profile(1, "Perfil 1"), Profile(2, "Perfil 2"))
    GameficandotarefasTheme {
        ProfileTopBar(profiles = profiles, activeProfileId = 2L, onSelectProfile = {})
    }
}
