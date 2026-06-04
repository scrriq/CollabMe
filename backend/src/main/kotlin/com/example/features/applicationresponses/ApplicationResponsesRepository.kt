package com.example.features.applicationresponses

import com.example.db.tables.ApplicationResponsesTable
import com.example.db.tables.UserProfilesTable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ApplicationResponsesRepository {

    fun exists(userId: UUID, applicationId: UUID): Boolean = transaction {
        ApplicationResponsesTable
            .selectAll()
            .where {
                (ApplicationResponsesTable.userId eq userId) and
                    (ApplicationResponsesTable.applicationId eq applicationId)
            }
            .limit(1)
            .any()
    }

    fun insert(userId: UUID, applicationId: UUID): Boolean = transaction {
        val alreadyExists = ApplicationResponsesTable
            .selectAll()
            .where {
                (ApplicationResponsesTable.userId eq userId) and
                    (ApplicationResponsesTable.applicationId eq applicationId)
            }
            .limit(1)
            .any()
        if (alreadyExists) {
            return@transaction false
        }

        ApplicationResponsesTable.insert {
            it[ApplicationResponsesTable.userId] = userId
            it[ApplicationResponsesTable.applicationId] = applicationId
            it[ApplicationResponsesTable.createdAt] = OffsetDateTime.now(ZoneOffset.UTC)
        }
        true
    }

    fun delete(userId: UUID, applicationId: UUID): Boolean = transaction {
        val deleted = ApplicationResponsesTable.deleteWhere {
            (ApplicationResponsesTable.userId eq userId) and
                (ApplicationResponsesTable.applicationId eq applicationId)
        }
        deleted > 0
    }

    fun listApplicationIdsForUser(userId: UUID): List<UUID> = transaction {
        ApplicationResponsesTable
            .selectAll()
            .where { ApplicationResponsesTable.userId eq userId }
            .orderBy(ApplicationResponsesTable.createdAt to SortOrder.DESC)
            .map { it[ApplicationResponsesTable.applicationId] }
    }

    fun listResponders(applicationId: UUID): List<ApplicationResponseUserDto> = transaction {
        ApplicationResponsesTable
            .join(UserProfilesTable, JoinType.LEFT, ApplicationResponsesTable.userId, UserProfilesTable.userId)
            .selectAll()
            .where { ApplicationResponsesTable.applicationId eq applicationId }
            .orderBy(ApplicationResponsesTable.createdAt to SortOrder.DESC)
            .map(::mapResponderRow)
    }

    private fun mapResponderRow(row: ResultRow): ApplicationResponseUserDto =
        ApplicationResponseUserDto(
            userId = row[ApplicationResponsesTable.userId].toString(),
            firstName = row.getOrNull(UserProfilesTable.firstName),
            lastName = row.getOrNull(UserProfilesTable.lastName),
            middleName = row.getOrNull(UserProfilesTable.middleName),
            socialLinks = row.getOrNull(UserProfilesTable.socialLinks)
                ?.let(::decodeSocialLinks)
                ?: buildJsonObject { },
            respondedAt = row[ApplicationResponsesTable.createdAt].toString(),
        )

    private fun decodeSocialLinks(raw: String): JsonObject =
        runCatching {
            Json.decodeFromString(JsonObject.serializer(), raw)
        }.getOrElse { buildJsonObject { } }
}
