package com.example.features.applicationstatuses

import com.example.db.tables.ApplicationStatusesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ApplicationStatusesRepository {
    fun findById(id: UUID): ApplicationStatusDto? = transaction {
        ApplicationStatusesTable
            .selectAll()
            .where { ApplicationStatusesTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                ApplicationStatusDto(
                    id = row[ApplicationStatusesTable.id].toString(),
                    code = row[ApplicationStatusesTable.code],
                    title = row[ApplicationStatusesTable.title],
                )
            }
    }
}
