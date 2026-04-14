package com.example.features.universities

import com.example.db.tables.UniversitiesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UniversitiesRepository {
    fun findById(id: UUID): UniversityDto? = transaction {
        UniversitiesTable
            .selectAll()
            .where { UniversitiesTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                UniversityDto(
                    id = row[UniversitiesTable.id].toString(),
                    name = row[UniversitiesTable.name],
                    slug = row[UniversitiesTable.slug],
                    cityId = row[UniversitiesTable.cityId]?.toString(),
                )
            }
    }
}
