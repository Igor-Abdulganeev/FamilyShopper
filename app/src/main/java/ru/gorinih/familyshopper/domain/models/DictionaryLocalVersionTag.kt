package ru.gorinih.familyshopper.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

@Parcelize
data class DictionaryLocalVersionTag (
    val tagId: String,
    val tagVersion: Int,
    val tagNames: List<String>
): Parcelable