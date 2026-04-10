package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ThemeSkillsTable : Table("theme_skills") {
    val themeId = uuid("theme_id")
        .references(ThemesTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val skillId = uuid("skill_id")
        .references(SkillsTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(themeId, skillId)
}
