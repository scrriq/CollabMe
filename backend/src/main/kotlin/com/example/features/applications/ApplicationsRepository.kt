package com.example.features.applications

import com.example.db.tables.ApplicationsTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ApplicationsRepository {
    fun findById(id: UUID): ApplicationDto? = transaction {
        ApplicationsTable
            .selectAll()
            .where { ApplicationsTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                ApplicationDto(
                    id = row[ApplicationsTable.id].toString(),
                    userId = row[ApplicationsTable.userId].toString(),
                    themeId = row[ApplicationsTable.themeId].toString(),
                    kindId = row[ApplicationsTable.kindId].toString(),
                    statusId = row[ApplicationsTable.statusId].toString(),
                    title = row[ApplicationsTable.title],
                    description = row[ApplicationsTable.description],
                    createdAt = row[ApplicationsTable.createdAt].toString(),
                    updatedAt = row[ApplicationsTable.updatedAt].toString(),
                    archivedAt = row[ApplicationsTable.archivedAt]?.toString(),
                    completedAt = row[ApplicationsTable.completedAt]?.toString(),
                )
            }
    }
}
