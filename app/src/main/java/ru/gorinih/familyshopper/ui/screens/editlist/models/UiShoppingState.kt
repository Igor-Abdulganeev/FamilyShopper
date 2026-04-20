package ru.gorinih.familyshopper.ui.screens.editlist.models

import ru.gorinih.familyshopper.domain.models.ShoppedItem
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.ShoppedUsers
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListUsers
import java.util.UUID
import kotlin.String

/**
 * State  создания нового/редактирования старого списков
 */

data class UiShoppingState(
    val listName: String = "", // наименование списка, надо задавать по умолчанию что то
    val listUuid: String = "", // идентификатор списка
    val ownerUuid: String = "", // идентификатор создателя (client)
    val listVersion: Int = 0, // версия списка
    val listLegend: TypeLegendList = TypeLegendList.NOTHING, // тип списка из TypeShoppedList
    val tagNames: List<UiShoppingItem> = emptyList(), // таги товаров с пометкой то отмечено
    val usersUuid: List<String> = emptyList(), // это список пользователей которым назначен список
    val dateTime: Long = 0L, // дата создания/обновления списка
    val userName: String = "", // имя полльзователя что в настройках ввел

    val listNameId: Int = 0, // ресурс имени для нового списка
    val date: String = "", // дата создания, для имени
    val listAllTags: List<String> = emptyList(), //список всех тэгов для выбора, пока просто без заголовочных букв
    val saved: Boolean = false, //лист сохранен нужно вернуться на предыдущий экран
    val loading: Boolean = false, //надо показать загрузку
    val warning: WarningState = WarningState(), // показ ошибки
    val isOwner: Boolean = false,
    val allUsersUuid: List<UiListUsers> = emptyList(), // список всех пользователей
    val error: Boolean = false, // метка сетевой ошибки, когда список сохранен только в БД и не отправлен на сервер, например нет интернета
)

fun UiShoppingState.toShoppedList() =
    ShoppedList(
        listId = this.listUuid.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
        listName = this.listName,
        ownerUuid = this.ownerUuid,
        listVersion = this.listVersion,
        listLegend = this.listLegend.listId,
        tagNames = this.tagNames.map { it.toShoppedItem() },
        usersUuid = this.usersUuid.map { ShoppedUsers(userUuid = it, userName = "") },
        dateTime = this.dateTime,
        countTags = 0, // в обратную сторону в БД они не нужны
        countStrikes = 0,
        userName = this.userName
    )

fun UiShoppingItem.toShoppedItem() =
    ShoppedItem(
        tagId = this.tagId,
        tagName = this.tagName,
        isStrike = this.isStrike,
        tagComment = this.tagComment
    )
