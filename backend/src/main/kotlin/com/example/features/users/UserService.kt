package com.example.features.users

import java.util.UUID

class UsersService(
    private val usersRepository: UsersRepository
) {
    fun getById(id: UUID): UserDto? {
        return usersRepository.findById(id)
    }
}