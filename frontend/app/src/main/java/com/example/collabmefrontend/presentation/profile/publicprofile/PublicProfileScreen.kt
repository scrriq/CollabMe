package com.example.collabmefrontend.presentation.profile.publicprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PublicProfileScreen(
    viewModel: PublicProfileViewModel,
    userId: String,
    isOwnProfile: Boolean = true,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId, isOwnProfile) {
        viewModel.onIntent(PublicProfileIntent.Load(userId, isOwnProfile))
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                PublicProfileEffect.NavigateBack -> onBack()
                PublicProfileEffect.NavigateToEdit -> onEdit()
            }
        }
    }

    val headerColor = Color(0xFF0A0F4F)
    val bodyColor = Color(0xFFE6E6E6)
    val avatarColor = Color(0xFF2F49B8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(headerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (!state.isOwnProfile) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(44.dp)
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(44.dp))
                }

                if (state.isOwnProfile) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(Color.White, Color.White)
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Edit",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(80.dp))
                }
            }

            Box(
                modifier = Modifier
                    .size(132.dp)
                    .align(Alignment.BottomCenter)
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .background(avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initialsFromName(state.displayName),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = bodyColor,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 24.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = buildNameAndAgeText(state.displayName, state.ageText),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${state.universityText} | ${state.professionText}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF2E2E2E)
                        )
                    }
                }

                item {
                    SectionTitle("Description")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.descriptionText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2E2E2E)
                    )
                }

                item {
                    SectionTitle("Contacts")
                }

                items(state.contacts) { contact ->
                    ContactCard(
                        platform = contact.platform,
                        url = contact.url
                    )
                }

                if (state.isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.size(12.dp))
                            Text("Loading profile...")
                        }
                    }
                }

                state.error?.let { error ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFE7E7)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = error,
                                    color = Color(0xFF8A1F1F),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(onClick = {
                                    viewModel.onIntent(PublicProfileIntent.Retry)
                                }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111)
    )
}

@Composable
private fun ContactCard(
    platform: String,
    url: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFF1E88FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = platform,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B6B6B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1E1E1E),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun initialsFromName(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    if (parts.isEmpty()) return "?"
    val first = parts.getOrNull(0)?.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
    val second = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
    return (first + second).ifBlank { "?" }
}

private fun buildNameAndAgeText(name: String, ageText: String): String {
    return if (ageText.isNotBlank()) {
        "$name, $ageText"
    } else {
        name
    }
}