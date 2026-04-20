package ru.gorinih.familyshopper.ui.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.models.TypeListTags
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem

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
    var sizeList by rememberSaveable { mutableStateOf(0) }

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
                    onTextLayout = {textLayoutResult = it},
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
                                    val lineMiddleY = (layout.getLineTop(i) + layout.getLineBottom(i)) / 2f
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
/*
                                val y = size.height / 2f
                                var xStart = 0f
                                var xEnd = 0f

                                when (tag.isStrike) {
                                    true -> {
                                        xStart = 0f
                                        xEnd = size.width * strikeProgress
                                    }

                                    false -> {
                                        xStart = (1f - strikeProgress) * size.width
                                        xEnd = size.width
                                    }
                                }
                                drawLine(
                                    color = color,
                                    start = Offset(x = xStart, y),
                                    end = Offset(x = xEnd, y),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                                */
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
                                    val lineMiddleY = (layout.getLineTop(i) + layout.getLineBottom(i)) / 2f
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
                                /*
                                                                val y = size.height / 2f
                                                                var xStart = 0f
                                                                var xEnd = 0f

                                                                when (tag.isStrike) {
                                                                    true -> {
                                                                        xStart = 0f
                                                                        xEnd = size.width * strikeProgress
                                                                    }

                                                                    false -> {
                                                                        xStart = (1f - strikeProgress) * size.width
                                                                        xEnd = size.width
                                                                    }
                                                                }
                                                                drawLine(
                                                                    color = color,
                                                                    start = Offset(x = xStart, y),
                                                                    end = Offset(x = xEnd, y),
                                                                    strokeWidth = 2.dp.toPx(),
                                                                    cap = StrokeCap.Round
                                                                )
                                                                */
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
                                    val lineMiddleY = (layout.getLineTop(i) + layout.getLineBottom(i)) / 2f
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
                                /*
                                                                val y = size.height / 2f
                                                                var xStart = 0f
                                                                var xEnd = 0f

                                                                when (tag.isStrike) {
                                                                    true -> {
                                                                        xStart = 0f
                                                                        xEnd = size.width * strikeProgress
                                                                    }

                                                                    false -> {
                                                                        xStart = (1f - strikeProgress) * size.width
                                                                        xEnd = size.width
                                                                    }
                                                                }
                                                                drawLine(
                                                                    color = color,
                                                                    start = Offset(x = xStart, y),
                                                                    end = Offset(x = xEnd, y),
                                                                    strokeWidth = 2.dp.toPx(),
                                                                    cap = StrokeCap.Round
                                                                )
                                                                */
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

/*
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

 */