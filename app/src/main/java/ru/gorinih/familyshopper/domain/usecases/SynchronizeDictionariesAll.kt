package ru.gorinih.familyshopper.domain.usecases

import okio.IOException
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 20.04.2026
 */

interface SynchronizeDictionariesGetAllRemote {
    suspend operator fun invoke(): Results
}

class SynchronizeDictionariesGetAllRemoteImpl(
    private val remote: RemoteRepository,
    private val database: DatabaseRepository,
) : SynchronizeDictionariesGetAllRemote {
    override suspend fun invoke(): Results {
        try {
            // заберем с сервера все ветки словарей
            val remoteDictionaries: Map<String, DictionaryRemoteTag> = remote.getAllDictionaries()

            val updatingDictionaries: List<DictionaryLocalVersionTag> =
                remoteDictionaries.map { dictionary ->
                    DictionaryLocalVersionTag(
                        tagId = dictionary.value.tagId,
                        tagVersion = dictionary.value.tagVersion,
                        tagNames = dictionary.value.tagNames
                    )
                }
            database.updateDictionaries(dictionaries = updatingDictionaries)
            return Results(false)

        } catch (_: IOException) {
            return Results(true, textError = "Отсутствует подключение к сети", textErrorResource = R.string.error_network_text)
        } catch (ex: Throwable) {
            return Results(true, ex.localizedMessage ?: "unknown error")
        }
    }
}
