package ru.gorinih.familyshopper.domain

import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.domain.models.LegendList

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

interface StoreRepository {

    suspend fun updatePalette(palette: String)

    fun paletteFlow(): Flow<String>

    suspend fun getVoice(): Boolean

    fun getVoiceFlow(): Flow<Boolean>

    suspend fun setVoice(enabled: Boolean)

    suspend fun setVoiceModel(name: String)

    suspend fun getVoiceModel(): String

    fun getVoiceModelFlow(): Flow<String>

    suspend fun setListSaveTags(listSettings: HashMap<LegendList, Boolean>)

    fun getListSaveTagsFlow(): Flow<HashMap<LegendList, Boolean>>

    suspend fun getListSaveTags(): HashMap<LegendList, Boolean>
}