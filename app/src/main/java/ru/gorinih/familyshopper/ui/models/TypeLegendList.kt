package ru.gorinih.familyshopper.ui.models

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