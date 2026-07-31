package ru.gorinih.familyshopper.ui.views

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.core.os.LocaleListCompat
import ru.gorinih.familyshopper.shared.R
import ru.gorinih.familyshopper.ui.screens.settings.models.AppLanguage
import ru.gorinih.familyshopper.utils.saveLocaleInPreference

/**
 * Created by Igor Abdulganeev on 23.04.2026
 */

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
fun takeLanguages(): Pair<List<AppLanguage>, String> {
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
