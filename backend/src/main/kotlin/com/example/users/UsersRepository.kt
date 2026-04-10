package com.example.users

import com.example.db.UsersTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UsersRepository {
    fun findById(id: UUID): UserDto? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq id }
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                UserDto(
                    id = row[UsersTable.id].toString(),
                    login = row[UsersTable.login],
                    email = row[UsersTable.email],
                    phone = row[UsersTable.phone],
                )
            }
    }
}

