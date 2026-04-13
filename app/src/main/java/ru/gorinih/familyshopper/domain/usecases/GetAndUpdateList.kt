package ru.gorinih.familyshopper.domain.usecases

import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.getNewerOrNull

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

interface GetAndUpdateList {
    suspend operator fun invoke(listId: String): ShoppedList?
}

class GetAndUpdateListImpl(
    private val database: DatabaseRepository,
    private val remote: RemoteRepository
): GetAndUpdateList {
    override suspend fun invoke(listId: String): ShoppedList? {
        val remoteResult = remote.getCurrentListById(listId = listId)
        remoteResult?.let { list ->
            val localResult = database.takeList(listId)
            val result = list.getNewerOrNull(localResult)
            result?.let { database.updateList(result) }
            return result
        }
        return null
    }
}