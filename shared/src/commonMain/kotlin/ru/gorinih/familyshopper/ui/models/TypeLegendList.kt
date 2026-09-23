package ru.gorinih.familyshopper.ui.models

import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.label_icon_add
import familyshopper.shared.generated.resources.label_icon_all
import familyshopper.shared.generated.resources.label_icon_private
import familyshopper.shared.generated.resources.label_icon_view
import org.jetbrains.compose.resources.StringResource

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

enum class TypeLegendList(val listId: Int) {
    NOTHING(listId = 0), // чтобы не красить выбор
    ALL(listId = 1),
    ADD(listId = 2),
    VIEW(listId = 3),
    PRIVATE(listId = 4)
}

fun TypeLegendList.legendListIdName(): StringResource? = when (this.listId) {
    1 -> Res.string.label_icon_all
    2 -> Res.string.label_icon_add
    3 -> Res.string.label_icon_view
    4 -> Res.string.label_icon_private
    else -> null
}
