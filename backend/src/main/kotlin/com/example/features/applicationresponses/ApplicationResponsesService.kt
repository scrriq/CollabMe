package com.example.features.applicationresponses

import com.example.app.plugins.ForbiddenException
import com.example.app.plugins.NotFoundException
import com.example.app.plugins.ValidationException
import com.example.features.applications.ApplicationDto
import com.example.features.applications.ApplicationsRepository
import java.util.UUID

class ApplicationResponsesService(
    private val applicationResponsesRepository: ApplicationResponsesRepository,
    private val applicationsRepository: ApplicationsRepository,
) {
    fun respond(userId: UUID, applicationId: UUID): ApplicationDto {
        val application = applicationsRepository.findVisibleById(applicationId)
            ?: throw NotFoundException("Application not found")

        if (application.userId == userId.toString()) {
            throw ValidationException("Cannot respond to your own application")
        }

        applicationResponsesRepository.insert(userId, applicationId)
        return application
    }

    fun withdraw(userId: UUID, applicationId: UUID) {
        if (!applicationResponsesRepository.delete(userId, applicationId)) {
            throw NotFoundException("Response not found")
        }
    }

    fun listMyResponseApplications(userId: UUID): List<ApplicationDto> {
        val applicationIds = applicationResponsesRepository.listApplicationIdsForUser(userId)
        return applicationsRepository.listVisibleByIds(applicationIds)
    }

    fun listResponders(applicationId: UUID, ownerUserId: UUID): List<ApplicationResponseUserDto> {
        applicationsRepository.findVisibleByIdAndUserId(applicationId, ownerUserId)
            ?: throw ForbiddenException("Only the application owner can view responders")

        return applicationResponsesRepository.listResponders(applicationId)
    }

    fun hasResponded(userId: UUID, applicationId: UUID): Boolean {
        return applicationResponsesRepository.exists(userId, applicationId)
    }
}
