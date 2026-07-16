package ru.gorinih.familyshopper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


/**
 * Created by Igor Abdulganeev on 16.07.2026
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            App(
                finishApp = { finishAfterTransition() }
            )
        }

    }
    /*
        override fun attachBaseContext(newBase: Context?) {
            val context = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && newBase != null) {
                val code = getLocaleFromPreference(newBase)
                LocaleHelper.wrap(newBase, code)
            } else {
                newBase
            }
            super.attachBaseContext(newBase)
        }
        */
}