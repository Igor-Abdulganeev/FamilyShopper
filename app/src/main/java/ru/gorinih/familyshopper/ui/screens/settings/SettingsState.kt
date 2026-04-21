package ru.gorinih.familyshopper.ui.screens.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListUser

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */
@Parcelize
data class SettingsState(
    val clientUUID: String,
    val groupUUID: String,
    val isFirstTime: Boolean, // первый запуск или нет
    val userName: String, // имя пользователя, по желанию
    val userNameSaved: String, // старое имя, будем сохранять если отличия есть
    val rainbow: Boolean, // анимированный или нет фон
    val defaultTypeList: Int, // используемый по умолчанию тип списка при создании
    val isSharing: Boolean = false, //шаринг данных запущен
    val listUsers: List<UiListUser> = emptyList(),//список имеющихся поьзователей
): Parcelable
