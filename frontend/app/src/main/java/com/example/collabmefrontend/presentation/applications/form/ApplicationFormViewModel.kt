package com.example.collabmefrontend.presentation.applications.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collabmefrontend.data.remote.dto.ApplicationCreateRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationPatchRequest
import com.example.collabmefrontend.data.repository.ApplicationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ApplicationFormViewModel(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ApplicationFormState())
    val state: StateFlow<ApplicationFormState> = _state.asStateFlow()

    private val _effect = Channel<ApplicationFormEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ApplicationFormIntent) {
        when (intent) {
            is ApplicationFormIntent.Load -> load(intent.applicationId)
            is ApplicationFormIntent.TitleChanged -> update { it.copy(title = intent.value) }
            is ApplicationFormIntent.DescriptionChanged -> update { it.copy(description = intent.value) }
            is ApplicationFormIntent.ThemeChanged -> update { it.copy(themeId = intent.value) }
            is ApplicationFormIntent.KindChanged -> update { it.copy(kindId = intent.value) }
            is ApplicationFormIntent.StatusChanged -> update { it.copy(statusId = intent.value) }
            ApplicationFormIntent.SaveClicked -> save()
        }
    }

    private fun load(applicationId: String?) {
        if (applicationId == null) {
            _state.value = ApplicationFormState()
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val item = applicationRepository.getApplicationById(applicationId)
                _state.value = _state.value.copy(
                    id = item.id,
                    themeId = item.themeId,
                    kindId = item.kindId,
                    statusId = item.statusId,
                    title = item.title,
                    description = item.description,
                    isLoading = false,
                    error = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Не удалось загрузить заявку",
                )
            }
        }
    }

    private fun save() {
        val current = _state.value

        if (
            current.themeId.isBlank() ||
            current.kindId.isBlank() ||
            current.statusId.isBlank() ||
            current.title.isBlank() ||
            current.description.isBlank()
        ) {
            viewModelScope.launch {
                _effect.send(
                    ApplicationFormEffect.ShowMessage(
                        "Заполни themeId, kindId, statusId, заголовок и описание"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)

            try {
                if (current.id == null) {
                    applicationRepository.createApplication(
                        ApplicationCreateRequest(
                            themeId = current.themeId,
                            kindId = current.kindId,
                            statusId = current.statusId,
                            title = current.title,
                            description = current.description,
                        )
                    )
                } else {
                    applicationRepository.patchApplication(
                        current.id,
                        ApplicationPatchRequest(
                            themeId = current.themeId,
                            kindId = current.kindId,
                            statusId = current.statusId,
                            title = current.title,
                            description = current.description,
                        )
                    )
                }

                _state.value = _state.value.copy(isSaving = false)
                _effect.send(ApplicationFormEffect.Saved)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message ?: "Не удалось сохранить заявку",
                )
            }
        }
    }

    private inline fun update(block: (ApplicationFormState) -> ApplicationFormState) {
        _state.value = block(_state.value)
    }
}