package ru.gorinih.familyshopper.domain.usecases

import android.content.Context
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo
import ru.gorinih.familyshopper.domain.models.Results
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

interface SynchronizeLists {
    suspend operator fun invoke(): Results
}

class SynchronizeListsImpl(
    private val context: Context,
    private val database: DatabaseRepository,
    private val remote: RemoteRepository,
    private val pref: StorageRepository,
) : SynchronizeLists {
    override suspend fun invoke(): Results =
        try {
            val userUuid = pref.getClientUUID()
            // получить версии списков с сервера
            val remoteListsInfo: Map<String, ListRemoteInfo> = remote.getListsVersions()

            Log.d("GINES", "remoteList=$remoteListsInfo")
            // получить локальные списки
            val localListInfo: MutableMap<String, ShoppedList> =
                database.takeListsWithVersions().toMutableMap()
            Log.d("GINES", "localListInfo=$localListInfo")

            // если у нас есть чужие но их нет на сервере - они удалены и у нас тоже надо удалить, получим их список и удалим до дальнейших шагов
            val deletedListKeys: Set<String> =
                localListInfo.filter { list -> !remoteListsInfo.containsKey(list.key) }.keys
            Log.d("GINES", "deletedListKeys=$deletedListKeys")
            val countDeleted = deletedListKeys.count()
            for (deleteId in deletedListKeys) {
                localListInfo.remove(deleteId)
                database.deleteList(listId = deleteId)
            }
            // сверить и получить 2 списка 1- тех что на сервере есть новее 2- тех что на докале есть новее
            // исключить из них чужие приватные
            val needUpdateFromRemoteKeys: Set<String> = remoteListsInfo.filter { (key, ver) ->
                ver.listVersion > (localListInfo[key]?.listVersion ?: 0) &&
                        (ver.listLegend < 4 || ver.listOwner == userUuid)

            }.keys // те что старше на сервере
            Log.d("GINES", "neddUpdateFromREMOTE=$needUpdateFromRemoteKeys")


            val needUpdateFromLocalKeys: Set<String> = localListInfo.filter { (key, ver) ->
                ver.listVersion > (remoteListsInfo[key]?.listVersion ?: 0)
            }.keys // те что старше на локале
            Log.d("GINES", "needUpdateFromLOCAL=$needUpdateFromLocalKeys")

            val needResearchKeys = localListInfo.filter { (key, ver) ->
                ver.listVersion == (remoteListsInfo[key]?.listVersion ?: 0)
            }.keys // те что одинаковой версии и там и там, надо проверить если дата отличается то тогда что то делать если нет то норм
            Log.d("GINES", "RESEARCH=$needResearchKeys")
            // Получим полные ветки списков с сервера
            val remoteKeys: MutableSet<String> = needUpdateFromRemoteKeys.toMutableSet()
            remoteKeys.addAll(needResearchKeys)

            val remoteLists: List<ShoppedList> = when {
                remoteKeys.isEmpty() -> emptyList()
                remoteKeys.size > 5 -> remote.getAllCurrentLists()
                    .filter { (key, _) -> key in remoteKeys }.map { it.value }

                else -> coroutineScope {
                    remoteKeys.map { key ->
                        async {
                            remote.getCurrentListById(listId = key)
                        }
                    }.awaitAll().filterNotNull()
                }
            }
            Log.d("GINES", "FULL remote=$remoteKeys")
            // сверим какие из needResearchKeys надо залить с сервера (toLoad) а оставшиеся надо значит закинуть на сервер (toUpload)
            val toLoad = remoteLists.filter {
                it.listId in needResearchKeys
                        && localListInfo[it.listId]?.listVersion == it.listVersion
                        && (localListInfo[it.listId]?.dateTime ?: 0) < it.dateTime
            }
            Log.d("GINES", "toLoad=$toLoad")
            val countUpdated = needUpdateFromRemoteKeys.count() + toLoad.count()

            val toUpLoad = needResearchKeys.toMutableSet()
            toUpLoad.removeAll(toLoad.map { it.listId }.toSet())
            Log.d("GINES", "toUpLoad=$toUpLoad")

            // залить изменения локально что надо
            for (list in remoteLists) {
                if (list.listId in needUpdateFromRemoteKeys) database.updateList(list)
            }
            toLoad.forEach { database.updateList(it) }
            // выкинуть их на сервер
            toUpLoad.addAll(needUpdateFromLocalKeys)
            val uploadList = mutableListOf<ShoppedList>()
            for (listId in toUpLoad) {
                val data = database.takeList(listId)
                uploadList.add(data)
            }
            Log.d("GINES", "uploadList=$uploadList")

            remote.updateListWithVersion(updates = uploadList)
            val completeText = StringBuilder()
            if (countUpdated != 0) completeText.append(
                context.resources.getString(
                    R.string.warning_text_changed,
                    countUpdated
                )
            )
            if (countDeleted != 0) completeText.append(
                context.resources.getString(
                    R.string.warning_text_deleted,
                    countDeleted
                )
            )
            Results(isError = false, textComplete = completeText.toString())
        } catch (ex: Throwable) {
            Results(isError = true, textError = ex.localizedMessage ?: "unknown error")
        }
}