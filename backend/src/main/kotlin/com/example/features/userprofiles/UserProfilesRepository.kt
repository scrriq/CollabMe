package com.example.features.userprofiles

import com.example.db.tables.UserProfileDirectionsTable
import com.example.db.tables.UserProfilesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

class UserProfilesRepository {
    fun findByUserId(userId: UUID): UserProfileDto? = transaction {
        UserProfilesTable
            .selectAll()
            .where { UserProfilesTable.userId eq userId }
            .limit(1)
            .singleOrNull()
            ?.let(::mapRow)
    }

    fun upsert(
        userId: UUID,
        firstName: String,
        lastName: String,
        middleName: String?,
        birthDate: LocalDate?,
        gender: String?,
        cityId: UUID?,
        universityId: UUID?,
        directionId: UUID?,
        about: String?,
        avatarUrl: String?,
        socialLinks: JsonObject,
    ): UserProfileDto = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val exists =
            UserProfilesTable
                .selectAll()
                .where { UserProfilesTable.userId eq userId }
                .limit(1)
                .singleOrNull() != null

        if (exists) {
            UserProfilesTable.update({ UserProfilesTable.userId eq userId }) {
                it[UserProfilesTable.firstName] = firstName
                it[UserProfilesTable.lastName] = lastName
                it[UserProfilesTable.middleName] = middleName
                it[UserProfilesTable.birthDate] = birthDate
                it[UserProfilesTable.gender] = gender
                it[UserProfilesTable.cityId] = cityId
                it[UserProfilesTable.universityId] = universityId
                it[UserProfilesTable.about] = about
                it[UserProfilesTable.avatarUrl] = avatarUrl
                it[UserProfilesTable.socialLinks] = Json.encodeToString(JsonObject.serializer(), socialLinks)
                it[UserProfilesTable.updatedAt] = now
            }
        } else {
            UserProfilesTable.insert {
                it[UserProfilesTable.userId] = userId
                it[UserProfilesTable.firstName] = firstName
                it[UserProfilesTable.lastName] = lastName
                it[UserProfilesTable.middleName] = middleName
                it[UserProfilesTable.birthDate] = birthDate
                it[UserProfilesTable.gender] = gender
                it[UserProfilesTable.cityId] = cityId
                it[UserProfilesTable.universityId] = universityId
                it[UserProfilesTable.about] = about
                it[UserProfilesTable.avatarUrl] = avatarUrl
                it[UserProfilesTable.socialLinks] = Json.encodeToString(JsonObject.serializer(), socialLinks)
                it[UserProfilesTable.updatedAt] = now
            }
        }

        replaceDirection(userId, directionId)

        UserProfilesTable
            .selectAll()
            .where { UserProfilesTable.userId eq userId }
            .limit(1)
            .single()
            .let(::mapRow)
    }

    fun patch(userId: UUID, ops: UserProfilePatchOps): UserProfileDto? = transaction {
        UserProfilesTable
            .selectAll()
            .where { UserProfilesTable.userId eq userId }
            .limit(1)
            .singleOrNull() ?: return@transaction null

        val now = OffsetDateTime.now(ZoneOffset.UTC)
        UserProfilesTable.update({ UserProfilesTable.userId eq userId }) {
            if (ops.touchFirstName) it[UserProfilesTable.firstName] = ops.firstName!!
            if (ops.touchLastName) it[UserProfilesTable.lastName] = ops.lastName!!
            if (ops.touchMiddleName) it[UserProfilesTable.middleName] = ops.middleName
            if (ops.touchBirthDate) it[UserProfilesTable.birthDate] = ops.birthDate
            if (ops.touchGender) it[UserProfilesTable.gender] = ops.gender
            if (ops.touchCityId) it[UserProfilesTable.cityId] = ops.cityId
            if (ops.touchUniversityId) it[UserProfilesTable.universityId] = ops.universityId
            if (ops.touchAbout) it[UserProfilesTable.about] = ops.about
            if (ops.touchAvatarUrl) it[UserProfilesTable.avatarUrl] = ops.avatarUrl
            if (ops.touchSocialLinks) {
                it[UserProfilesTable.socialLinks] =
                    Json.encodeToString(JsonObject.serializer(), ops.socialLinks!!)
            }
            it[UserProfilesTable.updatedAt] = now
        }

        if (ops.touchDirectionId) {
            replaceDirection(userId, ops.directionId)
        }

        UserProfilesTable
            .selectAll()
            .where { UserProfilesTable.userId eq userId }
            .limit(1)
            .single()
            .let(::mapRow)
    }

    private fun replaceDirection(userId: UUID, directionId: UUID?) {
        UserProfileDirectionsTable.deleteWhere { UserProfileDirectionsTable.userId eq userId }
        if (directionId != null) {
            UserProfileDirectionsTable.insert {
                it[UserProfileDirectionsTable.userId] = userId
                it[UserProfileDirectionsTable.directionId] = directionId
            }
        }
    }

    private fun directionIdForUser(userId: UUID): String? =
        UserProfileDirectionsTable
            .selectAll()
            .where { UserProfileDirectionsTable.userId eq userId }
            .limit(1)
            .singleOrNull()
            ?.get(UserProfileDirectionsTable.directionId)
            ?.toString()

    private fun mapRow(row: ResultRow): UserProfileDto {
        val userId = row[UserProfilesTable.userId]
        return UserProfileDto(
            userId = userId.toString(),
            firstName = row[UserProfilesTable.firstName],
            lastName = row[UserProfilesTable.lastName],
            middleName = row[UserProfilesTable.middleName],
            birthDate = row[UserProfilesTable.birthDate]?.toString(),
            gender = row[UserProfilesTable.gender],
            cityId = row[UserProfilesTable.cityId]?.toString(),
            universityId = row[UserProfilesTable.universityId]?.toString(),
            directionId = directionIdForUser(userId),
            about = row[UserProfilesTable.about],
            avatarUrl = row[UserProfilesTable.avatarUrl],
            socialLinks =
                runCatching {
                    Json.decodeFromString(JsonObject.serializer(), row[UserProfilesTable.socialLinks])
                }.getOrElse { buildJsonObject { } },
            updatedAt = row[UserProfilesTable.updatedAt].toString(),
        )
    }
}
