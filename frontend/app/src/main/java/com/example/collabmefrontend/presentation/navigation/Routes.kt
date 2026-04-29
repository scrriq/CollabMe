package com.example.collabmefrontend.presentation.navigation

object Routes {

    const val LOGIN = "login"
    const val REGISTER = "register"

    const val PROFILE = "profile"
    const val PUBLIC_PROFILE = "public_profile/{userId}"

    const val APPLICATIONS = "applications"

    const val APPLICATIONS_DETAILS =
        "applications/{applicationId}"

    const val APPLICATION_CREATE =
        "applications/create"

    const val APPLICATION_EDIT =
        "applications/edit/{applicationId}"

    fun applicationDetail(applicationId: String): String {
        return "applications/$applicationId"
    }

    fun applicationEdit(applicationId: String): String {
        return "applications/edit/$applicationId"
    }

    fun publicProfile(userId: String): String {
        return "public_profile/$userId"
    }
}