package ru.gorinih.familyshopper.ui.screens.list.models

import ru.gorinih.familyshopper.domain.models.ShoppedItem
import ru.gorinih.familyshopper.domain.models.ShoppedList
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
    val listLegend: Int = 0, // тип списка из TypeShoppedList
    val tagNames: List<UiShoppingItem> = emptyList(), // таги товаров с пометкой то отмечено
    val clientsUuid: List<String> = emptyList(), // задел на будущее - можно будет список назначать пользователю
    val dateTime: Long = 0L, // дата создания/обновления списка

    val listNameId: Int = 0, // ресурс имени для нового списка
    val date: String = "", // дата создания, для имени
    val isDictionary: Boolean = false, // показать список тэгов сохраненных
    val listAllTags: List<String> = emptyList(), //список всех тэгов для выбора, пока просто без заголовочных букв
    val saved: Boolean = false, //лист сохранен нужно вернуться на предыдущий экран
    val loading: Boolean = false, //надо показать загрузку
    val error: String? = null, // показ ошибки
    val isAdd: Boolean = true, // можно ли добавлять - нет если owner другой и listLegend >2
    val isEdit: Boolean = true, // можно ли редактировать - нет если owner другой и listLegend >1
)

fun UiShoppingState.toShoppedList() =
    ShoppedList(
        listId = this.listUuid.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
        listName = this.listName,
        ownerUuid = this.ownerUuid,
        listVersion = this.listVersion,
        listLegend = this.listLegend,
        tagNames = this.tagNames.map { it.toShoppedItem() },
        clientsUuid = this.clientsUuid,
        dateTime = this.dateTime,
        countTags = 0, // в обратную сторону в БД они не нужны
        countStrikes = 0,
)

fun UiShoppingItem.toShoppedItem() =
    ShoppedItem(
        tagId = this.tagId,
        tagName = this.tagName,
        isStrike = this.isStrike,
        tagComment = this.tagComment
    )
