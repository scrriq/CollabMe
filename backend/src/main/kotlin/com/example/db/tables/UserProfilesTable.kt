package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object UserProfilesTable : Table("user_profiles") {
    val userId = uuid("user_id")
        .references(UsersTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val firstName = text("first_name")
    val lastName = text("last_name")
    val middleName = text("middle_name").nullable()
    val birthDate = date("birth_date").nullable()
    val gender = text("gender").nullable()
    val cityId = uuid("city_id")
        .nullable()
        .references(CitiesTable.id, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE)
    val universityId = uuid("university_id")
        .nullable()
        .references(UniversitiesTable.id, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE)
    val about = text("about").nullable()
    val avatarUrl = text("avatar_url").nullable()
    val socialLinks = text("social_links")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(userId)
}
