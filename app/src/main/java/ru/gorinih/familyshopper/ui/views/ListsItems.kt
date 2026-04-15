package ru.gorinih.familyshopper.ui.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.models.TypeListTags
import ru.gorinih.familyshopper.ui.models.isEdit
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

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
    onEditComment: (String, String) -> Unit
) {
    val stateTagsColumn = rememberLazyListState()
    LaunchedEffect(list.size) {
        if (list.isNotEmpty())
            stateTagsColumn.animateScrollToItem(0)
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
                    onClick = { onClick(item.tagName) },
                    onDelete = { onDelete(item.tagName) },
                    onEditComment = { comment -> onEditComment(item.tagName, comment) }
                )
            }
        }
    }
}

@Composable
fun DictionaryList(
    list: List<String>,
    modifier: Modifier,
    onClick: (String) -> Unit
) {
    val stateDictionaryColumn = rememberLazyListState()
    var empty by remember { mutableStateOf(list.isEmpty()) }
    val modifierList = modifier
        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(4.dp)
        )
        .border(
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.inverseSurface
            ),
            shape = RoundedCornerShape(4.dp)
        )
    when (empty) {
        false -> LazyColumn(
            state = stateDictionaryColumn,
            modifier = modifierList
        ) {
            items(list, key = { id -> id }) { item ->
                Text(
                    text = item,
                    style = TextStyle(lineHeight = 16.sp),
                    modifier = Modifier
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                        .clickable(
                            onClick = { onClick(item) }
                        )
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        true -> {
            Row(modifier = modifierList) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.label_empty_list),
                    style = TextStyle(lineHeight = 16.sp),
                    modifier = Modifier
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TagItem(
    tag: UiShoppingItem,
    type: TypeListTags = TypeListTags.EDIT,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEditComment: (String) -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (tag.isStrike) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    val color = MaterialTheme.colorScheme.secondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = if (type.isEdit()) 0.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (type) {
            TypeListTags.EDIT -> {
                IconButton(
                    onClick = {
                        onDelete()
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(0.3f)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                }
                Text(
                    text = tag.tagName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            onClick = {
                                onClick()
                            }
                        )
                        .padding(start = 4.dp, end = 4.dp)
                        .drawWithContent {
                            drawContent()
                            if (progress > 0f) {
                                val y = size.height / 2f
                                var xStart = 0f
                                var xEnd = 0f

                                when (tag.isStrike) {
                                    true -> {
                                        xStart = 0f
                                        xEnd = size.width * progress
                                    }

                                    false -> {
                                        xStart = (1f - progress) * size.width
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
                            }
                        }
                )
                BracketTextField(
                    comment = tag.tagComment,
//            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    onChange = { comment ->
                        onEditComment(comment)
                    },
                    modifier = Modifier.weight(0.7f)
                )
            }

            TypeListTags.STRIKE -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tag.tagName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = {
                                    onClick()
                                }
                            )
                            .padding(start = 16.dp, end = 4.dp)
                            .drawWithContent {
                                drawContent()
                                if (progress > 0f) {
                                    val y = size.height / 2f
                                    var xStart = 0f
                                    var xEnd = 0f

                                    when (tag.isStrike) {
                                        true -> {
                                            xStart = 0f
                                            xEnd = size.width * progress
                                        }

                                        false -> {
                                            xStart = (1f - progress) * size.width
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
                                }
                            }
                    )
                    if (tag.tagComment.isNotBlank()) {
                        Text(
                            text = "(${tag.tagComment})",
                            modifier = Modifier
                                .weight(0.7f)
                        )
                    }
                }
            }

            TypeListTags.VIEW -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tag.tagName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 4.dp)
                            .drawWithContent {
                                drawContent()
                                if (progress > 0f) {
                                    val y = size.height / 2f
                                    var xStart = 0f
                                    var xEnd = 0f

                                    when (tag.isStrike) {
                                        true -> {
                                            xStart = 0f
                                            xEnd = size.width * progress
                                        }

                                        false -> {
                                            xStart = (1f - progress) * size.width
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
                                }
                            }
                    )
                    if (tag.tagComment.isNotBlank()) {
                        Text(
                            text = "(${tag.tagComment})",
                            modifier = Modifier
                        )
                    }
                }
            }
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
                onEditComment = {}
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
                onEditComment = {}
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
                onEditComment = {}
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
                onEditComment = {}
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
                onEditComment = {}
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
                onEditComment = {}
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
                onEditComment = {}
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
                onEditComment = {}
            )

        }
    }
}