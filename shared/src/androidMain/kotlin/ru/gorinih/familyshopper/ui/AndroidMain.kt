package ru.gorinih.familyshopper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Created by Igor Abdulganeev on 31.07.2026
 */

@Composable
actual fun AppBackHandler(enable: Boolean, onBack: () -> Unit) {
    BackHandler(enable) {
        onBack.invoke()
    }
}
