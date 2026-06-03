package com.example.features.themes

import com.example.db.tables.ThemesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ThemesRepository {
    fun findById(id: UUID): ThemeDto? = transaction {
        ThemesTable
            .selectAll()
            .where { ThemesTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                ThemeDto(
                    id = row[ThemesTable.id].toString(),
                    name = row[ThemesTable.name],
                    directionId = row[ThemesTable.directionId]?.toString(),
                )
            }
    }

    fun findAll(): List<ThemeDto> = transaction{
        ThemesTable
            .selectAll()
            .map { row ->
                ThemeDto(
                    id = row[ThemesTable.id].toString(),
                    name = row[ThemesTable.name],
                )
            }
    }
}
