package ru.gorinih.familyshopper.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okio.IOException
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Обновление справочника словаря продуктов
 * groupId берем из хранилища
 * имеющийся список лежит в БД
 *
 * результат так же загоняем в БД и автообновление UI все показывает
 */

interface SynchronizeDictionariesUseCase {
    suspend operator fun invoke(): Results
}

class SynchronizeDictionariesUseCaseImpl(
    private val pref: StorageRepository,
    private val remote: RemoteRepository,
    private val database: DatabaseRepository,
) : SynchronizeDictionariesUseCase {
    override suspend fun invoke(): Results =
        try {
            val remoteVersions: Map<String, Int> = remote.getDictionariesVersions()
            val localVersions: Map<String, Int> = database.takeDictionariesVersions()

            // 1) получим ветки которые есть новее на сервере
            val needUpdateFromRemoteKeys: Set<String> = remoteVersions.filter { (key, ver) ->
                ver > (localVersions[key] ?: 0)
            }.keys
            // 2) заберем нужные ветки с сервера если они есть
            val remoteDictionaries: List<DictionaryRemoteTag> = when {
                needUpdateFromRemoteKeys.isEmpty() -> emptyList()
                needUpdateFromRemoteKeys.count() > 7 ->
                    remote.getAllDictionaries().map { it.value }
                        .filter { it.tagId in needUpdateFromRemoteKeys }

                else -> coroutineScope {
                    needUpdateFromRemoteKeys.map { id ->
                        async {
                            id to remote.getDictionaryById(tagId = id)
                        }
                    }.awaitAll().map { (_, tags) -> tags }
                }
            }.filter { it.tagId.isNotBlank() }

            // 3) удалим из локальных веток старое и удалим локально удаленные в данных с сервера и зальем полученый результат в БД
            val updatingDictionaries: List<DictionaryLocalVersionTag> =
                needUpdateFromRemoteKeys.map { key ->
                    val version = remoteVersions[key] ?: 0
                    DictionaryLocalVersionTag(
                        tagId = key,
                        tagVersion = version,
                        tagNames = remoteDictionaries.filter { it.tagId == key }
                            .flatMap { it.tagNames }
                    )
                }
            database.updateDictionaries(dictionaries = updatingDictionaries)

            // 4) получим локальные ветки которые имеют несохраненные изменения и повысим версию у них
            val needSendToRemoteKeys: Map<String, Int> =
                database.takeKeysDictionaryForUpdate().map { (key, value) ->
                    when (key in needUpdateFromRemoteKeys) {
                        true -> key to (remoteVersions[key] ?: value) + 1
                        false -> key to value + 1
                    }
                }.toMap()

            // 5) получим данные на отправку на сервер
            val updates = mutableListOf<DictionaryRemoteTag>()
            needSendToRemoteKeys.map { (key, value) ->
                val tags = database.takeUpdateTagsFromDictionary(key)
                updates.add(DictionaryRemoteTag(key, value, tags.toList()))
            }
            if (updates.isNotEmpty()) {
                remote.updateDictionaryWithVersion(updates)
                val versions = updates.filter { it.tagNames.isEmpty() }
                // 6) обновим всё в БД
                database.updateDictionariesWithVersions(needSendToRemoteKeys)
                for (version in versions) {
                    database.deleteDictionaryVersion(version.tagId)
                }
            }
            Results(
                isError = false,
                textComplete = if (needUpdateFromRemoteKeys.isEmpty() && updates.isEmpty()) "not data" else ""
            )
        } catch (_: IOException) {
            if (pref.getGroupUUID().isNotBlank())
                Results(isError = true, textErrorResource = R.string.error_network_state)
            else Results(isError = false)
        } catch (ex: Throwable) {
            Results(true, ex.localizedMessage ?: "")
        }
}
