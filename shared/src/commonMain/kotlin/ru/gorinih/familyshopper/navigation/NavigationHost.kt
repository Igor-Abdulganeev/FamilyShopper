package ru.gorinih.familyshopper.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
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
    navigationActions: (NavigationActions) -> Unit
) {

    NavHost(
        modifier = modifier,
        navController = navigationController,
        startDestination = startedScreenKey,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(durationMillis = 500)) + fadeIn(animationSpec = tween(durationMillis = 200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 250))
        }
    ) {
        composable<NavigationKey.ListEntityScreen> {
            ListEntityScreen(
                router = { navigationKey ->
                    navigationController.navigate(navigationKey)
                },
                onClose = { onExit() },
                navigationActions = navigationActions,
                addList = {
                    navigationController.navigate(NavigationKey.EditListScreen(listUuid = ""))
                }
            )
        }

        composable<NavigationKey.SettingsScreen> {
            SettingsScreen(
                navigationActions = navigationActions,
                onBack = { navigationController.popBackStack() },
                firstTimeBackPressed = {
                    navigationController.navigate(NavigationKey.ListEntityScreen) {
                        popUpTo(NavigationKey.SettingsScreen) {
                            inclusive = true
                        }
                    }
                })
        }

        composable<NavigationKey.DictionariesScreen> {
            EditDictionariesScreen(
                onBack = { navigationController.popBackStack() },
                navigationActions = navigationActions
            )
        }

        composable<NavigationKey.EditListScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationKey.EditListScreen>()
            EditListScreen(
                listUuid = args.listUuid, onBack = { navigationController.popBackStack() },
                navigationActions = navigationActions
            )
        }

        composable<NavigationKey.ListStrikeTagsScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationKey.ListStrikeTagsScreen>()
                        ListStrikeTagsScreen(
                            listUuid = args.listUuid,
                            onBack = { navigationController.popBackStack() },
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