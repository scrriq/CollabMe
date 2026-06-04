package com.example.collabmefrontend.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.mapper.toJsonObjectOrNull
import com.example.collabmefrontend.data.mapper.toSocialLinksUiModel
import com.example.collabmefrontend.data.remote.api.ApiException
import com.example.collabmefrontend.data.repository.ProfileCatalogRepository
import com.example.collabmefrontend.data.repository.ProfileRepository
import com.example.collabmefrontend.data.repository.RemoteAuthRepository
import com.example.collabmefrontend.domain.model.SocialLinkUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val profileCatalogRepository: ProfileCatalogRepository,
    private val authRepository: RemoteAuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> load()
            is ProfileIntent.FirstNameChanged -> _state.value = _state.value.copy(firstName = intent.value, error = null)
            is ProfileIntent.LastNameChanged -> _state.value = _state.value.copy(lastName = intent.value, error = null)
            is ProfileIntent.MiddleNameChanged -> _state.value = _state.value.copy(middleName = intent.value, error = null)
            is ProfileIntent.BirthDateChanged -> _state.value = _state.value.copy(birthDate = intent.value, error = null)
            is ProfileIntent.GenderChanged -> _state.value = _state.value.copy(gender = intent.value ?: "", error = null)
            is ProfileIntent.CityIdChanged -> _state.value = _state.value.copy(cityId = intent.value ?: "", error = null)
            is ProfileIntent.UniversityIdChanged -> _state.value = _state.value.copy(universityId = intent.value ?: "", error = null)
            is ProfileIntent.DirectionChanged -> _state.value = _state.value.copy(directionId = intent.value ?: "", error = null)
            is ProfileIntent.AboutChanged -> _state.value = _state.value.copy(about = intent.value, error = null)
            is ProfileIntent.AvatarUrlChanged -> _state.value = _state.value.copy(avatarUrl = intent.value, error = null)

            ProfileIntent.AddSocialLink -> addSocialLink()
            is ProfileIntent.RemoveSocialLink -> removeSocialLink(intent.index)
            is ProfileIntent.SocialPlatformChanged -> updateSocialPlatform(intent.index, intent.value)
            is ProfileIntent.SocialUrlChanged -> updateSocialUrl(intent.index, intent.value)

            ProfileIntent.Save -> save()
            ProfileIntent.Logout -> logout()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val cityOptions = runCatching { profileCatalogRepository.getCities() }.getOrDefault(emptyList())
                val universityOptions = runCatching { profileCatalogRepository.getUniversities() }.getOrDefault(emptyList())
                val directionOptions = runCatching { profileCatalogRepository.getDirections() }.getOrDefault(emptyList())
                val user = profileRepository.getMyProfileOrNull()
                if (user == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isFirstRegistration = true,
                        user = null,
                        firstName = "",
                        lastName = "",
                        middleName = "",
                        birthDate = "",
                        gender = "",
                        cityId = "",
                        universityId = "",
                        directionId = "",
                        about = "",
                        avatarUrl = "",
                        socialLinks = listOf(SocialLinkUiModel()),
                        cityOptions = cityOptions,
                        universityOptions = universityOptions,
                        directionOptions = directionOptions,
                        error = null
                    )
                    return@launch
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    user = user,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    middleName = user.middleName ?: "",
                    birthDate = user.birthDate ?: "",
                    gender = user.gender ?: "",
                    cityId = user.cityId ?: "",
                    universityId = user.universityId ?: "",
                    directionId = user.directionId ?: "",
                    about = user.about ?: "",
                    avatarUrl = user.avatarUrl ?: "",
                    socialLinks = user.socialLinks.toSocialLinksUiModel(),
                    cityOptions = cityOptions,
                    universityOptions = universityOptions,
                    directionOptions = directionOptions,
                    isFirstRegistration = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val current = _state.value
            if (current.firstName.isBlank() || current.lastName.isBlank()) {
                _state.value = current.copy(error = "First name and last name are required")
                return@launch
            }

            val socialLinks: JsonObject = try {
                current.socialLinks.toJsonObjectOrNull() ?: buildJsonObject { }
            } catch (e: IllegalArgumentException) {
                _state.value = current.copy(error = e.message)
                return@launch
            }

            _state.value = current.copy(isSaving = true, error = null)

            try {
                val saved = if (current.isFirstRegistration) {
                    profileRepository.createMyProfile(
                        firstName = current.firstName.trim(),
                        lastName = current.lastName.trim(),
                        middleName = current.middleName.trim().takeIf { it.isNotBlank() },
                        birthDate = current.birthDate.trim().takeIf { it.isNotBlank() },
                        gender = current.gender.trim().takeIf { it.isNotBlank() },
                        cityId = current.cityId.trim().takeIf { it.isNotBlank() },
                        universityId = current.universityId.trim().takeIf { it.isNotBlank() },
                        directionId = current.directionId.trim().takeIf { it.isNotBlank() },
                        about = current.about.trim().takeIf { it.isNotBlank() },
                        avatarUrl = current.avatarUrl.trim().takeIf { it.isNotBlank() },
                        socialLinks = socialLinks
                    )
                } else {
                    profileRepository.updateMyProfile(
                        firstName = current.firstName.trim(),
                        lastName = current.lastName.trim(),
                        middleName = current.middleName.trim().takeIf { it.isNotBlank() },
                        birthDate = current.birthDate.trim().takeIf { it.isNotBlank() },
                        gender = current.gender.trim().takeIf { it.isNotBlank() },
                        cityId = current.cityId.trim().takeIf { it.isNotBlank() },
                        universityId = current.universityId.trim().takeIf { it.isNotBlank() },
                        directionId = current.directionId.trim().takeIf { it.isNotBlank() },
                        about = current.about.trim().takeIf { it.isNotBlank() },
                        avatarUrl = current.avatarUrl.trim().takeIf { it.isNotBlank() },
                        socialLinks = socialLinks
                    )
                }

                _state.value = _state.value.copy(
                    user = saved,
                    directionId = saved.directionId ?: current.directionId,
                    isFirstRegistration = false,
                    isSaving = false,
                    error = null,
                    socialLinks = saved.socialLinks.toSocialLinksUiModel()
                )

                _effect.send(ProfileEffect.NavigateToPublicProfile(saved.userId))
            } catch (e: ApiException) {
                Log.e("ProfileSave", "ApiException while saving profile", e)
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message ?: "Request failed"
                )
            } catch (e: Exception) {
                Log.e("ProfileSave", "Unexpected exception while saving profile", e)
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save profile"
                )
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _effect.send(ProfileEffect.NavigateToLogin)
        }
    }

    private fun addSocialLink() {
        _state.value = _state.value.copy(
            socialLinks = _state.value.socialLinks + SocialLinkUiModel()
        )
    }

    private fun removeSocialLink(index: Int) {
        val currentList = _state.value.socialLinks.toMutableList()
        if (currentList.size <= 1) {
            currentList[0] = SocialLinkUiModel()
        } else if (index in currentList.indices) {
            currentList.removeAt(index)
        }
        _state.value = _state.value.copy(socialLinks = currentList)
    }

    private fun updateSocialPlatform(index: Int, value: String) {
        val currentList = _state.value.socialLinks.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(platform = value)
            _state.value = _state.value.copy(socialLinks = currentList, error = null)
        }
    }

    private fun updateSocialUrl(index: Int, value: String) {
        val currentList = _state.value.socialLinks.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(url = value)
            _state.value = _state.value.copy(socialLinks = currentList, error = null)
        }
    }
}
