package ru.gorinih.familyshopper.domain.usecases

import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.Results
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.toDictionaryLocalTag

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

interface SaveList {
    suspend operator fun invoke(data: ShoppedList): Results
}

class SaveListImpl(
    private val database: DatabaseRepository,
    private val remote: RemoteRepository,
) : SaveList {
    override suspend fun invoke(data: ShoppedList): Results =
        try {
            // 1 подготовить данные для заливки в локальную БД и на сервер
            //  надо получить с сервера версии, найти нашу если есть, если нет то 0, и увеличить на 1
            val remoteLists = remote.getListsVersions()
            val remoteListVersion =
                if (remoteLists.containsKey(data.listId)) remoteLists[data.listId]?.listVersion
                    ?: 0 else 0
            //    получить список локальных тэгоы и если есть новые то их добавить (только для легенды 1-3)
            if (data.listLegend != 4) {
                val localTags = database.getDictionaryTags()
                val newTags = data.tagNames.filter { tag -> tag.tagName !in localTags }
                    .map { it.toDictionaryLocalTag(true) }
                if (newTags.isNotEmpty()) {
                    database.addTags(newTags)
                }
            }
            //  залить сперва в локальную
            //  залить изменение на сервер
            when(remoteLists.containsKey(data.listId) && remoteListVersion > data.listVersion) {
                true -> {
                    val saveData = remote.getCurrentListById(data.listId)
                    saveData?.let{database.updateList(it)}
                }
                false -> {
                    val localListVersion =
                        (if (remoteListVersion > data.listVersion) remoteListVersion else data.listVersion) + 1
                    val saveData = data.copy(listVersion = localListVersion)
                    database.updateList(saveData)
                    remote.updateListWithVersion(listOf(saveData))
                }
            }
            Results(isError = false)
        } catch (ex: Throwable) {
            Results(isError = true, textError = ex.localizedMessage ?: "unknown error")
        }

}