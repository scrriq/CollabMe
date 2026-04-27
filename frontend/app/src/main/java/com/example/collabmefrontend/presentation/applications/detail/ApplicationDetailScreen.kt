package com.example.collabmefrontend.presentation.applications.detail

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collabmefrontend.presentation.applications.list.ApplicationsIntent

@Composable
fun ApplicationDetailScreen(
    applicationId: String,
    viewModel: ApplicationDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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

        if(state.isLoading){
            CircularProgressIndicator()
        }

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {viewModel.onIntent(ApplicationDetailIntent.Retry)}) {
                Text("Retry")
            }
        }

        state.application?.let { application ->
            DetailRow("ID", application.id)
            DetailRow("User ID", application.userId)
            DetailRow("Theme ID", application.themeId)
            DetailRow("Kind ID", application.kindId)
            DetailRow("Status ID", application.statusId)
            DetailRow("Title", application.title)
            DetailRow("Description", application.description)
            DetailRow("Created at", application.createdAt)
            DetailRow("Updated at", application.updatedAt)
            DetailRow("Completed at", application.completedAt ?: "-")
            DetailRow("Deleted at", application.deletedAt ?: "-")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }



    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = label, style = MaterialTheme.typography.labelMedium)
    Text(text = value, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(8.dp))
    Divider()
}