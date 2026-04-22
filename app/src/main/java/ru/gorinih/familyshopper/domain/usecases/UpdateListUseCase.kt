package ru.gorinih.familyshopper.domain.usecases

import okio.IOException
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.Results
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.toDictionaryLocalTag

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

interface UpdateListUseCase {
    suspend operator fun invoke(data: ShoppedList): Results
}

class UpdateListUseCaseImpl(
    private val pref: StorageRepository,
    private val database: DatabaseRepository,
    private val remote: RemoteRepository,
) : UpdateListUseCase {
    override suspend fun invoke(data: ShoppedList): Results = try {
        // 1 подготовить данные для заливки в локальную БД и на сервер
        //  надо получить с сервера версии, найти нашу если есть, если нет то 0, и увеличить на 1
        val remoteLists = remote.getListsVersions()

        val remoteListVersion =
            if (remoteLists.containsKey(data.listId)) remoteLists[data.listId]?.listVersion
                ?: 0 else 0
        //    получить список локальных тэгов и если есть новые то их добавить (только для легенды 1-3)
        updateTags(data)

        //  залить сперва в локальную
        //  залить изменение на сервер
        when (remoteLists.containsKey(data.listId) && remoteListVersion > data.listVersion) {
            true -> {
                val saveData = remote.getCurrentListById(data.listId)
                saveData?.let {
                    database.updateList(it)
                }
            }

            false -> {
                val localListVersion =
                    (if (remoteListVersion > data.listVersion) remoteListVersion else data.listVersion) + 1
                val saveData = data.copy(listVersion = localListVersion)
                remote.updateListWithVersion(listOf(saveData))
                database.updateList(saveData)
            }
        }
        Results(isError = false)
    } catch (_: IOException) {
        updateTags(data)
        val localListVersion = data.listVersion + 1
        val saveData = data.copy(listVersion = localListVersion)
        database.updateList(saveData)
        if (pref.getGroupUUID().isNotBlank())
            Results(isError = true, textErrorResource = R.string.error_network_state)
        else Results(isError = false)
    } catch (ex: Throwable) {
        Results(isError = true, textError = ex.localizedMessage ?: "")
    }

    private suspend fun updateTags(data: ShoppedList) {
        if (data.listLegend != 4) {
            val localTags = database.getDictionaryTags()
            val newTags = data.tagNames.filter { tag -> tag.tagName !in localTags }
                .map { it.toDictionaryLocalTag(true) }
            if (newTags.isNotEmpty()) {
                database.addTags(newTags)
            }
        }
    }

}