package ru.gorinih.familyshopper.ui.screens.editlist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.navigation.ScreenLayoutType
import ru.gorinih.familyshopper.navigation.rememberScreenConfiguration
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeShoppedList
import ru.gorinih.familyshopper.ui.views.DictionaryList
import ru.gorinih.familyshopper.ui.views.ErrorDialog
import ru.gorinih.familyshopper.ui.views.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.views.RoundedTextField
import ru.gorinih.familyshopper.ui.views.TagsList
import ru.gorinih.familyshopper.ui.views.shadow

/**
 * Created by Igor Abdulganeev on 07.04.2026
 */

@Composable
fun EditListScreen(
    listUuid: String,
    modifier: Modifier = Modifier,
    router: () -> Unit,
    viewModel: EditListViewModel = koinViewModel(
        parameters = { parametersOf(listUuid) }
    )
) {
    val state = viewModel.shoppedList
    var addedTag by rememberSaveable { mutableStateOf("") }
    val keyboardManager = LocalFocusManager.current
    val screen = rememberScreenConfiguration()
    val named = stringResource(state.listNameId, state.date)
    val stateLazyList = rememberLazyListState()
    BackHandler(enabled = false) { }
    LaunchedEffect(Unit) {
        if (state.listName.isEmpty() && listUuid.isEmpty()) viewModel.updateListName(named)
    }

    fun addNewTag(item: String = "", comment: String = "") {
        if (addedTag.isNotBlank() || item.isNotBlank()) {
            viewModel.updateTag(item.takeIf { it.isNotBlank() } ?: addedTag, ActionTag.ADD, comment)
            addedTag = ""
        }
    }

    val tintIcon = when (state.isDictionary) {
        false -> LocalContentColor.current
        true -> MaterialTheme.colorScheme.primary
    }
    when (screen) {
        ScreenLayoutType.SINGLE_PANE -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp)
        ) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val iconSize = (maxWidth / 12)
                val iconLabelSize = with(LocalDensity.current) { (iconSize / 3).toSp() }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.inverseSurface
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                    ) {
                        Image(
                            painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        viewModel.updateLegend(TypeShoppedList.ALL)
                                    }
                                )
                                .size(iconSize),
                            contentScale = ContentScale.Inside,
                            colorFilter = when {
                                state.listLegend == TypeShoppedList.ALL -> null
                                else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                            }
                        )
                        Text(
                            text = stringResource(R.string.label_icon_all),
                            color = if (state.listLegend == TypeShoppedList.ALL) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                            fontSize = iconLabelSize
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 4.dp, top = 4.dp)
                    ) {
                        Image(
                            painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        viewModel.updateLegend(TypeShoppedList.ADD)
                                    }
                                )
                                .size(iconSize),
                            contentScale = ContentScale.Inside,
                            colorFilter = when {
                                state.listLegend == TypeShoppedList.ADD -> null
                                else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                            }
                        )
                        Text(
                            text = stringResource(R.string.label_icon_add),
                            color = if (state.listLegend == TypeShoppedList.ADD) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = iconLabelSize
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 4.dp, top = 4.dp)
                    ) {
                        Image(
                            painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        viewModel.updateLegend(TypeShoppedList.VIEW)
                                    }
                                )
                                .size(iconSize),
                            contentScale = ContentScale.Inside,
                            colorFilter = when {
                                state.listLegend == TypeShoppedList.VIEW -> null
                                else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                            }
                        )
                        Text(
                            text = stringResource(R.string.label_icon_view),
                            color = if (state.listLegend == TypeShoppedList.VIEW) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = iconLabelSize
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 4.dp, top = 4.dp)
                    ) {
                        Image(
                            painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(bottom = 2.dp)
                                .clickable(
                                    onClick = {
                                        viewModel.updateLegend(TypeShoppedList.PRIVATE)
                                    }
                                )
                                .size(iconSize),
                            contentScale = ContentScale.Inside,
                            colorFilter = when {
                                state.listLegend == TypeShoppedList.PRIVATE -> null
                                else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                            }
                        )
                        Text(
                            text = stringResource(R.string.label_icon_private),
                            color = if (state.listLegend == TypeShoppedList.PRIVATE) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onSurface,
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
                },
                trailingIcon = {
                    if (state.allUsersUuid.isNotEmpty() && state.listLegend != TypeShoppedList.PRIVATE) {
                        IconButton(
                            onClick = {
                                viewModel.showUsersSelect()
                            },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Icon(Icons.Default.SupervisedUserCircle, contentDescription = null)
                        }
                        Text(
                            text = state.usersUuid.count().toString(),
                            modifier = Modifier.padding(bottom = 16.dp, start = 12.dp),
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }
                }
            )
            AnimatedVisibility(
                visible = state.usersSelect,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 2.dp)
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {

                    LazyColumn(state = stateLazyList) {
                        items(state.allUsersUuid, key = { list -> list.userUuid }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isSelected,
                                    onCheckedChange = { viewModel.selectUser(item.userUuid) }
                                )
                                Text(
                                    text = item.userName,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }

                }
            }
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
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null
                            )
                        }
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
                TagsList(
                    list = state.tagNames,
                    isDeleteTag = true,
                    modifier = Modifier.weight(1f),
                    onClick = { name -> viewModel.updateTag(name, ActionTag.STRIKE) },
                    onDelete = { name ->
                        viewModel.updateTag(
                            addedTagName = name,
                            action = ActionTag.DELETE
                        )
                    },
                    onEditComment = { name, comment ->
                        viewModel.updateTag(
                            name,
                            ActionTag.COMMENT,
                            comment
                        )
                    }
                )
                AnimatedVisibility(
                    visible = state.isDictionary,
                ) {
                    DictionaryList(
                        list = state.listAllTags,
                        modifier = Modifier
                            .fillMaxWidth(0.35f),
                        onClick = { addNewTag(it) }
                    )
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .shadow(
                        colorLight = MaterialTheme.colorScheme.primary,
                        shadowRadius = 4.dp,
                        borderRadius = 16.dp,
                        offsetYLight = 2.dp,
                        offsetXLight = 3.dp,
                        alphaShadowLight = 0.3f
                    ),
                onClick = {
                    viewModel.saveList()
                }
            ) {
                Text("Сохранить")
            }
        }

        ScreenLayoutType.TWO_PANE -> Row(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 4.dp, end = 4.dp)
        ) {
            BoxWithConstraints(Modifier.fillMaxHeight()) {
                val iconSize = (maxHeight / 10)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    viewModel.updateLegend(TypeShoppedList.ALL)
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = when {
                            state.listLegend == TypeShoppedList.ALL -> null
                            else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                        }
                    )
                    Image(
                        painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    viewModel.updateLegend(TypeShoppedList.ADD)
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = when {
                            state.listLegend == TypeShoppedList.ADD -> null
                            else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                        }
                    )
                    Image(
                        painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    viewModel.updateLegend(TypeShoppedList.VIEW)
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = when {
                            state.listLegend == TypeShoppedList.VIEW -> null
                            else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                        }
                    )
                    Image(
                        painter = GlassCircleImageHolder.getImage(state.listLegend.listId),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(bottom = 2.dp)
                            .clickable(
                                onClick = {
                                    viewModel.updateLegend(TypeShoppedList.PRIVATE)
                                }
                            )
                            .size(iconSize),
                        contentScale = ContentScale.Inside,
                        colorFilter = when {
                            state.listLegend == TypeShoppedList.PRIVATE -> null
                            else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                        }
                    )
                }
            }
            Column(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoundedTextField(
                        value = state.listName,
                        onValueChange = { name ->
                            viewModel.updateListName(name)
                        },
                        label = stringResource(R.string.label_list_name),
                        action = {
                            keyboardManager.clearFocus()
                        },
                        modifier = Modifier.weight(0.4f)
                    )
                    Button(
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(horizontal = 4.dp)
                            .shadow(
                                colorLight = MaterialTheme.colorScheme.primary,
                                shadowRadius = 4.dp,
                                borderRadius = 16.dp,
                                offsetYLight = 2.dp,
                                offsetXLight = 3.dp,
                                alphaShadowLight = 0.3f
                            ),
                        onClick = {
                            viewModel.saveList()
                        }
                    ) {
                        Text("Сохранить")
                    }
                    RoundedTextField(
                        value = addedTag,
                        onValueChange = { name ->
                            addedTag = name
                        },
                        // label = stringResource(R.string.label_list_add_tag),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        addNewTag()
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null
                                    )
                                }
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
                        },
                        modifier = Modifier.weight(0.4f)
                    )
                }
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
                    TagsList(
                        list = state.tagNames,
                        isDeleteTag = true,
                        modifier = Modifier.weight(1f),
                        onClick = { name -> viewModel.updateTag(name, ActionTag.STRIKE) },
                        onDelete = { name -> viewModel.updateTag(name, ActionTag.DELETE) },
                        onEditComment = { name, comment ->
                            viewModel.updateTag(
                                name,
                                ActionTag.COMMENT,
                                comment
                            )
                        }
                    )
                    AnimatedVisibility(
                        visible = state.isDictionary,
                    ) {
                        DictionaryList(
                            list = state.listAllTags,
                            modifier = Modifier
                                .fillMaxWidth(0.35f),
                            onClick = { addNewTag(it) }
                        )
                    }
                }
            }
        }
    }
    AnimatedVisibility(
        visible = state.loading,
        enter = fadeIn(animationSpec = tween(durationMillis = 200)),
        exit = fadeOut(animationSpec = tween(durationMillis = 200))
    ) {
        ProgressLoadingOverlay()
    }
    if (state.warning.isWarning) {
        ErrorDialog(
            errorText = if (state.warning.resourceWarning != 0) stringResource(state.warning.resourceWarning)
            else state.warning.textWarning
        ) {
            viewModel.onDismiss()
        }
    }
    if (state.saved) router()

}
