package ru.gorinih.familyshopper.data.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.models.LegendList

/**
 * Created by Igor Abdulganeev on 27.07.2026
 */

class DesktopStoreRepository() : StoreRepository {
    override suspend fun updatePalette(palette: String) {
        TODO("Not yet implemented")
    }

    override fun paletteFlow(): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getVoice(): Boolean = false

    override fun getVoiceFlow(): Flow<Boolean> = flow { emit(false) }

    override suspend fun setVoice(enabled: Boolean) {}

    override suspend fun setVoiceModel(name: String) {}

    override suspend fun getVoiceModel(): String = "model-en-us"

    override fun getVoiceModelFlow(): Flow<String> = flow { emit("model-en-us") }

    override suspend fun setListSaveTags(listSettings: HashMap<LegendList, Boolean>) {
        TODO("Not yet implemented")
    }

    override fun getListSaveTagsFlow(): Flow<HashMap<LegendList, Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun getListSaveTags(): HashMap<LegendList, Boolean> {
        TODO("Not yet implemented")
    }
}