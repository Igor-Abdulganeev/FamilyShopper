package ru.gorinih.familyshopper.ui.screens.lists.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */

@Parcelize
data class UiListUsers(
    val userUuid: String,
    val userName: String,
    val isSelected: Boolean
) : Parcelable

fun UiListUsers.toShoppedUsers() =
    ShoppedUsers(
        userUuid = this.userUuid,
        userName = this.userName
    )

fun ShoppedUsers.toUiListUsers(check: Boolean = false) =
    UiListUsers(
        userUuid = this.userUuid,
        userName = this.userName,
        isSelected = check
    )
