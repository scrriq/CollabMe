package com.example.features.skills

import com.example.db.tables.SkillsTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class SkillsRepository {
    fun findById(id: UUID): SkillDto? = transaction {
        SkillsTable
            .selectAll()
            .where { SkillsTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                SkillDto(
                    id = row[SkillsTable.id].toString(),
                    name = row[SkillsTable.name],
                )
            }
    }
}
