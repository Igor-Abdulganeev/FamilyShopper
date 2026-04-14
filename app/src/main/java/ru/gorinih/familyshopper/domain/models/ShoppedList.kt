package ru.gorinih.familyshopper.domain.models


/**
 * Created by Igor Abdulganeev on 08.04.2026
 */

data class ShoppedList(
    val listId: String,
    val listName: String,
    val ownerUuid: String,
    val listVersion: Int,
    val listLegend: Int,
    val tagNames: List<ShoppedItem>,
    val usersUuid: List<ShoppedUsers>,
    val dateTime: Long,
    val countTags: Int,
    val countStrikes: Int,
    val userName: String,
)

fun ShoppedList.getNewerOrNull(list: ShoppedList): ShoppedList? =when {
    this.listId != list.listId ->  null // different
    this.listVersion > list.listVersion -> this
    list.listVersion > this.listVersion -> list
    this.dateTime > list.dateTime -> this
    else -> null // same
}

fun ShoppedList.fullCompare(shoppedList: ShoppedList): Boolean = when {
    tagNames.count() != shoppedList.tagNames.count() -> false
    usersUuid.count() != shoppedList.usersUuid.count() -> false
    listName != shoppedList.listName -> false
    listVersion != shoppedList.listVersion -> false
    listLegend != shoppedList.listLegend -> false
    !shoppedList.usersUuid.containsAll(usersUuid) -> false
    !tagNames.all { tag ->
        val listItem = shoppedList.tagNames.findLast { it.tagName == tag.tagName }
                listItem != null &&
                listItem.isStrike == tag.isStrike &&
                listItem.tagComment == tag.tagComment } -> false
    !shoppedList.tagNames.containsAll(tagNames) -> false
    else -> true
}
