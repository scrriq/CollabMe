package com.example.collabmefrontend.presentation.applications.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationFormScreen(
    viewModel: ApplicationFormViewModel,
    applicationId: String? = null,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(applicationId) {
        viewModel.onIntent(ApplicationFormIntent.Load(applicationId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ApplicationFormEffect.Saved -> onClose()
                is ApplicationFormEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.id == null) "Новая заявка" else "Изменить заявку",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF0D47A1)
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF1976D2))
            }

            state.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                focusedLabelColor = Color(0xFF1976D2)
            )

            // Заголовок
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            // Описание
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.DescriptionChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Описание") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            // Kind ID (вынесено отдельной строкой с иконкой)
            OutlinedTextField(
                value = state.kindId,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.KindChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Kind ID") },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            // Остальные ID в одну строку для экономии места
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.themeId,
                    onValueChange = { viewModel.onIntent(ApplicationFormIntent.ThemeChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Theme ID") },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = state.statusId,
                    onValueChange = { viewModel.onIntent(ApplicationFormIntent.StatusChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Status ID") },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onIntent(ApplicationFormIntent.SaveClicked) },
                enabled = !state.isSaving && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Сохранить", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}