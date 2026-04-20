package com.example.features.applications

import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import com.example.features.applicationkinds.ApplicationKindsRepository
import com.example.features.applicationstatuses.ApplicationStatusesRepository
import com.example.features.themes.ThemesRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ApplicationsService(
    private val applicationsRepository: ApplicationsRepository,
    private val themesRepository: ThemesRepository,
    private val applicationKindsRepository: ApplicationKindsRepository,
    private val applicationStatusesRepository: ApplicationStatusesRepository,
) {
    fun listVisible(userId: UUID?, excludeCompleted: Boolean): List<ApplicationDto> {
        return applicationsRepository.listVisible(userId, excludeCompleted)
    }

    fun getVisibleById(applicationId: UUID): ApplicationDto {
        return applicationsRepository.findVisibleById(applicationId)
            ?: throw NotFoundException("Application not found")
    }

    fun create(userId: UUID, request: ApplicationCreateRequest): ApplicationDto {
        val title = request.title.trim()
        val description = request.description.trim()
        if (title.isEmpty() || description.isEmpty()) {
            throw ValidationException("title and description are required")
        }

        val themeId = parseUuid(request.themeId, "themeId")
        val kindId = parseUuid(request.kindId, "kindId")
        val statusId = parseUuid(request.statusId, "statusId")

        requireReferenceIds(themeId, kindId, statusId)
        val completedAt = if (isCompletedStatus(statusId)) OffsetDateTime.now(ZoneOffset.UTC) else null

        val id = UUID.randomUUID()
        return applicationsRepository.insert(
            id = id,
            userId = userId,
            themeId = themeId,
            kindId = kindId,
            statusId = statusId,
            title = title,
            description = description,
            completedAt = completedAt,
        )
    }

    fun patch(applicationId: UUID, userId: UUID, request: ApplicationPatchRequest): ApplicationDto {
        val existing = applicationsRepository.findVisibleByIdAndUserId(applicationId, userId)
            ?: throw NotFoundException("Application not found")

        val themeId = request.themeId?.takeIf { it.isNotBlank() }?.let { parseUuid(it, "themeId") }
        val kindId = request.kindId?.takeIf { it.isNotBlank() }?.let { parseUuid(it, "kindId") }
        val statusId = request.statusId?.takeIf { it.isNotBlank() }?.let { parseUuid(it, "statusId") }
        if (request.title != null && request.title.trim().isEmpty()) {
            throw ValidationException("title cannot be blank")
        }
        if (request.description != null && request.description.trim().isEmpty()) {
            throw ValidationException("description cannot be blank")
        }

        val title = request.title?.trim()?.takeIf { it.isNotEmpty() }
        val description = request.description?.trim()?.takeIf { it.isNotEmpty() }

        if (themeId == null && kindId == null && statusId == null && title == null && description == null) {
            throw ValidationException("No fields to update")
        }

        val resolvedTheme = themeId ?: parseUuid(existing.themeId, "themeId")
        val resolvedKind = kindId ?: parseUuid(existing.kindId, "kindId")
        val resolvedStatus = statusId ?: parseUuid(existing.statusId, "statusId")
        requireReferenceIds(resolvedTheme, resolvedKind, resolvedStatus)

        val currentlyCompleted = existing.completedAt != null
        val shouldBeCompleted = isCompletedStatus(resolvedStatus)
        val touchCompletedAt = currentlyCompleted != shouldBeCompleted
        val completedAt = if (shouldBeCompleted) OffsetDateTime.now(ZoneOffset.UTC) else null

        return applicationsRepository.patch(
            id = applicationId,
            userId = userId,
            themeId = themeId,
            kindId = kindId,
            statusId = statusId,
            title = title,
            description = description,
            completedAt = completedAt,
            touchCompletedAt = touchCompletedAt,
        ) ?: throw NotFoundException("Application not found")
    }

    fun softDelete(applicationId: UUID, userId: UUID) {
        val ok = applicationsRepository.softDelete(applicationId, userId)
        if (!ok) {
            throw NotFoundException("Application not found")
        }
    }

    private fun requireReferenceIds(themeId: UUID, kindId: UUID, statusId: UUID) {
        if (themesRepository.findById(themeId) == null) {
            throw ValidationException("themeId not found")
        }
        if (applicationKindsRepository.findById(kindId) == null) {
            throw ValidationException("kindId not found")
        }
        if (applicationStatusesRepository.findById(statusId) == null) {
            throw ValidationException("statusId not found")
        }
    }

    private fun parseUuid(raw: String, field: String): UUID =
        runCatching { UUID.fromString(raw.trim()) }
            .getOrElse { throw ValidationException("Invalid UUID for $field") }

    private fun isCompletedStatus(statusId: UUID): Boolean {
        val status = applicationStatusesRepository.findById(statusId)
            ?: throw ValidationException("statusId not found")
        val code = status.code.trim().lowercase()
        return code == "completed" || code == "done" || code == "closed"
    }
}
