package com.example.collabmefrontend.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collabmefrontend.data.repository.CatalogOption
import com.example.collabmefrontend.domain.model.SocialLinkUiModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToPublicProfile: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileIntent.Load)
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateToLogin -> onNavigateToLogin()
                is ProfileEffect.NavigateToPublicProfile -> onNavigateToPublicProfile(effect.userId)
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF1976D2),
        focusedLabelColor = Color(0xFF1976D2),
        cursorColor = Color(0xFF1976D2)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isFirstRegistration) "Завершение регистрации" else "Настройки профиля",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D47A1),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Приветственный заголовок
            item {
                ProfileHeaderCard(
                    isFirstRegistration = state.isFirstRegistration,
                    userId = state.user?.userId
                )
            }

            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    }
                }
            }

            state.error?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(text = error, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            // Раздел: Личная информация
            item {
                SectionCard(title = "Личные данные", icon = Icons.Default.Person) {
                    ProfileTextField(
                        value = state.firstName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.FirstNameChanged(it)) },
                        label = "Имя *",
                        enabled = !state.isSaving,
                        colors = textFieldColors
                    )
                    ProfileTextField(
                        value = state.lastName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.LastNameChanged(it)) },
                        label = "Фамилия *",
                        enabled = !state.isSaving,
                        colors = textFieldColors
                    )
                    ProfileTextField(
                        value = state.birthDate,
                        onValueChange = { viewModel.onIntent(ProfileIntent.BirthDateChanged(it)) },
                        label = "Дата рождения (ГГГГ-ММ-ДД)",
                        enabled = !state.isSaving,
                        colors = textFieldColors
                    )
                }
            }

            // Раздел: Классификация
            item {
                SectionCard(title = "Место обучения", icon = Icons.Default.School) {
                    ProfileDropdownField(
                        label = "Город",
                        options = cityOptionsWithNotStated(state.cityOptions),
                        selectedValue = state.cityId,
                        onSelect = { viewModel.onIntent(ProfileIntent.CityIdChanged(it)) },
                        enabled = !state.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileDropdownField(
                        label = "Университет",
                        options = universityOptionsWithNotStated(state.universityOptions),
                        selectedValue = state.universityId,
                        onSelect = { viewModel.onIntent(ProfileIntent.UniversityIdChanged(it)) },
                        enabled = !state.isSaving
                    )
                }
            }

            // Раздел: О себе
            item {
                SectionCard(title = "О себе и аватар", icon = Icons.Default.Info) {
                    ProfileTextField(
                        value = state.about,
                        onValueChange = { viewModel.onIntent(ProfileIntent.AboutChanged(it)) },
                        label = "Краткая информация",
                        enabled = !state.isSaving,
                        colors = textFieldColors,
                        singleLine = false
                    )
                    ProfileTextField(
                        value = state.avatarUrl,
                        onValueChange = { viewModel.onIntent(ProfileIntent.AvatarUrlChanged(it)) },
                        label = "Ссылка на аватар",
                        enabled = !state.isSaving,
                        colors = textFieldColors
                    )
                }
            }

            // Раздел: Социальные сети
            item {
                SectionCard(
                    title = "Социальные сети",
                    icon = Icons.Default.Link,
                    subtitle = "Добавьте ссылки на ваши профили"
                ) {
                    state.socialLinks.forEachIndexed { index, link ->
                        SocialLinkRow(
                            index = index,
                            item = link,
                            enabled = !state.isSaving,
                            canRemove = state.socialLinks.size > 1,
                            onPlatformChange = { viewModel.onIntent(ProfileIntent.SocialPlatformChanged(index, it)) },
                            onUrlChange = { viewModel.onIntent(ProfileIntent.SocialUrlChanged(index, it)) },
                            onRemove = { viewModel.onIntent(ProfileIntent.RemoveSocialLink(index)) },
                            colors = textFieldColors
                        )
                    }

                    TextButton(
                        onClick = { viewModel.onIntent(ProfileIntent.AddSocialLink) },
                        enabled = !state.isSaving,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1976D2))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Добавить ссылку")
                    }
                }
            }

            // Кнопки управления
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.onIntent(ProfileIntent.Save) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !state.isSaving,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (state.isFirstRegistration) "Создать профиль" else "Сохранить изменения")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.onIntent(ProfileIntent.Logout) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Выйти из аккаунта")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(isFirstRegistration: Boolean, userId: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (isFirstRegistration) "Добро пожаловать!" else "Ваш профиль",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
            Text(
                text = if (isFirstRegistration) "Заполните данные, чтобы начать поиск проектов." else "Здесь вы можете изменить информацию о себе.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            if (!userId.isNullOrBlank()) {
                Surface(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = "ID: $userId",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
        }
        subtitle?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    colors: TextFieldColors,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        singleLine = singleLine
    )
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
    val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second ?: "Не указано"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                focusedLabelColor = Color(0xFF1976D2)
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
    onRemove: () -> Unit,
    colors: TextFieldColors
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ProfileTextField(value = item.platform, onValueChange = onPlatformChange, label = "Сеть (напр. Telegram)", enabled = enabled, colors = colors)
            ProfileTextField(value = item.url, onValueChange = onUrlChange, label = "Ссылка на профиль", enabled = enabled, colors = colors)
            if (canRemove) {
                TextButton(onClick = onRemove, enabled = enabled, modifier = Modifier.align(Alignment.End)) {
                    Text("Удалить", color = Color(0xFFD32F2F))
                }
            }
        }
    }
}

private fun cityOptionsWithNotStated(options: List<CatalogOption>): List<Pair<String?, String>> {
    return buildList {
        add(null to "Не указано")
        options.forEach { add(it.id to it.label) }
    }
}

private fun universityOptionsWithNotStated(options: List<CatalogOption>): List<Pair<String?, String>> {
    return buildList {
        add(null to "Не указано")
        options.forEach { add(it.id to it.label) }
    }
}