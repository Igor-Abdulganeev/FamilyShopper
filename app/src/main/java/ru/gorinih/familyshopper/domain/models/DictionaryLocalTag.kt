package ru.gorinih.familyshopper.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

@Parcelize
data class DictionaryLocalTag(
    val tagId: String,
    val tagName: String,
    val needUpdate: Boolean = false
): Parcelable