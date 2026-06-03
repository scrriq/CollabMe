package com.example.collabmefrontend.presentation.applications.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collabmefrontend.data.repository.CatalogOption
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
            val fieldsEnabled = !state.isLoading && !state.isSaving

            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = fieldsEnabled,
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.DescriptionChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Описание") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = fieldsEnabled,
            )

            ApplicationDropdownField(
                label = "Тема",
                options = state.themeOptions,
                selectedId = state.themeId,
                onSelect = { viewModel.onIntent(ApplicationFormIntent.ThemeChanged(it)) },
                enabled = fieldsEnabled,
                colors = textFieldColors,
            )

            ApplicationDropdownField(
                label = "Тип",
                options = state.kindOptions,
                selectedId = state.kindId,
                onSelect = { viewModel.onIntent(ApplicationFormIntent.KindChanged(it)) },
                enabled = fieldsEnabled,
                colors = textFieldColors,
            )

            ApplicationDropdownField(
                label = "Статус",
                options = state.statusOptions,
                selectedId = state.statusId,
                onSelect = { viewModel.onIntent(ApplicationFormIntent.StatusChanged(it)) },
                enabled = fieldsEnabled,
                colors = textFieldColors,
            )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationDropdownField(
    label: String,
    options: List<CatalogOption>,
    selectedId: String,
    onSelect: (String) -> Unit,
    enabled: Boolean,
    colors: TextFieldColors,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.id == selectedId }?.label ?: "Выберите..."

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = colors,
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelect(option.id)
                        expanded = false
                    },
                )
            }
        }
    }
}
