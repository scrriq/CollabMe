package com.example.features.applications

import com.example.db.tables.ApplicationKindsTable
import com.example.db.tables.ApplicationStatusesTable
import com.example.db.tables.ApplicationsTable
import com.example.db.tables.ThemesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class ApplicationsRepository {

    fun listVisible(userId: UUID?, excludeCompleted: Boolean): List<ApplicationDto> = transaction {
        applicationsWithRefs()
            .selectAll()
            .where {
                var expr = ApplicationsTable.deletedAt.isNull()
                if (userId != null) {
                    expr = expr and (ApplicationsTable.userId eq userId)
                }
                if (excludeCompleted) {
                    expr = expr and ApplicationsTable.completedAt.isNull()
                }
                expr
            }
            .orderBy(ApplicationsTable.createdAt to SortOrder.DESC)
            .map(::mapRow)
    }

    fun listVisibleByIds(ids: List<UUID>): List<ApplicationDto> = transaction {
        if (ids.isEmpty()) return@transaction emptyList()

        applicationsWithRefs()
            .selectAll()
            .where {
                (ApplicationsTable.id inList ids) and ApplicationsTable.deletedAt.isNull()
            }
            .orderBy(ApplicationsTable.createdAt to SortOrder.DESC)
            .map(::mapRow)
    }

    fun findVisibleById(id: UUID): ApplicationDto? = transaction {
        applicationsWithRefs()
            .selectAll()
            .where {
                (ApplicationsTable.id eq id) and ApplicationsTable.deletedAt.isNull()
            }
            .limit(1)
            .singleOrNull()
            ?.let(::mapRow)
    }

    fun findVisibleByIdAndUserId(id: UUID, userId: UUID): ApplicationDto? = transaction {
        applicationsWithRefs()
            .selectAll()
            .where {
                (ApplicationsTable.id eq id) and
                    (ApplicationsTable.userId eq userId) and
                    ApplicationsTable.deletedAt.isNull()
            }
            .limit(1)
            .singleOrNull()
            ?.let(::mapRow)
    }

    fun insert(
        id: UUID,
        userId: UUID,
        themeId: UUID,
        kindId: UUID,
        statusId: UUID,
        title: String,
        description: String,
        completedAt: OffsetDateTime?,
    ): ApplicationDto = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        ApplicationsTable.insert {
            it[ApplicationsTable.id] = id
            it[ApplicationsTable.userId] = userId
            it[ApplicationsTable.themeId] = themeId
            it[ApplicationsTable.kindId] = kindId
            it[ApplicationsTable.statusId] = statusId
            it[ApplicationsTable.title] = title
            it[ApplicationsTable.description] = description
            it[ApplicationsTable.createdAt] = now
            it[ApplicationsTable.updatedAt] = now
            it[ApplicationsTable.deletedAt] = null
            it[ApplicationsTable.completedAt] = completedAt
        }
        findVisibleByIdAndUserId(id, userId)!!
    }

    fun patch(
        id: UUID,
        userId: UUID,
        themeId: UUID?,
        kindId: UUID?,
        statusId: UUID?,
        title: String?,
        description: String?,
        completedAt: OffsetDateTime?,
        touchCompletedAt: Boolean,
    ): ApplicationDto? = transaction {
        val updated = ApplicationsTable.update(
            where = {
                (ApplicationsTable.id eq id) and
                    (ApplicationsTable.userId eq userId) and
                    ApplicationsTable.deletedAt.isNull()
            },
        ) {
            themeId?.let { tid -> it[ApplicationsTable.themeId] = tid }
            kindId?.let { kid -> it[ApplicationsTable.kindId] = kid }
            statusId?.let { sid -> it[ApplicationsTable.statusId] = sid }
            title?.let { t -> it[ApplicationsTable.title] = t }
            description?.let { d -> it[ApplicationsTable.description] = d }
            if (touchCompletedAt) {
                it[ApplicationsTable.completedAt] = completedAt
            }
        }
        if (updated == 0) null else findVisibleByIdAndUserId(id, userId)
    }

    fun softDelete(id: UUID, userId: UUID): Boolean = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val n = ApplicationsTable.update(
            where = {
                (ApplicationsTable.id eq id) and
                    (ApplicationsTable.userId eq userId) and
                    ApplicationsTable.deletedAt.isNull()
            },
        ) {
            it[ApplicationsTable.deletedAt] = now
        }
        n > 0
    }

    private fun applicationsWithRefs() =
        ApplicationsTable
            .innerJoin(ThemesTable, { ApplicationsTable.themeId }, { ThemesTable.id })
            .innerJoin(ApplicationKindsTable, { ApplicationsTable.kindId }, { ApplicationKindsTable.id })
            .innerJoin(ApplicationStatusesTable, { ApplicationsTable.statusId }, { ApplicationStatusesTable.id })

    private fun mapRow(row: ResultRow): ApplicationDto =
        ApplicationDto(
            id = row[ApplicationsTable.id].toString(),
            userId = row[ApplicationsTable.userId].toString(),
            themeId = row[ApplicationsTable.themeId].toString(),
            kindId = row[ApplicationsTable.kindId].toString(),
            statusId = row[ApplicationsTable.statusId].toString(),
            theme = ApplicationThemeRef(
                id = row[ThemesTable.id].toString(),
                name = row[ThemesTable.name],
            ),
            kind = ApplicationTitledRef(
                id = row[ApplicationKindsTable.id].toString(),
                title = row[ApplicationKindsTable.title],
            ),
            status = ApplicationTitledRef(
                id = row[ApplicationStatusesTable.id].toString(),
                title = row[ApplicationStatusesTable.title],
            ),
            title = row[ApplicationsTable.title],
            description = row[ApplicationsTable.description],
            createdAt = row[ApplicationsTable.createdAt].toString(),
            updatedAt = row[ApplicationsTable.updatedAt].toString(),
            completedAt = row[ApplicationsTable.completedAt]?.toString(),
            deletedAt = row[ApplicationsTable.deletedAt]?.toString(),
        )
}
