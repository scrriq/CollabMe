package com.example.features.userprofiles

import com.example.db.tables.UserProfilesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserProfilesRepository {
    fun findByUserId(userId: UUID): UserProfileDto? = transaction {
        UserProfilesTable
            .selectAll()
            .where { UserProfilesTable.userId eq userId }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                UserProfileDto(
                    userId = row[UserProfilesTable.userId].toString(),
                    firstName = row[UserProfilesTable.firstName],
                    lastName = row[UserProfilesTable.lastName],
                    middleName = row[UserProfilesTable.middleName],
                    birthDate = row[UserProfilesTable.birthDate]?.toString(),
                    gender = row[UserProfilesTable.gender],
                    cityId = row[UserProfilesTable.cityId]?.toString(),
                    universityId = row[UserProfilesTable.universityId]?.toString(),
                    about = row[UserProfilesTable.about],
                    avatarUrl = row[UserProfilesTable.avatarUrl],
                    socialLinks = row[UserProfilesTable.socialLinks],
                    updatedAt = row[UserProfilesTable.updatedAt].toString(),
                )
            }
    }
}
