package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object ApplicationStatusesTable : Table("application_statuses") {
    val id = uuid("id")
    val code = text("code").uniqueIndex()
    val title = text("title")

    override val primaryKey = PrimaryKey(id)
}
