package ru.gorinih.familyshopper.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.ShoppedUsers
import ru.gorinih.familyshopper.domain.usecases.UpdateUsers
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListUser
import ru.gorinih.familyshopper.ui.screens.lists.models.toShoppedUsers
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListUsers
import java.util.UUID

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class SettingsViewModel(
    private val pref: StorageRepository,
    private val remote: RemoteRepository,
    private val database: DatabaseRepository,
    private val updater: UpdateUsers,
) : ViewModel() {

    var stateSettings by mutableStateOf(getStartedKeys())
        private set

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
    }

    private val _shareData = Channel<String>()
    val shareEvents = _shareData.receiveAsFlow()

    init {
        updateUsers(false)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            database.takeUsers()
                .catch { }
                .onEach { users ->
                    val list: List<UiListUser> = users.map { it.toUiListUsers() }
                        .filterNot { it.userUuid == pref.getClientUUID() }.sortedBy { it.userUuid }
                    stateSettings = stateSettings.copy(listUsers = list)
                }.stateIn(
                    viewModelScope
                )
        }
    }

    fun updateClientUuid(uuid: String) {
        stateSettings = stateSettings.copy(clientUUID = uuid)
    }

    fun updateGroupUuid(uuid: String) {
        stateSettings = stateSettings.copy(groupUUID = uuid)
    }

    fun updateUserName(name: String) {
        stateSettings = stateSettings.copy(userName = name)
    }

    fun updateBackground() {
        stateSettings = stateSettings.copy(rainbow = !stateSettings.rainbow)
        pref.setBackgroundState(stateSettings.rainbow)
    }

    fun updateTypeList(type: Int) {
        stateSettings = stateSettings.copy(defaultTypeList = type)
        pref.setTypeList(type)
    }

    fun updateUser(user: UiListUser, isDelete: Boolean) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (isDelete) {
                database.deleteUser(user.toShoppedUsers())
            } else {
                database.keepUser(user.toShoppedUsers())
            }
        }
    }

    fun updateUsers(replace: Boolean) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            updater(replace)
        }
    }

    fun saveUserName() {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            if (stateSettings.userName != stateSettings.userNameSaved) {
                pref.setUserName(stateSettings.userName)
                try {
                    database.keepUser(
                        ShoppedUsers(
                            pref.getClientUUID(),
                            stateSettings.userName
                        )
                    )
                    remote.setUserName()
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun restoreGroupUuid() {
        stateSettings = stateSettings.copy(groupUUID = pref.getGroupUUID())
    }

    fun applyGroupUuid() {
        pref.setGroupUUID(stateSettings.groupUUID)
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
        if (!stateSettings.isSharing) {
            stateSettings = stateSettings.copy(isSharing = true)
            viewModelScope.launch(Dispatchers.Main.immediate) {
                try {
                    _shareData.send(stateSettings.groupUUID)
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun onShareClientUuid() {
        if (!stateSettings.isSharing) {
            viewModelScope.launch(Dispatchers.Main.immediate) {
                try {
                    _shareData.send(stateSettings.clientUUID)
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun shareDone() {
        stateSettings = stateSettings.copy(isSharing = false)
    }

    private fun getStartedKeys(): SettingsState =
        SettingsState(
            clientUUID = pref.getClientUUID(),
            groupUUID = pref.getGroupUUID(),
            isFirstTime = !pref.getStartedKey(),
            userName = if(!pref.getStartedKey() && pref.getUserName().isBlank()) pref.getClientUUID().substringBefore('-') else pref.getUserName(),
            userNameSaved = pref.getUserName(),
            rainbow = pref.getBackgroundState(),
            defaultTypeList = pref.getTypeList()
        ).apply {
            pref.setStartedKey()
        }
}