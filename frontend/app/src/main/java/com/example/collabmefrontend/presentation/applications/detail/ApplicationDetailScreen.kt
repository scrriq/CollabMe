package com.example.collabmefrontend.presentation.applications.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ApplicationDetailScreen(
    applicationId: String,
    viewModel: ApplicationDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit // Новый колбэк
) {
    val state by viewModel.state.collectAsState()

    // Подписка на эффекты навигации из ViewModel
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ApplicationDetailEffect.NavigateToUserProfile -> {
                    onNavigateToProfile(effect.userId)
                }
            }
        }
    }

    LaunchedEffect(applicationId) {
        viewModel.onIntent(ApplicationDetailIntent.Load(applicationId))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Application details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.onIntent(ApplicationDetailIntent.Retry) }) {
                Text("Retry")
            }
        }

        state.application?.let { application ->
            DetailRow("ID", application.id)

            // Специальная строка для User ID с кнопкой перехода
            UserDetailRow(
                userId = application.userId,
                onUserClick = {
                    viewModel.onIntent(ApplicationDetailIntent.UserClicked(application.userId))
                }
            )

            DetailRow("Theme ID", application.themeId)
            DetailRow("Kind ID", application.kindId)
            DetailRow("Status ID", application.statusId)
            DetailRow("Title", application.title)
            DetailRow("Description", application.description)
            DetailRow("Created at", application.createdAt)
            DetailRow("Updated at", application.updatedAt)
            DetailRow("Completed at", application.completedAt ?: "-")
            DetailRow("Deleted at", application.deletedAt ?: "-")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onEdit(application.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun UserDetailRow(
    userId: String,
    onUserClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "User ID", style = MaterialTheme.typography.labelMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = userId, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onUserClick) {
                Text("Смотреть профиль")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = label, style = MaterialTheme.typography.labelMedium)
    Text(text = value, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(8.dp))
    Divider()
}