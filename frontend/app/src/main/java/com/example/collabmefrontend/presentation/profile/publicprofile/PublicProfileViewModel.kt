package com.example.collabmefrontend.presentation.profile.publicprofile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.remote.api.ApiException
import com.example.collabmefrontend.data.repository.ProfileCatalogRepository
import com.example.collabmefrontend.data.repository.ProfileRepository
import com.example.collabmefrontend.domain.model.UserProfile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.Period

class PublicProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val profileCatalogRepository: ProfileCatalogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PublicProfileState())
    val state: StateFlow<PublicProfileState> = _state.asStateFlow()

    private val _effect = Channel<PublicProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var currentUserId: String? = null
    private var currentIsOwnProfile: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    fun onIntent(intent: PublicProfileIntent) {
        when (intent) {
            is PublicProfileIntent.Load -> {
                currentUserId = intent.userId
                currentIsOwnProfile = intent.isOwnProfile
                load(intent.userId, intent.isOwnProfile)
            }

            PublicProfileIntent.Retry -> {
                val id = currentUserId ?: return
                load(id, currentIsOwnProfile)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun load(userId: String, isOwnProfile: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val cities = runCatching { profileCatalogRepository.getCities() }.getOrDefault(emptyList())
                val universities = runCatching { profileCatalogRepository.getUniversities() }.getOrDefault(emptyList())

                val user: UserProfile =
                    profileRepository.getProfileByUserId(userId)
                        ?: throw IllegalStateException("User profile not found")

                val universityText =
                    universities.firstOrNull { it.id == user.universityId }?.label ?: "University not specified"

                val ageText = user.birthDate
                    ?.takeIf { it.isNotBlank() }
                    ?.let { calculateAgeText(it) }
                    ?: ""

                val displayName = buildDisplayName(user)
                val descriptionText = user.about?.takeIf { it.isNotBlank() } ?: "No description"

                val contacts = user.socialLinks.entries.map { entry ->
                    PublicProfileContactUiModel(
                        platform = entry.key.replaceFirstChar { ch ->
                            if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                        },
                        url = entry.value.jsonPrimitive.content
                    )
                }


                _state.value = _state.value.copy(
                    user = user,
                    isOwnProfile = isOwnProfile,
                    displayName = displayName,
                    ageText = ageText,
                    universityText = universityText,
                    professionText = "Android разработчик",
                    descriptionText = descriptionText,
                    contacts = contacts,
                    isLoading = false,
                    error = null
                )
            } catch (e: ApiException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"

                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    private fun buildDisplayName(user: UserProfile): String {
        val parts = buildList {
            user.lastName.takeIf { it.isNotBlank() }?.let { add(it) }
            user.firstName.takeIf { it.isNotBlank() }?.let { add(it) }
            user.middleName?.takeIf { it.isNotBlank() }?.let { add(it) }
        }
        return parts.joinToString(" ")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAgeText(birthDate: String): String {
        val parsed = runCatching { LocalDate.parse(birthDate) }.getOrNull() ?: return ""
        val age = Period.between(parsed, LocalDate.now()).years
        return if (age >= 0) "$age" else ""
    }
}