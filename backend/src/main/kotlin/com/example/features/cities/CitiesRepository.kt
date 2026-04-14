package com.example.features.cities

import com.example.db.tables.CitiesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class CitiesRepository {
    fun findById(id: UUID): CityDto? = transaction {
        CitiesTable
            .selectAll()
            .where { CitiesTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                CityDto(
                    id = row[CitiesTable.id].toString(),
                    name = row[CitiesTable.name],
                )
            }
    }
}
