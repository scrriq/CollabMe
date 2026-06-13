package com.example.collabmefrontend.presentation.applications.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collabmefrontend.core.util.buildAuthorDisplayName
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationCardsScreen(
    viewModel: ApplicationCardsViewModel,
    onNavigateToDetails: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onIntent(ApplicationCardsIntent.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ApplicationCardsEffect.NavigateToDetails -> onNavigateToDetails(effect.applicationId)
                is ApplicationCardsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Карточки", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D47A1),
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF1976D2),
                )
            }

            state.error?.let { error ->
                if (!state.isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.onIntent(ApplicationCardsIntent.Retry) }) {
                            Text("Повторить")
                        }
                    }
                }
            }

            if (state.allViewed && !state.isLoading && state.error == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Все заявки просмотрены",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            state.currentCard?.let { card ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SwipeApplicationCard(
                        card = card,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CardActionButtons(
                        isActionInProgress = state.isActionInProgress,
                        onReject = { viewModel.onIntent(ApplicationCardsIntent.RejectClicked) },
                        onView = { viewModel.onIntent(ApplicationCardsIntent.ViewApplicationClicked) },
                        onAccept = { viewModel.onIntent(ApplicationCardsIntent.AcceptClicked) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeApplicationCard(
    card: ApplicationCardUiModel,
    modifier: Modifier = Modifier,
) {
    val application = card.application
    val authorName = buildAuthorDisplayName(card.authorFirstName, card.authorLastName)
    val educationLine = listOf(card.universityLabel, card.directionLabel)
        .filter { it.isNotBlank() }
        .joinToString(" | ")

    Surface(
        modifier = modifier
            .border(3.dp, Color.Black, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFF8E7),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF1976D2)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = card.authorInitials,
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text = authorName.ifBlank { "Пользователь" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                if (card.authorAge.isNotBlank()) {
                    Text(
                        text = card.authorAge,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                if (educationLine.isNotBlank()) {
                    Text(
                        text = educationLine,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                if (application.title.isNotBlank()) {
                    Text(
                        text = application.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }

                Text(
                    text = application.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatusChip(
                        label = application.kind.title,
                        backgroundColor = Color(0xFFBDBDBD),
                        modifier = Modifier.weight(1f),
                    )
                    StatusChip(
                        label = application.status.title,
                        backgroundColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = Color.Black,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CardActionButtons(
    isActionInProgress: Boolean,
    onReject: () -> Unit,
    onView: () -> Unit,
    onAccept: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onReject,
            enabled = !isActionInProgress,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(bottomStart = 16.dp, topEnd = 8.dp, topStart = 8.dp, bottomEnd = 8.dp))
                .background(Color(0xFFE53935)),
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Пропустить",
                tint = Color.Black,
                modifier = Modifier.size(32.dp),
            )
        }

        IconButton(
            onClick = onView,
            enabled = !isActionInProgress,
            modifier = Modifier
                .size(72.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF8E7)),
        ) {
            Icon(
                Icons.Default.Assignment,
                contentDescription = "Посмотреть заявку",
                tint = Color.Black,
                modifier = Modifier.size(32.dp),
            )
        }

        Box(contentAlignment = Alignment.Center) {
            IconButton(
                onClick = onAccept,
                enabled = !isActionInProgress,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(bottomEnd = 16.dp, topEnd = 8.dp, topStart = 8.dp, bottomStart = 8.dp))
                    .background(Color(0xFF4CAF50)),
            ) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Добавить в избранное",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp),
                )
            }
            if (isActionInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(72.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}
