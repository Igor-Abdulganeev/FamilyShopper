package ru.gorinih.familyshopper.ui.screens.settings.models

import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.ui.models.TypeLegendList

/**
 * Created by Igor Abdulganeev on 19.05.2026
 */

@Serializable
data class ListSaved(
    val legend: TypeLegendList = TypeLegendList.NOTHING,
    val enabled: Boolean = false,
)
