package com.example.features.auth

import com.example.db.tables.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

data class AuthUserCredentials(
    val user: AuthUserDto,
    val passwordHash: String,
)

class AuthRepository {
    fun existsByLoginOrEmail(login: String, email: String): Boolean = transaction {
        UsersTable
            .selectAll()
            .where { (UsersTable.login eq login) or (UsersTable.email eq email) }
            .limit(1)
            .any()
    }

    fun createUser(login: String, email: String, passwordHash: String, phone: String?): AuthUserDto = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val id = UUID.randomUUID()

        UsersTable.insert { stmt ->
            stmt[UsersTable.id] = id
            stmt[UsersTable.login] = login
            stmt[UsersTable.email] = email
            stmt[UsersTable.passwordHash] = passwordHash
            stmt[UsersTable.phone] = phone
            stmt[UsersTable.createdAt] = now
            stmt[UsersTable.updatedAt] = now
            stmt[UsersTable.deletedAt] = null
        }

        AuthUserDto(
            id = id.toString(),
            login = login,
            email = email,
            phone = phone,
        )
    }

    fun findByLogin(login: String): AuthUserCredentials? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.login eq login }
            .andWhere { UsersTable.deletedAt.isNull() }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                AuthUserCredentials(
                    user = AuthUserDto(
                        id = row[UsersTable.id].toString(),
                        login = row[UsersTable.login],
                        email = row[UsersTable.email],
                        phone = row[UsersTable.phone],
                    ),
                    passwordHash = row[UsersTable.passwordHash],
                )
            }
    }
}
