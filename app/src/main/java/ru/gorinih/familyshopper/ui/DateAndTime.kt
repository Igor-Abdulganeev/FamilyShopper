package ru.gorinih.familyshopper.ui

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */

fun Long.toShowDate(
    todayName: String,
    yesterdayName: String
): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(this).atZone(zone).toLocalDate()
    val today = LocalDate.now()
    return when (date) {
        today -> todayName
        today.minusDays(1) -> yesterdayName
        else -> date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

}