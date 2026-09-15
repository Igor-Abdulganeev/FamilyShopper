package ru.gorinih.familyshopper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Created by Igor Abdulganeev on 31.07.2026
 */

@Composable
actual fun AppBackHandler(enable: Boolean, onBack: () -> Unit) {
    val currentCallback by rememberUpdatedState(onBack)
    BackHandler(enable) {
        currentCallback.invoke()
    }
}
