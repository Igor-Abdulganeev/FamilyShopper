package ru.gorinih.familyshopper.ui.views

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.screens.settings.models.AppLanguage
import java.util.Locale

/**
 * Created by Igor Abdulganeev on 23.04.2026
 */
// region костыль для старых андроидов
fun saveLocaleInPreference(context: Context, code: String) {
    context.getSharedPreferences("family_locale_settings", Context.MODE_PRIVATE).edit {
        putString("family_locale_code", code)
    }
}

fun getLocaleFromPreference(context: Context): String =
    context.getSharedPreferences("family_locale_settings", Context.MODE_PRIVATE)
        .getString("family_locale_code", "system") ?: "system"

object LocaleHelper {

    fun wrap(context: Context, language: String): Context {
        if (language == "system") return context

        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
//endregion

fun setAppLanguage(context: Context, code: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val localeManager = context.getSystemService(LocaleManager::class.java)
        if (code == "system") {
            localeManager.applicationLocales =
                LocaleListCompat.getEmptyLocaleList().unwrap() as LocaleList
        } else {
            localeManager?.applicationLocales = LocaleList.forLanguageTags(code)
        }
    } else {
        saveLocaleInPreference(context, code)
        val locales = if (code == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(code)
        }
        AppCompatDelegate.setApplicationLocales(locales)
        (context as? Activity?)?.recreate()
    }
}

@Composable
fun rememberLanguages(): Pair<List<AppLanguage>, String> {
    val codes = stringArrayResource(R.array.languages_codes)
    val names = stringArrayResource(R.array.languages_names)

    val context = LocalContext.current

    val languages = codes.zip(names).map { (c, n) ->
        AppLanguage(c, n)
    }

    val currentCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales
            .let { if (it.isEmpty) "system" else it[0].toLanguageTag() }
    } else {
        val currentLocales =
            AppCompatDelegate.getApplicationLocales()
        if (currentLocales.isEmpty) {
            "system"
        } else {
            currentLocales.toLanguageTags()
        }
    }

    return languages to currentCode
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier
) {
    val (languages, currentCode) = rememberLanguages()
    val context = LocalContext.current

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
                            Text(lang.title)

                            if (lang.code == currentCode) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null
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