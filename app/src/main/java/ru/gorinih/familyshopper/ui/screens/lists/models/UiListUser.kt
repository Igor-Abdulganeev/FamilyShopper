package ru.gorinih.familyshopper.ui.screens.lists.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */

@Parcelize
data class UiListUser(
    val userUuid: String,
    val userName: String,
    val isSelected: Boolean
) : Parcelable

fun UiListUser.toShoppedUsers() =
    ShoppedUsers(
        userUuid = this.userUuid,
        userName = this.userName
    )

fun ShoppedUsers.toUiListUsers(check: Boolean = false) =
    UiListUser(
        userUuid = this.userUuid,
        userName = this.userName,
        isSelected = check
    )
