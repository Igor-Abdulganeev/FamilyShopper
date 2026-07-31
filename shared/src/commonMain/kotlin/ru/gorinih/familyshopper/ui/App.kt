package ru.gorinih.familyshopper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.toolbar_main_header
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.domain.PreferenceRepository
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.navigation.NavigationHost
import ru.gorinih.familyshopper.navigation.NavigationKey
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * Created by Igor Abdulganeev on 03.07.2026
 */
val LocalDynamicColorsSupported = staticCompositionLocalOf { false }

@Composable
expect fun AppBackHandler(enable: Boolean, onBack: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    finishApp: () -> Unit,
) {
    val viewModel: FamilyShopperViewModel = koinViewModel()
    val isDynamicColor by viewModel.dynamicColor.collectAsState(initial = false)
    FamilyShopperTheme(
        dynamicColor = isDynamicColor,
        dataPreferenceRepository = getKoin().get()
    ) {
        val navController = rememberNavController()
        var navigationActions by remember {
            mutableStateOf(
                NavigationActions(
                    onNavigationClick = { navController.popBackStack() })
            )
        }
        val backStackEntry by navController.currentBackStackEntryAsState()
        val pref: PreferenceRepository = koinInject()
        val startedKey: NavigationKey = when (pref.getStartedKey()) {
            true -> NavigationKey.ListEntityScreen
            false -> NavigationKey.SettingsScreen
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.toolbar_main_header),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigationActions.onNavigationClick()
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (
                                backStackEntry?.destination?.route?.contains("SettingsScreen") != true
                            ) {
                                IconButton(
                                    enabled = navController.currentDestination?.route?.contains(
                                        "DictionariesScreen"
                                    ) == false,
                                    onClick = {
                                        navController.navigate(NavigationKey.DictionariesScreen)
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Notes,
                                        contentDescription = null
                                    )
                                }
                            }
                            IconButton(
                                enabled = navController.currentDestination?.route?.contains(
                                    "SettingsScreen"
                                ) == false,
                                onClick = {
                                    navController.navigate(NavigationKey.SettingsScreen)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                NavigationHost(
                    startedScreenKey = startedKey,
                    navigationController = navController,
                    onExit = { finishApp() },
                    navigationActions = { actions -> navigationActions = actions }
                )
            }
        }
    }
}