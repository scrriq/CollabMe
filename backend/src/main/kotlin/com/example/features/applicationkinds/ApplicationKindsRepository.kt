package com.example.features.applicationkinds

import com.example.db.tables.ApplicationKindsTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ApplicationKindsRepository {
    fun findById(id: UUID): ApplicationKindDto? = transaction {
        ApplicationKindsTable
            .selectAll()
            .where { ApplicationKindsTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                ApplicationKindDto(
                    id = row[ApplicationKindsTable.id].toString(),
                    code = row[ApplicationKindsTable.code],
                    title = row[ApplicationKindsTable.title],
                )
            }
    }
}
