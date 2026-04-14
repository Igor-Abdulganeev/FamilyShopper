package ru.gorinih.familyshopper.domain.usecases

import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 14.04.2026
 */

interface UpdateUsers {
    suspend operator fun invoke()
}

class UpdateUsersImpl(
    private val remote: RemoteRepository,
    private val database: DatabaseRepository,
) : UpdateUsers {
    override suspend fun invoke() {
        val remoteUsers = remote.getUsersNames()
        if (remoteUsers.isNotEmpty()) {
            val listUser =
                remoteUsers.map { (key, value) -> ShoppedUsers(userUuid = key, userName = value) }
            database.keepUsers(listUser)
        }
    }
}