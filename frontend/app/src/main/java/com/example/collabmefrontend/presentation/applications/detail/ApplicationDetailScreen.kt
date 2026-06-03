package com.example.collabmefrontend.presentation.applications.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    applicationId: String,
    viewModel: ApplicationDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ApplicationDetailEffect.NavigateToUserProfile -> onNavigateToProfile(effect.userId)
            }
        }
    }

    LaunchedEffect(applicationId) {
        viewModel.onIntent(ApplicationDetailIntent.Load(applicationId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали заявки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    state.application?.let {
                        IconButton(onClick = { onEdit(it.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1976D2))
            }
        }

        state.application?.let { app ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Главная карточка с заголовком
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = app.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = app.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Инфо о пользователе
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1976D2))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Автор (ID)", style = MaterialTheme.typography.labelMedium)
                            Text(app.userId, style = MaterialTheme.typography.bodyMedium)
                        }
                        TextButton(onClick = { viewModel.onIntent(ApplicationDetailIntent.UserClicked(app.userId)) }) {
                            Text("Профиль", color = Color(0xFF1976D2))
                        }
                    }
                }

                // Технические детали
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailItem("Статус", app.status.title)
                        DetailItem("Тип", app.kind.title)
                        DetailItem("Тема", app.theme.name)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFBBDEFB))
                        DetailItem("Создано", app.createdAt)
                        DetailItem("Обновлено", app.updatedAt)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}