package ru.gorinih.familyshopper.ui.screens.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.domain.StorageRepository
import java.util.UUID

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class SettingsViewModel(
    private val pref: StorageRepository
) : ViewModel() {

    var stateSettings by mutableStateOf(getStartedKeys())
        private set

    private val _shareData = Channel<String>()
    val shareEvents = _shareData.receiveAsFlow()

    init {
        Log.d("GINES", "CREATE VM=$this")
    }

    fun updateClientUuid(uuid: String) {
        stateSettings = stateSettings.copy(clientUUID = uuid)
    }

    fun updateGroupUuid(uuid: String) {
        stateSettings = stateSettings.copy(groupUUID = uuid)
    }

    fun applyGroupUuid() {
        if (stateSettings.groupUUID.isNotBlank()) {
            pref.setGroupUUID(stateSettings.groupUUID)
        } else {
            val current = pref.getGroupUUID().takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            stateSettings = stateSettings.copy(groupUUID = current)
        }
    }

    fun applyClientUuid() {
        if (stateSettings.clientUUID.isNotBlank()) {
            pref.setClientUUID(stateSettings.clientUUID)
        } else {
            stateSettings = stateSettings.copy(clientUUID = pref.getClientUUID())
        }
    }

    fun createGroupUUID() {
        val uuid = UUID.randomUUID().toString()
        stateSettings = stateSettings.copy(groupUUID = uuid)
    }

    fun onShareGroupUuid() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                _shareData.send(stateSettings.groupUUID)
            } catch (_: Throwable) { }
        }
    }

    fun onShareClientUuid() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                _shareData.send(stateSettings.clientUUID)
            } catch (_: Throwable) { }
        }
    }

    private fun getStartedKeys(): SettingsState =
        SettingsState(
            clientUUID = pref.getClientUUID(),
            groupUUID = pref.getGroupUUID(),
        )
}