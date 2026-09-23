package ru.gorinih.familyshopper.utils

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

/**
 * Created by Igor Abdulganeev on 28.07.2026
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

