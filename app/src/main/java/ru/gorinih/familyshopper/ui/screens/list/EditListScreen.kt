package ru.gorinih.familyshopper.ui.screens.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.ShoppingItem
import ru.gorinih.familyshopper.ui.models.TypeShoppedList
import ru.gorinih.familyshopper.ui.screens.RoundedTextField

/**
 * Created by Igor Abdulganeev on 07.04.2026
 */

@Composable
fun EditListScreen(
    listUuid: String,
    modifier: Modifier = Modifier,
    viewModel: EditListViewModel = koinViewModel(
        parameters = { parametersOf(listUuid) }
    )
) {
    val state = viewModel.shoppedList
    var addedTag by rememberSaveable { mutableStateOf("") }
    val stateTagsColumn = rememberLazyListState()
    val stateDictionaryColumn = rememberLazyListState()
    val keyboardManager = LocalFocusManager.current

    fun addNewTag(item: String = "") {
        if (addedTag.isNotBlank() || item.isNotBlank()) {
            viewModel.updateTag(item.takeIf { it.isNotBlank() } ?: addedTag, ActionTag.ADD)
            addedTag = ""
        }
    }

    val tintIcon = when (state.isDictionary) {
        false -> LocalContentColor.current
        true -> MaterialTheme.colorScheme.primary
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val iconSize = (maxWidth / 12)
            val iconLabelSize = with(LocalDensity.current) { (iconSize / 3).toSp() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.type_all),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    if (state.listLegend != 1) viewModel.updateLegend(
                                        TypeShoppedList.ALL
                                    )
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                setToSaturation(
                                    when {
                                        state.listLegend == TypeShoppedList.valueOf(TypeShoppedList.ALL.name).listId -> 1f
                                        else -> 0f
                                    }
                                )
                            }
                        )
                    )
                    Text(
                        text = stringResource(R.string.label_icon_all),
                        color = if (state.listLegend == 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                        fontSize = iconLabelSize
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.type_add),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    if (state.listLegend != 2) viewModel.updateLegend(
                                        TypeShoppedList.ADD
                                    )
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                setToSaturation(
                                    when {
                                        state.listLegend == TypeShoppedList.valueOf(TypeShoppedList.ADD.name).listId -> 1f
                                        else -> 0f
                                    }
                                )
                            }
                        )
                    )
                    Text(
                        text = stringResource(R.string.label_icon_add),
                        color = if (state.listLegend == 2) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                        fontSize = iconLabelSize)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.type_view),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    if (state.listLegend != 3) viewModel.updateLegend(
                                        TypeShoppedList.VIEW
                                    )
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                setToSaturation(
                                    when {
                                        state.listLegend == TypeShoppedList.valueOf(TypeShoppedList.VIEW.name).listId -> 1f
                                        else -> 0f
                                    }
                                )
                            }
                        )
                    )
                    Text(
                        text = stringResource(R.string.label_icon_view),
                        color = if (state.listLegend == 3) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                        fontSize = iconLabelSize)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.type_private),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(bottom = 2.dp)
                            .clickable(
                                onClick = {
                                    if (state.listLegend != 4) viewModel.updateLegend(
                                        TypeShoppedList.PRIVATE
                                    )
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                setToSaturation(
                                    when {
                                        state.listLegend == TypeShoppedList.valueOf(TypeShoppedList.PRIVATE.name).listId -> 1f
                                        else -> 0f
                                    }
                                )
                            }
                        )
                    )
                    Text(
                        text = stringResource(R.string.label_icon_private),
                        color = if (state.listLegend == 4) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                        fontSize = iconLabelSize
                    )
                }
            }
        }
        RoundedTextField(
            value = state.listName,
            onValueChange = { name ->
                viewModel.updateListName(name)
            },
            label = stringResource(R.string.label_list_name),
            action = {
                keyboardManager.clearFocus()
            }
        )
        RoundedTextField(
            value = addedTag,
            onValueChange = { name ->
                addedTag = name
            },
            label = stringResource(R.string.label_list_add_tag),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            addNewTag()
                        },
                    ) { Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null) }
                    IconButton(
                        onClick = {
                            viewModel.showDictionaries()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            tint = tintIcon
                        )
                    }
                }
            },
            action = {
                addNewTag()
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 4.dp, bottom = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.inverseSurface
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            if (state.tagNames.isEmpty()) {
                Text(
                    stringResource(R.string.label_empty_list),
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    state = stateTagsColumn,
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.tagNames, key = { list -> list.tagName }) { item ->
                        TagItem(
                            item,
                            { viewModel.updateTag(item.tagName, ActionTag.STRIKE) },
                            { viewModel.updateTag(item.tagName, ActionTag.DELETE) }
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = state.isDictionary,
            ) {
                LazyColumn(
                    state = stateDictionaryColumn,
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.inverseSurface
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    items(state.listAllTags, key = { item -> item }) { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(start = 4.dp, end = 16.dp)
                                .clickable(
                                    onClick = { addNewTag(item) }
                                )
                        )
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            onClick = {}
        ) {
            Text("Сохранить")
        }
    }
}

@Composable
fun TagItem(
    tag: ShoppingItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (tag.isStrike) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    val color = MaterialTheme.colorScheme.secondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onDelete()
            },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(Icons.Default.Clear, contentDescription = null)
        }
        Text(
            text = tag.tagName,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(
                    onClick = {
                        onClick()
                    }
                )
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
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewEditListScreen() {
    Column(Modifier.padding(top = 64.dp, start = 8.dp, end = 8.dp)) {
        EditListScreen("")
    }

}