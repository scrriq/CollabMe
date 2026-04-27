package com.example.collabmefrontend.presentation.applications.list

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collabmefrontend.presentation.profile.ProfileIntent
import kotlinx.coroutines.flow.collectLatest


@Composable
fun ApplicationsScreen(
    viewModel: ApplicationsViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ApplicationsIntent.Load)

        viewModel.effect.collectLatest { effect ->
            when(effect){
                is ApplicationEffect.NavigateToDetails -> {
                    onNavigateToDetails(effect.applicationId)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
    ) {

        Text(
            text = "Applications",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onIntent(ApplicationsIntent.Retry) }
            ) {
                Text("Retry")
            }
        }

        if (!state.isLoading && state.error == null && state.applications.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("No applications found")
        }

        if (state.applications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.applications,
                    key = { it.id }
                ) { application ->
                    ApplicationCard(
                        title = application.title,
                        description = application.description,
                        createdAt = application.createdAt,
                        statusId = application.statusId,
                        onClick = {
                            viewModel.onIntent(ApplicationsIntent.ApplicationClicked(application.id))
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun ApplicationCard(
    title: String,
    description: String,
    createdAt: String,
    statusId: String,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{onClick()},
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)){
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status: $statusId", style = MaterialTheme.typography.bodySmall)
            Text(text = "Created: $createdAt", style = MaterialTheme.typography.bodySmall)
        }
    }
}