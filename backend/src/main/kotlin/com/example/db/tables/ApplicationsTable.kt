package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object ApplicationsTable : Table("applications") {
    val id = uuid("id")
    val userId = uuid("user_id")
        .references(UsersTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val themeId = uuid("theme_id")
        .references(ThemesTable.id, onDelete = ReferenceOption.RESTRICT, onUpdate = ReferenceOption.CASCADE)
    val kindId = uuid("kind_id")
        .references(ApplicationKindsTable.id, onDelete = ReferenceOption.RESTRICT, onUpdate = ReferenceOption.CASCADE)
    val statusId = uuid("status_id")
        .references(ApplicationStatusesTable.id, onDelete = ReferenceOption.RESTRICT, onUpdate = ReferenceOption.CASCADE)
    val title = text("title")
    val description = text("description")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    /** Soft delete (скрыта с основных экранов). */
    val deletedAt = timestampWithTimeZone("deleted_at").nullable()
    /** «Долгий ящик» / завершение сценария без удаления записи. */
    val completedAt = timestampWithTimeZone("completed_at").nullable()

    override val primaryKey = PrimaryKey(id)
}
