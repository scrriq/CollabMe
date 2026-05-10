package com.example.collabmefrontend.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val PUBLIC_PROFILE = "public_profile/{userId}"
    const val APPLICATIONS = "applications"
    const val APPLICATIONS_DETAILS = "applications/{applicationId}"
    const val APPLICATION_CREATE = "applications/create"
    const val APPLICATION_EDIT = "applications/edit/{applicationId}"

    // Служебный роут для проверки авторизации
    const val AUTH_CHECK = "auth_check"

    fun applicationDetail(applicationId: String) = "applications/$applicationId"
    fun applicationEdit(applicationId: String) = "applications/edit/$applicationId"
    fun publicProfile(userId: String) = "public_profile/$userId"
}

// Модель для пунктов нижнего меню
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Applications : BottomNavItem(Routes.APPLICATIONS, Icons.Default.List, "Проекты")
    object Profile : BottomNavItem(Routes.PROFILE, Icons.Default.Person, "Профиль")
}