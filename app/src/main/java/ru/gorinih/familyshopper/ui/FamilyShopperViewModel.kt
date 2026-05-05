package ru.gorinih.familyshopper.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.StorageRepository

/**
 * Created by Igor Abdulganeev on 03.05.2026
 */

class FamilyShopperViewModel(
    pref: StorageRepository
) : ViewModel() {

    val dynamicColor: Flow<Boolean> = pref.paletteFlow().map { it.isDynamic() }

}