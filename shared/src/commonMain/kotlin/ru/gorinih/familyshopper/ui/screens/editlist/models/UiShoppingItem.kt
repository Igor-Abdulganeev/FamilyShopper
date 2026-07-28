package ru.gorinih.familyshopper.ui.screens.editlist.models

import ru.gorinih.familyshopper.domain.models.ShoppedItem

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class UiShoppingItem(
    val tagId: String,
    val tagName: String, //наименование
    val isStrike: Boolean, //true - куплен, зачеркнут false - еще нет
    val tagComment: String, // добавлено поле сюда можно например количество записать или еще что
)

fun ShoppedItem.toUiShoppingItem() =
    UiShoppingItem(
        tagId = this.tagId,
        tagName = this.tagName,
        isStrike = this.isStrike,
        tagComment = this.tagComment
    )