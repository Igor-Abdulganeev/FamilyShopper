package ru.gorinih.familyshopper.domain.usecases

import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.getNewerOrNull

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

interface GetAndUpdateListUseCase {
    suspend operator fun invoke(listId: String): ShoppedList?
}

class GetAndUpdateListUseCaseImpl(
    private val database: DatabaseRepository,
    private val remote: RemoteRepository
) : GetAndUpdateListUseCase {
    override suspend fun invoke(listId: String): ShoppedList? =
        try {
            val remoteResult = remote.getCurrentListById(listId = listId) ?: return null
            val localResult = database.takeList(listId)
            val result = remoteResult.getNewerOrNull(localResult)
            result?.let { database.updateList(result) }
            result
        } catch (_: Throwable) {
            null
        }
}