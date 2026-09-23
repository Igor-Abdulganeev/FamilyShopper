package ru.gorinih.familyshopper.ui.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import familyshopper.shared.generated.resources.Res

/**
 * Created by Igor Abdulganeev on 28.07.2026
 */

@Composable
actual fun rememberAppVersion(): String {
    var version by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        version = try {
            val bytes = Res.readBytes("files/version.txt")
            bytes.decodeToString()
        } catch (_: Throwable) {
            ""
        }
    }
    return version
}