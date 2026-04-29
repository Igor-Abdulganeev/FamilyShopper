package ru.gorinih.familyshopper

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.navigation.NavigationHost
import ru.gorinih.familyshopper.navigation.NavigationKey
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme
import ru.gorinih.familyshopper.ui.views.LocaleHelper
import ru.gorinih.familyshopper.ui.views.getLocaleFromPreference
import ru.gorinih.familyshopper.ui.widget.WidgetLists

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            WidgetLists().updateAll(application.applicationContext)
        }
        setContent {
            FamilyShopperTheme {
                val navController = rememberNavController()
                var navigationActions by remember { mutableStateOf(NavigationActions(onNavigationClick = { navController.popBackStack()})) }
                val backStackEntry by navController.currentBackStackEntryAsState()
                val pref: StorageRepository = koinInject()
                val startedKey: NavigationKey = when (pref.getStartedKey()) {
                    true -> NavigationKey.ListEntityScreen
                    false -> NavigationKey.SettingsScreen
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.toolbar_main_header)) },
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
                                            enabled = navController.currentDestination?.route?.contains("DictionariesScreen") == false,
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
                                        enabled = navController.currentDestination?.route?.contains("SettingsScreen") == false,
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
                            onExit = { finishAfterTransition() },
                            navigationActions = {actions -> navigationActions = actions}
                        )
                    }
                }
            }
        }
    }

    /**
     * поддержка смены локали старых андроидов
     */
    override fun attachBaseContext(newBase: Context?) {
        val context = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && newBase!= null) {
            val code = getLocaleFromPreference(newBase)
            LocaleHelper.wrap(newBase, code)
        } else { newBase }
        super.attachBaseContext(context)
    }
}