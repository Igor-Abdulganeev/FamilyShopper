package ru.gorinih.familyshopper.domain.models

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

data class ListRemoteInfo(
    val listVersion: Int,
    val listLegend: Int,
    val listOwner: String,
    val listDatetime: Long,
)
