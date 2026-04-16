package ru.gorinih.familyshopper.domain.usecases

import okio.IOException
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */

interface DeleteList {
    suspend operator fun invoke(listId: String): Results
}

class DeleteListImpl(
    private val database: DatabaseRepository,
    private val remote: RemoteRepository
) : DeleteList {
    override suspend fun invoke(listId: String): Results {
        val result = try {
            remote.deleteListWithVersion(listId = listId)
        } catch (_: IOException) {
            Results(isError = true, textError = "Отсутствует подключение к сети")
        } catch (ex: Throwable) {
            Results(isError = true, textError = ex.localizedMessage ?: "")
        }
        database.deleteList(listId = listId)
        return result
    }
}