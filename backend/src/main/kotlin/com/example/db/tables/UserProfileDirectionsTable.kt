package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserProfileDirectionsTable : Table("user_profile_directions") {
    val userId = uuid("user_id")
        .references(UserProfilesTable.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val directionId = uuid("direction_id")
        .references(DirectionsTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userId, directionId)
}
