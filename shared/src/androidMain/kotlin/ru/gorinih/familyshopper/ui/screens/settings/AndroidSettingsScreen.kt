package ru.gorinih.familyshopper.ui.screens.settings

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.glance.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ru.gorinih.familyshopper.shared.R
import ru.gorinih.familyshopper.ui.views.RoundedTextField
import ru.gorinih.familyshopper.ui.views.setAppLanguage
import ru.gorinih.familyshopper.ui.views.takeLanguages
import ru.gorinih.familyshopper.voice.VoicePermissionProvide

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

@Composable
actual fun rememberShareEventsHandler(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { uuid ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, uuid)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            context.startActivity(shareIntent)
        }
    }
}

@Composable
actual fun SettingsBackHandler(enable: Boolean, onBack: () -> Unit) {
    BackHandler(enable) {
        onBack.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun LanguageSelector(modifier: Modifier) {
    val (languages, currentCode) = takeLanguages()
    val context = androidx.compose.ui.platform.LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    val current = languages.find { it.code == currentCode }
        ?: languages.first()

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        RoundedTextField(
            value = current.title,
            onValueChange = {},
            isEditable = false,
            label = stringResource(R.string.label_language_selector),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lang.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            if (lang.code == currentCode) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    onClick = {
                        expanded = false
                        setAppLanguage(context, lang.code)
                    }
                )
            }
        }
    }
}

@Composable
actual fun rememberVoicePermissionStatus(provider: VoicePermissionProvide?): Boolean {
    if (provider == null) return false
    var isGrantedState by remember { mutableStateOf(provider.isVoiceGranted()) }
    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isGrantedState = provider.isVoiceGranted()
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }

    return isGrantedState
}