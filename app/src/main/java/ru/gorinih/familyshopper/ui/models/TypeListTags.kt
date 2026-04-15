package ru.gorinih.familyshopper.ui.models

/**
 * Created by Igor Abdulganeev on 15.04.2026
 */

enum class TypeListTags {
    EDIT,
    STRIKE,
    VIEW
}

fun TypeListTags.isEdit() = this.name == TypeListTags.EDIT.name

fun TypeListTags.isStrike() = this.name == TypeListTags.STRIKE.name

fun TypeListTags.isView() = this.name == TypeListTags.VIEW.name