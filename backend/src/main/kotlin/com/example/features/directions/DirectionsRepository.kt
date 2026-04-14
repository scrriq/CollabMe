package com.example.features.directions

import com.example.db.tables.DirectionsTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class DirectionsRepository {
    fun findById(id: UUID): DirectionDto? = transaction {
        DirectionsTable
            .selectAll()
            .where { DirectionsTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                DirectionDto(
                    id = row[DirectionsTable.id].toString(),
                    name = row[DirectionsTable.name],
                )
            }
    }
}
