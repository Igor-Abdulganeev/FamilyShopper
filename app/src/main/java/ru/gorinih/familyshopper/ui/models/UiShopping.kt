package ru.gorinih.familyshopper.ui.models

/**
 * State  создания нового/редактирования старого списков
 */

data class UiShopping(
    val listName: String = "", // наименование списка, надо задавать по умолчанию что то
    val listUuid: String = "", // идентификатор списка
    val ownerUuid: String = "", // идентификатор создателя (client)
    val listVersion: Int = 0, // версия списка
    val listLegend: Int = 1, // тип списка из TypeShoppedList
    val tagNames: List<ShoppingItem> = emptyList(), // таги товаров с пометкой то отмечено
    val clientsUuid: List<String> = emptyList(), // задел на будущее - можно будет список назначать пользователю

    val listNameId: Int = 0, // ресурс имени для нового списка
    val isDictionary: Boolean = false, // показать список тэгов сохраненных
    val listAllTags: List<String> = emptyList(), //список всех тэгов для выбора, пока просто без заголовочных букв
)

data class ShoppingItem(
    val tagName: String, //наименование
    val isStrike: Boolean //true - куплен, зачеркнут false - еще нет
)

enum class TypeShoppedList(val listId: Int){
    ALL(listId = 1),
    ADD(listId = 2),
    VIEW(listId = 3),
    PRIVATE(listId = 4)
}
