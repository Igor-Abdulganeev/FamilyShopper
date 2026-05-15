package ru.gorinih.familyshopper.ui.screens.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesGetAllRemoteUseCase
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesUseCase
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
import ru.gorinih.familyshopper.ui.screens.dictionary.models.EditDictionariesState
import ru.gorinih.familyshopper.ui.screens.dictionary.models.UiDictionary
import ru.gorinih.familyshopper.ui.screens.dictionary.models.toUiTag
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizer

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

class EditDictionariesViewModel(
    private val database: DatabaseRepository,
    private val syncRemote: SynchronizeDictionariesUseCase,
    private val syncAllRemote: SynchronizeDictionariesGetAllRemoteUseCase,
    private val voice: FamilyVoiceRecognizer,
    pref: StorageRepository
) : ViewModel() {

    private var jobVoiceRecognize: Job? = null

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        dictionaryState.update {
            it.copy(
                warning = WarningState(
                    isWarning = true,
                    textWarning = throwable.localizedMessage ?: ""
                )
            )
        }
    }

    var dictionaryState = MutableStateFlow(
        EditDictionariesState(canSync = pref.getGroupUUID().isNotBlank())
    )
        private set

    init {
        viewModelScope.launch {
            val result = voice.initRecognizer()
            dictionaryState.update {
                it.copy(voiceRecognizer = dictionaryState.map { v -> v.voiceRecognizer }
                    .first().copy(isEnabled = result))
            }
        }
        pref.getVoiceFlow()
            .catch {
                dictionaryState.update { state ->
                    state.copy(voiceRecognizer = dictionaryState.map { voice -> voice.voiceRecognizer }
                        .first().copy(isVisible = false, isEnabled = false))
                }
            }
            .onEach { enabled ->
                dictionaryState.update {
                    it.copy(voiceRecognizer = dictionaryState.map { v -> v.voiceRecognizer }
                        .first().copy(isVisible = enabled, isEnabled = enabled && voice.isPrepared()))
                }
            }.launchIn(viewModelScope)
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
                            textWarning = error.localizedMessage ?: ""
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
            val tagName = tag.lowercase().trim()
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
        viewModelScope.launch(Dispatchers.IO) {
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
                if (result.isError) dictionaryState.update { it.copy(warning = result.toWarningState()) }
            }
        }
    }

    fun onDismiss() {
        dictionaryState.update { it.copy(warning = WarningState()) }
    }

    fun voiceRecognizePress(isPressed: Boolean) {
        if (isPressed) {
            jobVoiceRecognize?.cancel()
            jobVoiceRecognize = null
            viewModelScope.launch(Dispatchers.Main.immediate + coroutineExceptionHandler) {
                dictionaryState.update {
                    it.copy(voiceRecognizer = dictionaryState.map { voice -> voice.voiceRecognizer }
                        .first().copy(isEnabled = false))
                }
            }
        }
        when (isPressed) {
            true -> {
                jobVoiceRecognize =
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            if (!voice.isPrepared()) {
                                if (!voice.initRecognizer()) {
                                    throw IllegalStateException("Not init voice recognizer")
                                }
                            }

                            voice.startListening().collect { text ->
                                if (text.isNotBlank()) {
                                    withContext(Dispatchers.Main.immediate) {
                                        dictionaryState.update {
                                            it.copy(voiceRecognizer = dictionaryState.map { voice -> voice.voiceRecognizer }
                                                .first().copy(fieldText = text))
                                        }
                                    }
                                }
                            }

                        } catch (ex: CancellationException) {
                            withContext(Dispatchers.Main.immediate + NonCancellable) {
                                dictionaryState.update {
                                    it.copy(voiceRecognizer = dictionaryState.map { voice -> voice.voiceRecognizer }
                                        .first().copy(isEnabled = true))
                                }
                            }
                            throw ex
                        } catch (_: Throwable) {
                            withContext(Dispatchers.Main.immediate + NonCancellable) {
                                dictionaryState.update {
                                    it.copy(voiceRecognizer = dictionaryState.map { voice -> voice.voiceRecognizer }
                                        .first().copy(isEnabled = false, isVisible = false))
                                }
                            }
                        }
                    }
            }

            false -> {
                viewModelScope.launch(Dispatchers.Main.immediate + coroutineExceptionHandler) {
                    jobVoiceRecognize?.cancel()
                    jobVoiceRecognize = null
                }
            }
        }
    }
}