package ru.gorinih.familyshopper.ui.screens.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesUseCase
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesGetAllRemoteUseCase
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
import ru.gorinih.familyshopper.ui.screens.dictionary.models.EditDictionariesState
import ru.gorinih.familyshopper.ui.screens.dictionary.models.UiDictionary
import ru.gorinih.familyshopper.ui.screens.dictionary.models.toUiTag

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

class EditDictionariesViewModel(
    private val database: DatabaseRepository,
    private val syncRemote: SynchronizeDictionariesUseCase,
    private val syncAllRemote: SynchronizeDictionariesGetAllRemoteUseCase,
    pref: StorageRepository
) : ViewModel() {

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        dictionaryState.update {
            it.copy(
                warning = WarningState(
                    isWarning = true,
                    textWarning = throwable.localizedMessage ?: "неизвестная ошибка"
                )
            )
        }
    }

    var dictionaryState = MutableStateFlow(
        EditDictionariesState(canSync = pref.getGroupUUID().isNotBlank())
    )
        private set

    init {
        syncDictionaries()
        database.takeDictionaries()
            .map { list ->
                list.groupBy { it.tagId }
                    .map { (key, value) ->
                        UiDictionary(
                            tagId = key,
                            tagNames = value.map { it.toUiTag() }
                        )
                    }
            }.catch { error ->
                dictionaryState.update {
                    it.copy(
                        warning = WarningState(
                            isWarning = true,
                            textWarning = error.localizedMessage ?: "неизвестная ошибка"
                        ), isLoading = false
                    )
                }
            }.onEach { list ->

                dictionaryState.update {
                    it.copy(
                        warning = WarningState(),
                        list = list,
                        isLoading = false
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun addTag(tag: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val tagName = tag.lowercase()
            val tagId: String = tagName.first().toString().uppercase()
            database.addTag(
                DictionaryLocalTag(
                    tagId = tagId,
                    tagName = tagName,
                    needUpdate = true
                )
            )
        }
    }

    fun deleteTag(tagNane: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val tagId = tagNane.first().uppercase()
            database.deleteTag(tagId, tagNane)
        }
    }

    fun syncDictionaries() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            syncRemote()
        }
    }

    fun refreshDictionaries() {
        if (!dictionaryState.value.isLoading) {
            dictionaryState.update { it.copy(isLoading = true) }
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                val result = syncAllRemote().apply {
                    dictionaryState.update { it.copy(isLoading = false) }
                }
                if (result.isError) dictionaryState.update {it.copy(warning = result.toWarningState())}
            }
        }
    }

    fun onDismiss() {
        dictionaryState.update { it.copy(warning = WarningState()) }
    }
}