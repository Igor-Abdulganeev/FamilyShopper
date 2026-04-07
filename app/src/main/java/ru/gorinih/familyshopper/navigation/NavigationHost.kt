package ru.gorinih.familyshopper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.gorinih.familyshopper.ui.screens.dictionary.EditDictionariesScreen
import ru.gorinih.familyshopper.ui.screens.settings.SettingsScreen
import ru.gorinih.familyshopper.ui.screens.started.StartedScreen

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navigationController: NavHostController,
    onExit: () -> Unit,
) {

    fun popBackStack(destination: NavigationKey? = null) {
        when (destination) {
            null -> if (!navigationController.popBackStack()) onExit()
            else -> navigationController.popBackStack(route = destination, true)
        }
    }

    NavHost(
        modifier = modifier,
        navController = navigationController,
        startDestination = NavigationKey.StartedScreen
    ) {
        composable<NavigationKey.SettingsScreen> {
            SettingsScreen()
        }

        composable<NavigationKey.DictionariesScreen> {
            EditDictionariesScreen()
        }

        composable<NavigationKey.StartedScreen> {
            StartedScreen() { key ->
                navigationController.navigate(key)
            }
        }
    }

}