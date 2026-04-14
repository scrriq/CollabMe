package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ThemesTable : Table("themes") {
    val id = uuid("id")
    val name = text("name")
    val directionId = uuid("direction_id")
        .references(DirectionsTable.id, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE)
        .nullable()

    override val primaryKey = PrimaryKey(id)
}
