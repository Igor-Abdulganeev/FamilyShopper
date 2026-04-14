package ru.gorinih.familyshopper.ui.screens.lists.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.domain.models.ShoppedList
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Parcelize
data class UiListObject(
    val listId: String,
    val listVersion: Int,
    val listName: String,
    val listLegend: Int,
    val listOwner: String,
    val listTo: List<UiListUsers> = emptyList(),
    val listDatetime: String,
    val countTags: Int,
    val countStrikes: Int,
    val userName: String
) : Parcelable

fun ShoppedList.toUiListObject() =
    UiListObject(
        listId = this.listId,
        listVersion = this.listVersion,
        listName = this.listName,
        listLegend = this.listLegend,
        listOwner = this.ownerUuid,
        listTo = this.usersUuid.map { it.toUiListUsers() },
        listDatetime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(this.dateTime)),
        countTags = this.countTags,
        countStrikes = this.countStrikes,
        userName = this.userName
    )
