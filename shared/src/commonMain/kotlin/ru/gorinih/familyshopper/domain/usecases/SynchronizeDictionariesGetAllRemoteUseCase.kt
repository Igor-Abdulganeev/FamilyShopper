package ru.gorinih.familyshopper.domain.usecases

import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.error_network_state
import okio.IOException
import org.jetbrains.compose.resources.getString
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 20.04.2026
 */

interface SynchronizeDictionariesGetAllRemoteUseCase {
    suspend operator fun invoke(): Results
}

class SynchronizeDictionariesGetAllRemoteUseCaseImpl(
    private val store: StoreRepository,
    private val remote: RemoteRepository,
    private val database: DatabaseRepository,
) : SynchronizeDictionariesGetAllRemoteUseCase {
    override suspend fun invoke(): Results =
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
            Results(false)
        } catch (_: IOException) {
            if (store.getGroupUUID().isNotBlank())
                Results(
                    isError = true,
                    isNetworkError = true,
                    textError = getString(Res.string.error_network_state)
                )
            else Results(isError = false)
        } catch (ex: Throwable) {
            Results(isError = true, textError = ex.localizedMessage ?: "")
        }
}
