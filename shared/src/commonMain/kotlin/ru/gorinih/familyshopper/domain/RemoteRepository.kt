package ru.gorinih.familyshopper.domain

import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

interface RemoteRepository {
    /**
     * Сохранение/Обновление данных на сервер
     *      для словаря: В Body: {"dictionaries/A": dictionaryObject, "dictionaries_versions/A": 2}
     *      для списков: В Body: {"current_lists/ID": listObject, "current_lists_versions/ID": ListVersionInfo}
     *      для пользователей: В Body: {"current_users/ID": userObject}
     *
     * Удаление аналогично для всех
     *      В Body: {"current_lists/ID": null, "current_lists_versions/ID": null}
     */
    suspend fun updateDictionaryWithVersion(updates: List<DictionaryRemoteTag>)
    suspend fun updateListWithVersion(updates: List<ShoppedList>)
    suspend fun setUserName()
    suspend fun deleteListWithVersion(listId: String)

    // Получение всех пользователей
    suspend fun getUsersNames(): Map<String, String>

    // Получение списка версий словарей
    suspend fun getDictionariesVersions(): Map<String, Int>

    // Получение всех словарей
    suspend fun getAllDictionaries(): Map<String, DictionaryRemoteTag>

    // Получение словаря выборочно
    suspend fun getDictionaryById(tagId: String): DictionaryRemoteTag

    // Получение списка версий списков покупок
    suspend fun getListsVersions(): Map<String, ListRemoteInfo>

    // Получение всех списков, приватные фильтруем в коде
    suspend fun getAllCurrentLists(): Map<String, ShoppedList>

    // Получение конкретного списка, приватные фильтруем в коде
    suspend fun getCurrentListById(listId: String): ShoppedList?
}