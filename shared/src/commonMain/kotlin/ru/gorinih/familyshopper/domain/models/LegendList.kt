package ru.gorinih.familyshopper.domain.models

/**
 * Created by Igor Abdulganeev on 19.05.2026
 */

enum class LegendList(val listId: Int) {
    ALL(listId = 1),
    ADD(listId = 2),
    VIEW(listId = 3),
    PRIVATE(listId = 4)
}