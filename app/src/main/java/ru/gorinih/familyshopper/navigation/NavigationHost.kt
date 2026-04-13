package ru.gorinih.familyshopper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.gorinih.familyshopper.ui.screens.dictionary.EditDictionariesScreen
import ru.gorinih.familyshopper.ui.screens.editlist.EditListScreen
import ru.gorinih.familyshopper.ui.screens.lists.ListEntityScreen
import ru.gorinih.familyshopper.ui.screens.settings.SettingsScreen
import ru.gorinih.familyshopper.ui.screens.started.StartedScreen
import ru.gorinih.familyshopper.ui.screens.strikelist.ListStrikeTagsScreen

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    startedScreenKey: NavigationKey,
    navigationController: NavHostController,
    onExit: () -> Unit,
    navigationActions: (NavigationActions) -> Unit = {}
) {

    fun popupBackStack(destination: NavigationKey? = null) {
        when (destination) {
            null -> if (!navigationController.popBackStack()) onExit()
            else -> navigationController.popBackStack(route = destination, true)
        }
    }

    NavHost(
        modifier = modifier,
        navController = navigationController,
        startDestination = startedScreenKey
    ) {
        composable<NavigationKey.SettingsScreen> {
            SettingsScreen(
                navigationActions = navigationActions,
                backPressed = { popupBackStack() },
                firstTimeBackPressed = {
                    navigationController.navigate(NavigationKey.StartedScreen) {
                        popUpTo(NavigationKey.SettingsScreen) {
                            inclusive = true
                        }
                    }
                })
        }

        composable<NavigationKey.DictionariesScreen> {
            EditDictionariesScreen()
        }

        composable<NavigationKey.StartedScreen> {
            StartedScreen(
                router = { key ->
                    navigationController.navigate(key)
                },
                backClick = { popupBackStack() },
                navigationActions = navigationActions
            )
        }

        composable<NavigationKey.EditListScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationKey.EditListScreen>()
            EditListScreen(args.listUuid, router = { popupBackStack() })
        }

        composable<NavigationKey.ListEntityScreen> {
            ListEntityScreen(
                router = { listId ->
                    navigationController.navigate(NavigationKey.ListStrikeTagsScreen(listUuid = listId))
                },
                addList = {
                    navigationController.navigate(NavigationKey.EditListScreen(listUuid = ""))
                }
            )
        }

        composable<NavigationKey.ListStrikeTagsScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationKey.ListStrikeTagsScreen>()
            ListStrikeTagsScreen(
                listUuid = args.listUuid,
                backPressed = { popupBackStack() },
                route = { listId ->
                    navigationController.navigate(
                        NavigationKey.EditListScreen(
                            listUuid = listId
                        )
                    )
                },
                navigationActions = navigationActions
            )
        }
    }

}