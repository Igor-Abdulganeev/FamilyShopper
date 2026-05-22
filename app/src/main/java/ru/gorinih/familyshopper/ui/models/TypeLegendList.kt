package ru.gorinih.familyshopper.ui.models

import ru.gorinih.familyshopper.R

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

fun TypeLegendList.legendListIdName(): Int = when (this.listId) {
    1 -> R.string.label_icon_all
    2 -> R.string.label_icon_add
    3 -> R.string.label_icon_view
    4 -> R.string.label_icon_private
    else -> 0
}
