package ru.gorinih.familyshopper.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.toolbar_main_header
import org.jetbrains.compose.resources.stringResource
import ru.gorinih.familyshopper.BuildKonfig
import ru.gorinih.familyshopper.di.initKoin
import ru.gorinih.familyshopper.voice.LocalVoicePermission

/**
 * Created by Igor Abdulganeev on 03.07.2026
 */

fun main() = application {
    initKoin(
        baseUrl = "${BuildKonfig.BASE_POINT}${BuildKonfig.BASE_SERVER}",
        isDebug = BuildKonfig.DEBUG
    ) {
    }

    val windowTitle = stringResource(Res.string.toolbar_main_header)
    Window(onCloseRequest = ::exitApplication, title = windowTitle) {
        CompositionLocalProvider(
            LocalVoicePermission provides null,
            LocalDynamicColorsSupported provides false
        ) {
            App() {}
        }
    }
}

@Composable
actual fun AppBackHandler(enable: Boolean, onBack: () -> Unit) {
}
