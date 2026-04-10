package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ProfileSkillsTable : Table("profile_skills") {
    val userId = uuid("user_id")
        .references(UserProfilesTable.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val skillId = uuid("skill_id")
        .references(SkillsTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userId, skillId)
}
