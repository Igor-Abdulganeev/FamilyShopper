package ru.gorinih.familyshopper.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import ru.gorinih.familyshopper.ui.theme.ListHeaderBlue
import ru.gorinih.familyshopper.ui.theme.ListHeaderGreen
import ru.gorinih.familyshopper.ui.theme.ListHeaderRed
import ru.gorinih.familyshopper.ui.theme.ListHeaderYellow

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */

object GlassCircleImageHolder {
    val typedList = mapOf<Int, Painter>(
        1 to GlassCircleImage(ListHeaderGreen),
        2 to GlassCircleImage(ListHeaderBlue),
        3 to GlassCircleImage(ListHeaderYellow),
        4 to GlassCircleImage(ListHeaderRed),
    )

    fun getImage(listLegend: Int): Painter =
        if (typedList.containsKey(listLegend)) typedList[listLegend]
            ?: GlassCircleImage(Color.Green) else GlassCircleImage(Color.Green)
}