package ru.gorinih.familyshopper.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.TypeListTags
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListObject
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListUser
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme
import ru.gorinih.familyshopper.ui.theme.ListDarkBlue
import ru.gorinih.familyshopper.ui.theme.ListDarkGreen
import ru.gorinih.familyshopper.ui.theme.ListDarkRed
import ru.gorinih.familyshopper.ui.theme.ListDarkYellow
import ru.gorinih.familyshopper.ui.theme.ListLightBlue
import ru.gorinih.familyshopper.ui.theme.ListLightGreen
import ru.gorinih.familyshopper.ui.theme.ListLightRed
import ru.gorinih.familyshopper.ui.theme.ListLightYellow
import ru.gorinih.familyshopper.ui.toShowDate

/**
 * Created by Igor Abdulganeev on 12.04.2026
 */


@Composable
fun TagsList(
    list: List<UiShoppingItem>,
    typeList: TypeListTags,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEditComment: (String, String) -> Unit,
    onFocusRegister: (String, () -> Unit) -> Unit,
    onFocusUnRegister: (String) -> Unit,
    onClearCurrentField: () -> Unit,
) {
    val stateTagsColumn = rememberLazyListState()
    var sizeList by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(list.size) {
        if (list.isNotEmpty() && sizeList < list.count())
            stateTagsColumn.animateScrollToItem(0)
        sizeList = list.count()
    }

    if (list.isEmpty()) {
        Text(
            stringResource(R.string.label_empty_list),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    } else {
        LazyColumn(
            state = stateTagsColumn,
            modifier = modifier
        ) {
            items(list, key = { list -> list.tagName }) { item ->
                TagItem(
                    tag = item,
                    type = typeList,
                    onFocusRegister = onFocusRegister,
                    onFocusUnRegister = onFocusUnRegister,
                    onClearCurrentField = onClearCurrentField,
                    onClick = { onClick(item.tagName) },
                    onDelete = { onDelete(item.tagName) },
                    onEditComment = { comment -> onEditComment(item.tagName, comment) },
                )
            }
        }
    }
}

@Composable
fun TagItem(
    tag: UiShoppingItem,
    type: TypeListTags = TypeListTags.EDIT,
    onClick: (Boolean?) -> Unit,
    onDelete: () -> Unit,
    onEditComment: (String) -> Unit,
    onFocusRegister: (String, () -> Unit) -> Unit,
    onFocusUnRegister: (String) -> Unit,
    onClearCurrentField: () -> Unit,
) {
    val strikeProgress by animateFloatAsState(
        targetValue = if (tag.isStrike) 1f else 0f,
        animationSpec = tween(durationMillis = 350)
    )
    val color = MaterialTheme.colorScheme.primary
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    when (type) {
        TypeListTags.EDIT -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tag.isStrike,
                    onCheckedChange = {
                        onClearCurrentField()
                        onClick(it)
                    },
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = tag.tagName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (tag.isStrike) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    onTextLayout = { textLayoutResult = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = {
                            onClearCurrentField()
                            onClick(null)
                        }
                        )
                        .drawWithContent {
                            drawContent()
                            val layout = textLayoutResult ?: return@drawWithContent
                            if (strikeProgress > 0f) {

                                for (i in 0 until layout.lineCount) {
                                    val lineStart = layout.getLineLeft(i)
                                    val lineEnd = layout.getLineRight(i)
                                    val lineMiddleY =
                                        (layout.getLineTop(i) + layout.getLineBottom(i)) / 2f
                                    val lineWidth = lineEnd - lineStart

                                    var xStart = 0f
                                    var xEnd = 0f
                                    when (tag.isStrike) {
                                        true -> {
                                            xStart = lineStart
                                            xEnd = lineStart + (lineWidth * strikeProgress)
                                        }

                                        false -> {
                                            xStart = lineStart + (lineWidth * (1f - strikeProgress))
                                            xEnd = lineEnd
                                        }
                                    }
                                    drawLine(
                                        color = color,
                                        start = Offset(x = xStart, lineMiddleY),
                                        end = Offset(x = xEnd, lineMiddleY),
                                        strokeWidth = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                )
                EditableCommentChip(
                    tagId = tag.tagName,
                    comment = tag.tagComment,
                    onCommentChange = { comment ->
                        onEditComment(comment)
                    },
                    modifier = Modifier.padding(end = 4.dp),
                    onFocusRegister = onFocusRegister,
                    onFocusUnRegister = onFocusUnRegister,
                )
                IconButton(
                    onClick = {
                        onClearCurrentField()
                        onDelete()
                    },
                ) {
                    Icon(Icons.Default.Clear, null)
                }
            }
        }

        TypeListTags.VIEW -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tag.tagName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    onTextLayout = { textLayoutResult = it },
                    color = if (tag.isStrike) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .drawWithContent {
                            drawContent()
                            val layout = textLayoutResult ?: return@drawWithContent
                            if (strikeProgress > 0f) {

                                for (i in 0 until layout.lineCount) {
                                    val lineStart = layout.getLineLeft(i)
                                    val lineEnd = layout.getLineRight(i)
                                    val lineMiddleY =
                                        (layout.getLineTop(i) + layout.getLineBottom(i)) / 2f
                                    val lineWidth = lineEnd - lineStart

                                    var xStart = 0f
                                    var xEnd = 0f
                                    when (tag.isStrike) {
                                        true -> {
                                            xStart = lineStart
                                            xEnd = lineStart + (lineWidth * strikeProgress)
                                        }

                                        false -> {
                                            xStart = lineStart + (lineWidth * (1f - strikeProgress))
                                            xEnd = lineEnd
                                        }
                                    }
                                    drawLine(
                                        color = color,
                                        start = Offset(x = xStart, lineMiddleY),
                                        end = Offset(x = xEnd, lineMiddleY),
                                        strokeWidth = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                             }
                        }
                )
                if (tag.tagComment.isNotBlank()) {
                    Text(
                        text = "(${tag.tagComment})",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .weight(0.7f)
                    )
                }
            }
        }

        TypeListTags.STRIKE -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = {
                        onClearCurrentField()
                        onClick(null)
                    }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tag.isStrike,
                    onCheckedChange = {
                        onClearCurrentField()
                        onClick(it)
                    },
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = tag.tagName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    onTextLayout = { textLayoutResult = it },
                    color = if (tag.isStrike) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .drawWithContent {
                            drawContent()
                            val layout = textLayoutResult ?: return@drawWithContent
                            if (strikeProgress > 0f) {

                                for (i in 0 until layout.lineCount) {
                                    val lineStart = layout.getLineLeft(i)
                                    val lineEnd = layout.getLineRight(i)
                                    val lineMiddleY =
                                        (layout.getLineTop(i) + layout.getLineBottom(i)) / 2f
                                    val lineWidth = lineEnd - lineStart

                                    var xStart = 0f
                                    var xEnd = 0f
                                    when (tag.isStrike) {
                                        true -> {
                                            xStart = lineStart
                                            xEnd = lineStart + (lineWidth * strikeProgress)
                                        }

                                        false -> {
                                            xStart = lineStart + (lineWidth * (1f - strikeProgress))
                                            xEnd = lineEnd
                                        }
                                    }
                                    drawLine(
                                        color = color,
                                        start = Offset(x = xStart, lineMiddleY),
                                        end = Offset(x = xEnd, lineMiddleY),
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                )
                if (tag.tagComment.isNotBlank()) {
                    Text(
                        text = "(${tag.tagComment})",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .weight(0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun Users(
    list: List<UiListUser>,
    modifier: Modifier = Modifier,
    onEdit: (UiListUser) -> Unit,
    onDelete: (UiListUser) -> Unit,
) {
    val stateList = rememberLazyListState()

        LazyColumn(
            modifier = modifier,
            state = stateList,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            items(list, key = {it.userUuid; it.userName}) { item ->
                User(
                    item = item,
                    onEdit = { onEdit(it) },
                    onDelete = { onDelete(item) }
                )
            }
        }
}

@Composable
fun User(
    item: UiListUser,
    modifier: Modifier = Modifier,
    onEdit: (UiListUser) -> Unit,
    onDelete: () -> Unit,
) {
    var editable by remember { mutableStateOf(false) }
    var editName by rememberSaveable { mutableStateOf("") }
    val focusRequest = remember { FocusRequester() }

    LaunchedEffect(item.userName) {
        editName = item.userName
    }

    LaunchedEffect(editable) {
        if (editable) focusRequest.requestFocus()
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = {
                onDelete()
            },
            modifier = Modifier.weight(0.1f)
        ) {
            Icon(Icons.Default.Clear, null)
        }
        AnimatedContent(
            targetState = editable,
            transitionSpec = { fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 300)) using SizeTransform(clip = false) },
            modifier = Modifier.weight(1.0f)
        )
        { isEdit ->
            when (isEdit) {
                true -> {
                    RoundedTextField(
                        value = editName,
                        onValueChange = { name ->
                            editName = name
                        },
                        modifier = Modifier
                            .focusRequester(focusRequest)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    editable = false
                                    onEdit(item.copy(userName = editName))
                                }
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null)
                            }
                        }
                    )
                }

                false -> {
                    Box(modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                    ){
                        Text(
                            text = editName,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }
                 }
            }
        }
        IconButton(
            onClick = {
                editable = true
            },
            modifier = Modifier.weight(0.1f)
        ) {
            Icon(Icons.Default.EditNote, null)
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewTagItem() {
    FamilyShopperTheme() {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(color = Color.Gray)
        ) {
            TagItem(
                tag = UiShoppingItem(
                    tagId = "asd",
                    tagName = "молоко EDIT",
                    isStrike = true,
                    tagComment = "2 пакетика"
                ),
                type = TypeListTags.EDIT,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )
            TagItem(
                tag = UiShoppingItem(
                    tagId = "asd",
                    tagName = "молоко EDIT",
                    isStrike = false,
                    tagComment = "2 пакетика"
                ),
                type = TypeListTags.EDIT,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )
            TagItem(
                tag = UiShoppingItem(
                    tagId = "asd",
                    tagName = "кефир STRIKE",
                    isStrike = true,
                    tagComment = "42 пакетика"
                ),
                type = TypeListTags.STRIKE,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )
            TagItem(
                tag = UiShoppingItem(
                    tagId = "asd",
                    tagName = "кефир STRIKE",
                    isStrike = false,
                    tagComment = "42 пакетика"
                ),
                type = TypeListTags.STRIKE,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )

            TagItem(
                tag = UiShoppingItem(
                    tagId = "asdfg",
                    tagName = "продукт View",
                    isStrike = true,
                    tagComment = ""
                ),
                type = TypeListTags.VIEW,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )

            TagItem(
                tag = UiShoppingItem(
                    tagId = "asdfg",
                    tagName = "продукт View",
                    isStrike = false,
                    tagComment = "комментарий"
                ),
                type = TypeListTags.VIEW,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )
            TagItem(
                tag = UiShoppingItem(
                    tagId = "asdfg",
                    tagName = "хлеб, много хлеба для длинного наименования",
                    isStrike = false,
                    tagComment = "2 пакетика"
                ),
                type = TypeListTags.EDIT,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = Color.White)
            )
            TagItem(
                tag = UiShoppingItem(
                    tagId = "asdfg",
                    tagName = "хлеб, много хлеба",
                    isStrike = false,
                    tagComment = "2 пакетика проверка вместимости коментария"
                ),
                type = TypeListTags.STRIKE,
                onClick = {},
                onDelete = {},
                onEditComment = {},
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = {},
                onClearCurrentField = {},
            )

        }
    }
}

@Composable
fun CardListSimpleItem(
    item: UiListObject,
 //   onClick: (String, Int, TypeLegendList, String) -> Unit,
    onClick: (String) -> Unit,
) {
    val title = item.listName.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.label_empty_list_name)
    val isDark = isSystemInDarkTheme()
    val progress =
        (item.countStrikes.toFloat() / (item.countTags.takeIf { it > 0 } ?: 1))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .shadow(
                borderRadius = 16.dp,
                shadowRadius = 8.dp,
                alphaShadowLight = 0.3f,
                offsetXLight = 0.dp,
                offsetYLight = 0.dp
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val colorBrush = MaterialTheme.colorScheme.background
        val brush = Brush.horizontalGradient(
            colors = if (isDark) {
                when (item.listLegend) {
                    TypeLegendList.ALL -> listOf(
                        ListDarkGreen,
                        colorBrush,
                    )

                    TypeLegendList.ADD -> listOf(
                        ListDarkBlue,
                        colorBrush,
                    )

                    TypeLegendList.VIEW -> listOf(
                        ListDarkYellow,
                        colorBrush,
                    )

                    TypeLegendList.PRIVATE -> listOf(
                        ListDarkRed,
                        colorBrush,
                    )

                    TypeLegendList.NOTHING -> listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        colorBrush,
                    )
                }
            } else {
                when (item.listLegend) {
                    TypeLegendList.ALL -> listOf(
                        ListLightGreen,
                        colorBrush,
                    )

                    TypeLegendList.ADD -> listOf(
                        ListLightBlue,
                        colorBrush,
                    )

                    TypeLegendList.VIEW -> listOf(
                        ListLightYellow,
                        colorBrush,
                    )

                    TypeLegendList.PRIVATE -> listOf(
                        ListLightRed,
                        colorBrush,
                    )

                    TypeLegendList.NOTHING -> listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        colorBrush,
                    )
                }
            },
            startX = 0.0f,
            endX = 550f
        )

        MaterialGroupBox(
            modifier = Modifier.fillMaxWidth(),
        //    onClick = { onClick(item.listId, item.listVersion, item.listLegend, item.listOwner) },
            onClick = { onClick(item.listId) },
            color = MaterialTheme.colorScheme.primary,
            brush = brush,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    //наименование
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                            .weight(1f),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (!isDark) {
                            Text(
                                text = title,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    drawStyle = Stroke(
                                        width = 4f,
                                        join = StrokeJoin.Round
                                    )
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 16.sp,
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    // время изменения
                    Text(
                        text = item.listDatetimeValue.toShowDate(
                            todayName = stringResource(R.string.label_datetime_today),
                            yesterdayName = stringResource(R.string.label_datetime_yesterday)),
                        style = TextStyle(
                            fontSize = 12.sp,
                            baselineShift = BaselineShift.Subscript
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(0.4f),
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (progress != 0f) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .padding(start = 32.dp),
                            progress = { progress },
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.primary,
                            gapSize = 0.dp,
                            strokeCap = StrokeCap.Butt,
                            drawStopIndicator = {}
                        )
                        if (progress == 1f)
                            Icon(
                                Icons.Default.Done, contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(horizontal = 4.dp)
                            )
                        else Spacer(modifier = Modifier.weight(0.3f))
                    }
                }
                Text(
                    text = stringResource(
                        R.string.label_results_count,
                        item.countStrikes.toString(),
                        item.countTags.toString()
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                )
            }
        }
    }
}
