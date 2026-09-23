package ru.gorinih.familyshopper

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.ui.App
import ru.gorinih.familyshopper.ui.LocalDynamicColorsSupported
import ru.gorinih.familyshopper.ui.widget.WidgetLists
import ru.gorinih.familyshopper.utils.LocaleHelper
import ru.gorinih.familyshopper.utils.getLocaleFromPreference
import ru.gorinih.familyshopper.voice.LocalVoicePermission
import ru.gorinih.familyshopper.voice.VoicePermissionHandler
import ru.gorinih.familyshopper.voice.VoicePermissionProvide

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val voiceHandler = VoicePermissionHandler(activityResultRegistry, this)
        val voiceProvider = object : VoicePermissionProvide {
            override fun isVoiceGranted(): Boolean = voiceHandler.isVoiceGranted()

            override fun requestVoicePermission(callback: (Boolean) -> Unit) =
                voiceHandler.requestVoicePermission(callback)
        }
        val isDynamicColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        lifecycleScope.launch {
            WidgetLists().updateAll(application.applicationContext)
        }

        setContent {
            CompositionLocalProvider(
                LocalVoicePermission provides voiceProvider,
                LocalDynamicColorsSupported provides isDynamicColorsSupported
            ) {
                App(
                    finishApp = { finishAfterTransition() }
                )
            }
        }

    }

    /**
     * поддержка смены локали старых андроидов
     */
    override fun attachBaseContext(newBase: Context?) {
        val context = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && newBase != null) {
            val code = getLocaleFromPreference(newBase)
            LocaleHelper.wrap(newBase, code)
        } else {
            newBase
        }
        super.attachBaseContext(context)
    }
}

