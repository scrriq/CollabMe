package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UniversitiesTable : Table("universities") {
    val id = uuid("id")
    val name = text("name").uniqueIndex()
    val slug = text("slug").uniqueIndex()
    val cityId = uuid("city_id")
        .nullable()
        .references(CitiesTable.id, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(id)
}
