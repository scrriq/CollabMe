package com.example.collabmefrontend.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collabmefrontend.data.repository.CatalogOption
import com.example.collabmefrontend.domain.model.SocialLinkUiModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.layout.ColumnScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileIntent.Load)
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isFirstRegistration) "Complete profile" else "Profile",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeaderCard(
                    isFirstRegistration = state.isFirstRegistration,
                    userId = state.user?.userId
                )
            }

            if (state.isLoading) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Loading profile...")
                        }
                    }
                }
            }

            state.error?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            item {
                SectionCard(title = "Personal information") {
                    ProfileTextField(
                        value = state.firstName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.FirstNameChanged(it)) },
                        label = "First name *",
                        enabled = !state.isSaving
                    )

                    ProfileTextField(
                        value = state.lastName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.LastNameChanged(it)) },
                        label = "Last name *",
                        enabled = !state.isSaving
                    )

                    ProfileTextField(
                        value = state.middleName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.MiddleNameChanged(it)) },
                        label = "Middle name (optional)",
                        enabled = !state.isSaving
                    )

                    ProfileTextField(
                        value = state.birthDate,
                        onValueChange = { viewModel.onIntent(ProfileIntent.BirthDateChanged(it)) },
                        label = "Birth date YYYY-MM-DD (optional)",
                        enabled = !state.isSaving
                    )
                }
            }

            item {
                SectionCard(title = "Classification") {
                    ProfileDropdownField(
                        label = "Gender",
                        options = listOf(
                            null to "Not stated",
                            "male" to "Male",
                            "female" to "Female"
                        ),
                        selectedValue = state.gender,
                        onSelect = { viewModel.onIntent(ProfileIntent.GenderChanged(it)) },
                        enabled = !state.isSaving
                    )

                    ProfileDropdownField(
                        label = "City",
                        options = cityOptionsWithNotStated(state.cityOptions),
                        selectedValue = state.cityId,
                        onSelect = { viewModel.onIntent(ProfileIntent.CityIdChanged(it)) },
                        enabled = !state.isSaving
                    )

                    ProfileDropdownField(
                        label = "University",
                        options = universityOptionsWithNotStated(state.universityOptions),
                        selectedValue = state.universityId,
                        onSelect = { viewModel.onIntent(ProfileIntent.UniversityIdChanged(it)) },
                        enabled = !state.isSaving
                    )
                }
            }

            item {
                SectionCard(title = "About & avatar") {
                    ProfileTextField(
                        value = state.about,
                        onValueChange = { viewModel.onIntent(ProfileIntent.AboutChanged(it)) },
                        label = "About (optional)",
                        enabled = !state.isSaving
                    )

                    ProfileTextField(
                        value = state.avatarUrl,
                        onValueChange = { viewModel.onIntent(ProfileIntent.AvatarUrlChanged(it)) },
                        label = "Avatar URL (optional)",
                        enabled = !state.isSaving
                    )
                }
            }

            item {
                SectionCard(
                    title = "Social links",
                    subtitle = "Add as many links as you need"
                ) {
                    state.socialLinks.forEachIndexed { index, link ->
                        SocialLinkRow(
                            index = index,
                            item = link,
                            enabled = !state.isSaving,
                            canRemove = state.socialLinks.size > 1,
                            onPlatformChange = {
                                viewModel.onIntent(ProfileIntent.SocialPlatformChanged(index, it))
                            },
                            onUrlChange = {
                                viewModel.onIntent(ProfileIntent.SocialUrlChanged(index, it))
                            },
                            onRemove = {
                                viewModel.onIntent(ProfileIntent.RemoveSocialLink(index))
                            }
                        )
                    }

                    TextButton(
                        onClick = { viewModel.onIntent(ProfileIntent.AddSocialLink) },
                        enabled = !state.isSaving
                    ) {
                        Text("+ Add social link")
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.onIntent(ProfileIntent.Save) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                ) {
                    Text(if (state.isFirstRegistration) "Create profile" else "Save changes")
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.onIntent(ProfileIntent.Logout) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }

            if (state.isSaving) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.width(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Saving...")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    isFirstRegistration: Boolean,
    userId: String?
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isFirstRegistration) "Finish your profile" else "Profile settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = if (isFirstRegistration) {
                    "Fill in the required fields to continue."
                } else {
                    "You can update your information at any time."
                },
                style = MaterialTheme.typography.bodyMedium
            )
            if (!userId.isNullOrBlank()) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = "User ID: $userId",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.padding(top = 2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.padding(top = 12.dp))
            content()
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
    Spacer(modifier = Modifier.padding(top = 8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDropdownField(
    label: String,
    options: List<Pair<String?, String>>,
    selectedValue: String?,
    onSelect: (String?) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second ?: "Not stated"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SocialLinkRow(
    index: Int,
    item: SocialLinkUiModel,
    enabled: Boolean,
    canRemove: Boolean,
    onPlatformChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Link ${index + 1}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            OutlinedTextField(
                value = item.platform,
                onValueChange = onPlatformChange,
                label = { Text("Social network") },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            OutlinedTextField(
                value = item.url,
                onValueChange = onUrlChange,
                label = { Text("Profile URL") },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            )

            if (canRemove) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                TextButton(
                    onClick = onRemove,
                    enabled = enabled
                ) {
                    Text("Remove")
                }
            }
        }
    }

    Spacer(modifier = Modifier.padding(top = 8.dp))
}

private fun cityOptionsWithNotStated(options: List<CatalogOption>): List<Pair<String?, String>> {
    return buildList {
        add(null to "Not stated")
        options.forEach { add(it.id to it.label) }
    }
}

private fun universityOptionsWithNotStated(options: List<CatalogOption>): List<Pair<String?, String>> {
    return buildList {
        add(null to "Not stated")
        options.forEach { add(it.id to it.label) }
    }
}