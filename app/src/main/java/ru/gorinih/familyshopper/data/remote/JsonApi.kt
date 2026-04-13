package ru.gorinih.familyshopper.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import ru.gorinih.familyshopper.data.remote.models.ListObject
import ru.gorinih.familyshopper.data.remote.models.ListVersionInfo
import ru.gorinih.familyshopper.data.remote.models.RemoteDictionary

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

interface JsonApi {

    //region СЛОВАРИ (Dictionaries) ---

    // Получение списка версий словарей
    @GET("shared_data/{group_id}/dictionaries_versions.json")
    suspend fun getDictionariesVersions(
        @Path("group_id") groupId: String
    ): Response<Map<String, Int>?>

    // Получение всех словарей
    @GET("shared_data/{group_id}/dictionaries.json")
    suspend fun getAllDictionaries(
        @Path("group_id") groupId: String
    ): Response<Map<String, RemoteDictionary>?>

    // Получение словаря выборочно
    @GET("shared_data/{group_id}/dictionaries/{tag_id}.json")
    suspend fun getDictionaryById(
        @Path("group_id") groupId: String,
        @Path("tag_id") tagId: String
    ): Response<RemoteDictionary?>

    // Сохранение/Обновление словаря (Атомарно обновляет и данные, и версию)
    @PATCH("shared_data/{group_id}.json")
    suspend fun updateDictionaryWithVersion(
        @Path("group_id") groupId: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any> // В Body: {"dictionaries/A": dictionaryObject, "dictionaries_versions/A": 2}
    )

    //endregion

    // --- СПИСКИ ПОКУПОК (Current Lists) ---

    // Получение списка версий списков покупок
    @GET("shared_data/{group_id}/current_lists_versions.json")
    suspend fun getListsVersions(
        @Path("group_id") groupId: String
    ): Response<Map<String, ListVersionInfo>?>

    // Обновление списка покупок
    @PATCH("shared_data/{group_id}.json")
    suspend fun updateListWithVersion(
        @Path("group_id") groupId: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any> // В Body: {"current_lists/ID": listObject, "current_lists_versions/ID": ListVersionInfo}
    ): Response<Unit>

    // Получение всех списков, приватные фильтруем в коде
    @GET("shared_data/{group_id}/current_lists.json")
    suspend fun getAllCurrentLists(
        @Path("group_id") groupId: String
    ): Response<Map<String, ListObject>?>

    // Получение конкретного списка, приватные фильтруем в коде
    @GET("shared_data/{group_id}/current_lists/{list_id}.json")
    suspend fun getCurrentListById(
        @Path("group_id") groupId: String,
        @Path("list_id") listId: String
    ): Response<ListObject?>

    /*

        // Удаление списка (Удаляет из двух веток сразу через PATCH со значениями null)
        @PATCH("shared_data/{group_id}.json")
        suspend fun deleteListWithVersion(
            @Path("group_id") groupId: String,
            @Body updates: Map<String, Any?> // В Body: {"current_lists/ID": null, "current_lists_versions/ID": null}
        ): Response<Unit>


     */
}
