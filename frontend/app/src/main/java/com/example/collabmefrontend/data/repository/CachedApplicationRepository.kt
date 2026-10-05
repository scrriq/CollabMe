package com.example.collabmefrontend.data.repository

import com.example.collabmefrontend.data.local.CacheJson
import com.example.collabmefrontend.data.local.dao.ApplicationDao
import com.example.collabmefrontend.data.local.dao.FavoriteApplicationDao
import com.example.collabmefrontend.data.remote.dto.ApplicationCreateRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationPatchRequest
import com.example.collabmefrontend.data.remote.dto.ApplicationResponseUserDto
import com.example.collabmefrontend.domain.model.ApplicationItem

class CachedApplicationRepository(
    private val remote: RemoteApplicationRepository,
    private val applicationDao: ApplicationDao,
    private val favoriteApplicationDao: FavoriteApplicationDao,
) : ApplicationRepository {

    override suspend fun getApplications(): List<ApplicationItem> {
        return try {
            val items = remote.getApplications()
            applicationDao.replaceAll(items.map(CacheJson::applicationToEntity))
            items
        } catch (e: Exception) {
            val cached = applicationDao.getAll().map(CacheJson::entityToApplication)
            if (cached.isEmpty()) throw e
            cached
        }
    }

    override suspend fun getMyResponseApplications(): List<ApplicationItem> {
        return try {
            val items = remote.getMyResponseApplications()
            favoriteApplicationDao.replaceAll(items.map(CacheJson::favoriteToEntity))
            items
        } catch (e: Exception) {
            val cached = favoriteApplicationDao.getAll().map(CacheJson::entityToFavorite)
            if (cached.isEmpty()) throw e
            cached
        }
    }

    override suspend fun getApplicationById(applicationId: String): ApplicationItem {
        return try {
            val item = remote.getApplicationById(applicationId)
            applicationDao.upsertAll(listOf(CacheJson.applicationToEntity(item)))
            item
        } catch (e: Exception) {
            applicationDao.getAll()
                .map(CacheJson::entityToApplication)
                .firstOrNull { it.id == applicationId }
                ?: throw e
        }
    }

    override suspend fun hasResponded(applicationId: String): Boolean {
        return remote.hasResponded(applicationId)
    }

    override suspend fun respondToApplication(applicationId: String): ApplicationItem {
        val item = remote.respondToApplication(applicationId)
        favoriteApplicationDao.upsertAll(listOf(CacheJson.favoriteToEntity(item)))
        return item
    }

    override suspend fun withdrawResponse(applicationId: String) {
        remote.withdrawResponse(applicationId)
    }

    override suspend fun listApplicationResponders(applicationId: String): List<ApplicationResponseUserDto> {
        return remote.listApplicationResponders(applicationId)
    }

    override suspend fun createApplication(request: ApplicationCreateRequest): ApplicationItem {
        val item = remote.createApplication(request)
        applicationDao.upsertAll(listOf(CacheJson.applicationToEntity(item)))
        return item
    }

    override suspend fun patchApplication(
        applicationId: String,
        request: ApplicationPatchRequest,
    ): ApplicationItem {
        val item = remote.patchApplication(applicationId, request)
        applicationDao.upsertAll(listOf(CacheJson.applicationToEntity(item)))
        return item
    }
}
