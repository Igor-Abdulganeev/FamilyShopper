package ru.gorinih.familyshopper.navigation

import kotlinx.serialization.Serializable

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

@Serializable
sealed class NavigationKey {

    @Serializable
    data object SettingsScreen: NavigationKey()

    @Serializable
    data object DictionariesScreen: NavigationKey()

    @Serializable
    data object StartedScreen: NavigationKey()

}