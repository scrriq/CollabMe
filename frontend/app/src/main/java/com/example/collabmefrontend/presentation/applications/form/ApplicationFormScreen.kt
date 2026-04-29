package com.example.collabmefrontend.presentation.applications.form

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        },
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = state.themeId,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.ThemeChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Theme ID") },
                singleLine = true,
            )

            OutlinedTextField(
                value = state.kindId,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.KindChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Kind ID") },
                singleLine = true,
            )

            OutlinedTextField(
                value = state.statusId,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.StatusChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Status ID") },
                singleLine = true,
            )

            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок") },
                singleLine = true,
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onIntent(ApplicationFormIntent.DescriptionChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Описание") },
                minLines = 4,
            )

            Button(
                onClick = { viewModel.onIntent(ApplicationFormIntent.SaveClicked) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) "Сохранение..." else "Сохранить")
            }

            TextButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Отмена")
            }
        }
    }
}