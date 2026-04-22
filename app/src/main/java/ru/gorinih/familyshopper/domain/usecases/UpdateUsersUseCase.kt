package ru.gorinih.familyshopper.domain.usecases

import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 14.04.2026
 */

interface UpdateUsersUseCase {
    suspend operator fun invoke(replace: Boolean = false)
}

class UpdateUsersUseCaseImpl(
    private val remote: RemoteRepository,
    private val database: DatabaseRepository,
) : UpdateUsersUseCase {
    override suspend fun invoke(replace: Boolean) {
        val remoteUsers = remote.getUsersNames()
        if (remoteUsers.isNotEmpty()) {
            val listUser =
                remoteUsers.map { (key, value) -> ShoppedUsers(userUuid = key, userName = value) }
            if (replace) database.replaceUsers((listUser))
            database.keepUsers(listUser)
        }
    }
}