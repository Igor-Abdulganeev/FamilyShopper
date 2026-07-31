package ru.gorinih.familyshopper.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.label_language_selector
import familyshopper.shared.generated.resources.languages_codes
import familyshopper.shared.generated.resources.languages_names
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import ru.gorinih.familyshopper.ui.screens.settings.models.AppLanguage
import ru.gorinih.familyshopper.ui.views.RoundedTextField
import ru.gorinih.familyshopper.voice.VoicePermissionProvide
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.Locale

/**
 * Created by Igor Abdulganeev on 30.07.2026
 */

@Composable
actual fun rememberShareEventsHandler(): (String) -> Unit {
    /*
        val text = stringResource(Res.string.error_title_text)
        Snackbar(
            snackbarData = object : SnackbarData {
                override val message: String = text

                override val actionLabel: String? = null
                override val duration: SnackbarDuration = SnackbarDuration.Indefinite

                override fun performAction() {
                }

                override fun dismiss() {
                }
            }
        )
    */

    return remember {
        { uuid ->
            try {
                val selectionString = StringSelection(uuid)
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(selectionString, null)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }
}

@Composable
actual fun LanguageSelector(modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var currentCode by remember { mutableStateOf(Locale.getDefault().language) }
    val languages = takeLanguages()
    val current = languages.firstOrNull { it.code == currentCode } ?: languages.first()

    Box(modifier = Modifier) {
        RoundedTextField(
            value = current.title,
            onValueChange = {},
            isEditable = false,
            // Используем мультиплатформенный Res!
            label = stringResource(Res.string.label_language_selector),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    // Можно использовать обычную иконку стрелочки вниз
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = language.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (language.code == currentCode) {
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
                        currentCode = language.code
                        Locale.setDefault(Locale.forLanguageTag(language.code))
                    }
                )
            }
        }
    }
}

@Composable
actual fun rememberVoicePermissionStatus(provider: VoicePermissionProvide?): Boolean = false

@Composable
private fun takeLanguages(): List<AppLanguage> {
    val codes = stringArrayResource(Res.array.languages_codes)
    val names = stringArrayResource(Res.array.languages_names)

    return codes.zip(names).map { (c, n) ->
        AppLanguage(c, n)
    }.filter { it.code != "system" }
}