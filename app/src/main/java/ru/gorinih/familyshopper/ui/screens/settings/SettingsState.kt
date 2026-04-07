package ru.gorinih.familyshopper.ui.screens.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */
@Parcelize
data class SettingsState(
    val clientUUID: String,
    val groupUUID: String,
): Parcelable
