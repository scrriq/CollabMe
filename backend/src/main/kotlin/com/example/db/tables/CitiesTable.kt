package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object CitiesTable : Table("cities") {
    val id = uuid("id")
    val name = text("name").uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}
