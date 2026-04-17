package ru.gorinih.familyshopper.domain.models

/**
 * для возврата результата/ошибок при необходимости
 */

data class Results(
    val isError: Boolean,
    val textError: String = "",
    val textComplete: String = ""
)
