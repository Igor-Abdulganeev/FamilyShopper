package ru.gorinih.familyshopper.ui.screens.lists.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.ui.models.TypeLegendList

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Parcelize
data class UiListObject(
    val listId: String,
    val listVersion: Int,
    val listName: String,
    val listLegend: TypeLegendList,
    val listOwner: String,
    val listTo: List<UiListUser> = emptyList(),
    val listDatetimeValue: Long,
    val countTags: Int,
    val countStrikes: Int,
    val userName: String,
    val isEdit: Boolean = false,
    val isDelete: Boolean = false,
) : Parcelable

fun ShoppedList.toUiListObject() =
    UiListObject(
        listId = this.listId,
        listVersion = this.listVersion,
        listName = this.listName,
        listLegend = TypeLegendList.entries.first { it.listId == this.listLegend },
        listOwner = this.ownerUuid,
        listTo = this.usersUuid.map { it.toUiListUsers() },
        listDatetimeValue = this.dateTime,
        countTags = this.countTags,
        countStrikes = this.countStrikes,
        userName = this.userName
    )
