package com.example.collabmefrontend.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileIntent.Load)

        viewModel.effect.collectLatest { effect ->
            when(effect){
                ProfileEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile")

        Spacer(modifier = Modifier.height(16.dp))

        if(state.isLoading){
            CircularProgressIndicator()
            return@Column
        }

        if (state.isFirstRegistration) {
            Text("Complete your profile to unlock interactions with applications.")
            Text("Required fields are marked with *")
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Text("Edit your profile")
            Text("Required fields are marked with *")
            Spacer(modifier = Modifier.height(8.dp))
            state.user?.let { user ->
                Text("User ID: ${user.userId}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        OutlinedTextField(
            value = state.firstName,
            onValueChange = { viewModel.onIntent(ProfileIntent.FirstNameChanged(it)) },
            label = { Text("First name *") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.lastName,
            onValueChange = { viewModel.onIntent(ProfileIntent.LastNameChanged(it)) },
            label = { Text("Last name *") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.middleName,
            onValueChange = { viewModel.onIntent(ProfileIntent.MiddleNameChanged(it)) },
            label = { Text("Middle name (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.birthDate,
            onValueChange = { viewModel.onIntent(ProfileIntent.BirthDateChanged(it)) },
            label = { Text("Birth date YYYY-MM-DD (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.gender,
            onValueChange = { viewModel.onIntent(ProfileIntent.GenderChanged(it)) },
            label = { Text("Gender: male/female (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.cityId,
            onValueChange = { viewModel.onIntent(ProfileIntent.CityIdChanged(it)) },
            label = { Text("City ID (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.universityId,
            onValueChange = { viewModel.onIntent(ProfileIntent.UniversityIdChanged(it)) },
            label = { Text("University ID (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.about,
            onValueChange = { viewModel.onIntent(ProfileIntent.AboutChanged(it)) },
            label = { Text("About (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.avatarUrl,
            onValueChange = { viewModel.onIntent(ProfileIntent.AvatarUrlChanged(it)) },
            label = { Text("Avatar URL (optional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.socialLinks,
            onValueChange = { viewModel.onIntent(ProfileIntent.SocialLinksChanged(it)) },
            label = { Text("Social links JSON *") },
            supportingText = { Text("Example: {} or {\"telegram\":\"@username\"}") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.onIntent(ProfileIntent.Save) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        ) {
            Text(if (state.isFirstRegistration) "Create profile" else "Save changes")
        }

        if (state.isSaving) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator()
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {viewModel.onIntent(ProfileIntent.Logout)},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}