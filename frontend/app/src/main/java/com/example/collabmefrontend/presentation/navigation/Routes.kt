package com.example.collabmefrontend.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val APPLICATIONS = "applications"
    const val APPLICATIONS_DETAILS = "applications/{applicationId}"

    fun applicationDetail(applicationId: String) : String {
        return "applications/$applicationId"
    }
}