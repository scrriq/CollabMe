package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object ApplicationResponsesTable : Table("application_responses") {
    val userId = uuid("user_id")
        .references(UsersTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val applicationId = uuid("application_id")
        .references(ApplicationsTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(userId, applicationId)
}
