package ru.gorinih.familyshopper.navigation

/**
 * переопределения action навигации
 */

data class NavigationActions(
    val onBackClick: () -> Unit = {}
)
