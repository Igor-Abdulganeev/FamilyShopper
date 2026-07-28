package ru.gorinih.familyshopper.ui.screens.about

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

/**
 * Created by Igor Abdulganeev on 28.07.2026
 */

@Composable
actual fun rememberAppVersion(): String {
    val context = LocalContext.current
    return rememberSaveable {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.longVersionCode
            "$versionName ($versionCode)"
        } catch (_: PackageManager.NameNotFoundException) {
            ""
        }

    }
}