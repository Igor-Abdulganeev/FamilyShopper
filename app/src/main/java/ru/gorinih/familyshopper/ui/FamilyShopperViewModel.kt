package ru.gorinih.familyshopper.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.ui.theme.ThemeType
import ru.gorinih.familyshopper.ui.theme.models.Palettes

/**
 * Created by Igor Abdulganeev on 03.05.2026
 */

class FamilyShopperViewModel(
    pref: StorageRepository
) : ViewModel() {

    val dynamicColor: Flow<Boolean> = pref.paletteFlow().map { namePalette ->
        val themeType = ThemeType.entries.firstOrNull { it.name == namePalette } ?: ThemeType.MAIN
        (Palettes.palettes.firstOrNull { it.themeType == themeType }
            ?: Palettes.instance()).isDynamic()
    }
}