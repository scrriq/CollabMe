package com.example.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object UsersTable : Table("users") {
    val id = uuid("id")
    val login = text("login").uniqueIndex()
    val email = text("email").uniqueIndex()
    val phone = text("phone").nullable()
    val passwordHash = text("password_hash")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    val deletedAt = timestampWithTimeZone("deleted_at").nullable()

    override val primaryKey = PrimaryKey(id)
}
