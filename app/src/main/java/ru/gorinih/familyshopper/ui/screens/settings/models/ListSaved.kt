package ru.gorinih.familyshopper.ui.screens.settings.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.ui.models.TypeLegendList

/**
 * Created by Igor Abdulganeev on 19.05.2026
 */

@Parcelize
data class ListSaved(
    val legend: TypeLegendList = TypeLegendList.NOTHING,
    val enabled: Boolean = false,
): Parcelable
