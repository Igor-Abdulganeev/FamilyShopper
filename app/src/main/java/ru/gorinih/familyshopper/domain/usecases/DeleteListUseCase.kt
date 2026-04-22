package ru.gorinih.familyshopper.domain.usecases

import okio.IOException
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */

interface DeleteListUseCase {
    suspend operator fun invoke(listId: String): Results
}

class DeleteListUseCaseImpl(
    private val pref: StorageRepository,
    private val database: DatabaseRepository,
    private val remote: RemoteRepository
) : DeleteListUseCase {
    override suspend fun invoke(listId: String): Results = try {
        database.deleteList(listId = listId)
        remote.deleteListWithVersion(listId = listId)
        Results(isError = false)
    } catch (_: IOException) {
        if (pref.getGroupUUID().isNotBlank())
            Results(isError = true, textErrorResource = R.string.error_network_state)
        else Results(isError = false)
    } catch (ex: Throwable) {
        Results(isError = true, textError = ex.localizedMessage ?: "")
    }
}
