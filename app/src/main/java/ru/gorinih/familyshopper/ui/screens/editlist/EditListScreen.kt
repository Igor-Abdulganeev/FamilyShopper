package ru.gorinih.familyshopper.ui.screens.editlist

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.navigation.ScreenLayoutType
import ru.gorinih.familyshopper.navigation.rememberScreenConfiguration
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.TypeListTags
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListUser
import ru.gorinih.familyshopper.ui.views.ChipPanelSelectTypeList
import ru.gorinih.familyshopper.ui.views.ErrorDialog
import ru.gorinih.familyshopper.ui.views.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.views.RoundedTextField
import ru.gorinih.familyshopper.ui.views.TagsList
import ru.gorinih.familyshopper.ui.views.shadow
import ru.gorinih.familyshopper.ui.widget.WidgetLists
import ru.gorinih.familyshopper.ui.widget.notifyWidgetAboutChanged

/**
 * Created by Igor Abdulganeev on 07.04.2026
 */

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListScreen(
    listUuid: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: EditListViewModel = koinViewModel(
        parameters = { parametersOf(listUuid) }
    )
) {
    val state = viewModel.shoppedList
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    var addedTag by rememberSaveable { mutableStateOf("") }
    val keyboardManager = LocalFocusManager.current
    val screen = rememberScreenConfiguration()
    val named = stringResource(state.listNameId, state.date)
    var isOpenUserSheet by rememberSaveable { mutableStateOf(false) }
    var isDictionary by rememberSaveable { mutableStateOf(false) }

    var helpWords by remember { mutableStateOf(listOf<String>()) }
    var isHelpWords by remember { mutableStateOf(false) }

    BackHandler(enabled = false) { }
    LaunchedEffect(Unit) {
        if (state.listName.isEmpty() && listUuid.isEmpty()) viewModel.updateListName(named)
    }

    fun addNewTag(item: String = "", comment: String = "") {
        if (addedTag.isNotBlank() || item.isNotBlank()) {
            viewModel.updateTag(item.takeIf { it.isNotBlank() } ?: addedTag, ActionTag.ADD, comment)
            addedTag = ""
            helpWords = emptyList()
        }
    }

    val tintIcon = when (isDictionary) {
        false -> LocalContentColor.current
        true -> MaterialTheme.colorScheme.primary
    }
    val typeColorTexts = listOf(
        stringResource(R.string.label_icon_all),
        stringResource(R.string.label_icon_add),
        stringResource(R.string.label_icon_view),
        stringResource(R.string.label_icon_private),
    )
    when (screen) {
        ScreenLayoutType.SINGLE_PANE -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 32.dp, max = 38.dp)
            ) {

                typeColorTexts.forEachIndexed { index, text ->
                    val colorTypeTextButton = when {
                        index + 1 == state.listLegend.listId && state.isOwner -> MaterialTheme.colorScheme.onSurface
                        index + 1 == state.listLegend.listId && !state.isOwner -> MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )

                        state.isOwner -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                    SegmentedButton(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        selected = index + 1 == state.listLegend.listId,
                        enabled = state.isOwner,
                        onClick = { viewModel.updateLegend(TypeLegendList.entries.first { it.listId == index + 1 }) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = typeColorTexts.count()
                        ),
                        label = {
                            BasicText(
                                text = text,
                                color = { colorTypeTextButton },
                                maxLines = 1,
                                autoSize = TextAutoSize.StepBased(),
                            )
                        },
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = GlassCircleImageHolder.getColor(index + 1)
                                .copy(alpha = 0.5f),
                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledActiveContainerColor = GlassCircleImageHolder.getColor(index + 1)
                                .copy(alpha = 0.5f),
                            disabledInactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.4f
                            ),
                        ),
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                    modifier = Modifier.weight(1f),
                    onClearCurrentField = { viewModel.clearCurrentField() }
                )
                AnimatedVisibility(state.allUsersUuid.isNotEmpty() && state.listLegend != TypeLegendList.PRIVATE && !state.isLocalJob) {
                    AssistChip(
                        modifier = Modifier
                            .weight(0.35f)
                            .padding(start = 4.dp, end = 4.dp),
                        onClick = {
                            viewModel.clearCurrentField()
                            keyboardManager.clearFocus()
                            isOpenUserSheet = true
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.label_icon_select_words,
                                    state.usersUuid.count()
                                ),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        },
                    )
                }
            }

            if (isOpenUserSheet) {
                UsersSheet(
                    listUsers = state.allUsersUuid,
                    modifier = Modifier.navigationBarsPadding(),
                    multiplier = 0.2f,
                    onDismiss = { isOpenUserSheet = false },
                    onChecked = { viewModel.selectUser(it) }
                )
            }

            ExposedDropdownMenuBox(
                expanded = isHelpWords,
                onExpandedChange = { isHelpWords = it }
            ) {
                RoundedTextField(
                    value = addedTag,
                    onValueChange = { name ->
                        addedTag = name
                        helpWords = when {
                            name.isNotBlank() -> state.listAllTags.filter { it.contains(name) && (it !in state.tagNames.map { tags -> tags.tagName }) }
                                .take(5)

                            else -> emptyList()
                        }
                        isHelpWords = helpWords.isNotEmpty()
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .fillMaxWidth(),
                    label = stringResource(R.string.label_list_add_tag),
                    leadingIcon = if (state.listAllTags.isNotEmpty()) {
                        {
                            IconButton(
                                onClick = {
                                    keyboardManager.clearFocus()
                                    isDictionary = true
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.List,
                                    contentDescription = null,
                                    tint = tintIcon
                                )
                            }
                        }
                    } else null,
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
                        }
                    },
                    action = {
                        addNewTag()
                    },
                    onClearCurrentField = { viewModel.clearCurrentField() }
                )
                if (helpWords.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = isHelpWords,
                        onDismissRequest = { isHelpWords = false }
                    ) {
                        helpWords.forEach { word ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = word,
                                        modifier = Modifier.padding(
                                            horizontal = 4.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                },
                                onClick = {
                                    addNewTag(item = word) // выбор сразу вносит в список
                                    //addedTag = word // выбор добавляет в строку ввода
                                    isHelpWords = false
                                }
                            )
                        }
                    }
                }
            }

            if (isDictionary) {
                DictionarySheet(
                    list = state.listAllTags,
                    onDismiss = { isDictionary = false },
                    onClick = { text -> addNewTag(text) },
                    modifier = Modifier.navigationBarsPadding(),
                )
            }

            TagsList(
                list = state.tagNames,
                typeList = TypeListTags.EDIT,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 4.dp, bottom = 4.dp),
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
                },
                onFocusRegister = { id, lambda ->
                    viewModel.registerField(id, lambda)
                },
                onFocusUnRegister = { id ->
                    viewModel.unregisterField(id)
                },
                onClearCurrentField = {
                    viewModel.clearCurrentField()
                }
            )

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
                    viewModel.clearCurrentField()
                    viewModel.saveList()
                }
            ) {
                Text(stringResource(R.string.button_save_text))
            }
        }

        ScreenLayoutType.TWO_PANE -> Row(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 4.dp, end = 4.dp)
        ) {
            ChipPanelSelectTypeList(
                legend = state.listLegend.listId,
                isOwner = state.isOwner,
                textChips = typeColorTexts,
                onClick = { index ->
                    viewModel.updateLegend(TypeLegendList.entries.first { it.listId == index + 1 })
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Min),
                showText = false
            )
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
                        label = stringResource(R.string.label_list_name_short),
                        action = {
                            keyboardManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(0.3f)
                            .padding(end = 4.dp),
                        onClearCurrentField = { viewModel.clearCurrentField() }
                    )
                    ExposedDropdownMenuBox(
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 4.dp),
                        expanded = isHelpWords,
                        onExpandedChange = { isHelpWords = it }
                    ) {
                        RoundedTextField(
                            value = addedTag,
                            onValueChange = { name ->
                                addedTag = name
                                helpWords = when {
                                    name.isNotBlank() -> state.listAllTags.filter { it.contains(name) && (it !in state.tagNames.map { tags -> tags.tagName }) }
                                        .take(5)

                                    else -> emptyList()
                                }
                                isHelpWords = helpWords.isNotEmpty()
                            },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                            label = stringResource(R.string.label_list_add_tag),
                            leadingIcon = if (state.listAllTags.isNotEmpty()) {
                                {
                                    IconButton(
                                        onClick = {
                                            keyboardManager.clearFocus()
                                            isDictionary = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.List,
                                            contentDescription = null,
                                            tint = tintIcon
                                        )
                                    }
                                }
                            } else null,
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
                                }
                            },
                            action = {
                                addNewTag()
                            },
                            onClearCurrentField = { viewModel.clearCurrentField() }
                        )
                        if (helpWords.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = isHelpWords,
                                onDismissRequest = { isHelpWords = false }
                            ) {
                                helpWords.forEach { word ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = word,
                                                modifier = Modifier.padding(
                                                    horizontal = 4.dp,
                                                    vertical = 4.dp
                                                )
                                            )
                                        },
                                        onClick = {
                                            addNewTag(item = word)
                                            isHelpWords = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.2f)
                    ) {
                        AnimatedVisibility(state.allUsersUuid.isNotEmpty() && state.listLegend != TypeLegendList.PRIVATE && !state.isLocalJob) {
                            AssistChip(
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 4.dp),
                                onClick = {
                                    viewModel.clearCurrentField()
                                    keyboardManager.clearFocus()
                                    isOpenUserSheet = true
                                },
                                label = {
                                    Text(
                                        text = stringResource(
                                            R.string.label_icon_select_words,
                                            state.usersUuid.count()
                                        ),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                },
                            )
                        }
                        Button(
                            modifier = Modifier
                                // .weight(0.2f)
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
                                viewModel.clearCurrentField()
                                viewModel.saveList()
                            }
                        ) {
                            Text("Сохранить")
                        }
                    }
                }

                if (isDictionary) {
                    DictionarySheet(
                        list = state.listAllTags,
                        onDismiss = { isDictionary = false },
                        onClick = { text -> addNewTag(text) },
                        modifier = Modifier.navigationBarsPadding(),
                    )
                }

                TagsList(
                    list = state.tagNames,
                    typeList = TypeListTags.EDIT,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    onClick = { name -> viewModel.updateTag(name, ActionTag.STRIKE) },
                    onDelete = { name -> viewModel.updateTag(name, ActionTag.DELETE) },
                    onEditComment = { name, comment ->
                        viewModel.updateTag(
                            name,
                            ActionTag.COMMENT,
                            comment
                        )
                    },
                    onFocusRegister = { id, lambda ->
                        viewModel.registerField(id, lambda)
                    },
                    onFocusUnRegister = { id ->
                        viewModel.unregisterField(id)
                    },
                    onClearCurrentField = {
                        viewModel.clearCurrentField()
                    }
                )

                if (isOpenUserSheet) {
                    UsersSheet(
                        listUsers = state.allUsersUuid,
                        modifier = Modifier.navigationBarsPadding(),
                        multiplier = 0.4f,
                        onDismiss = { isOpenUserSheet = false },
                        onChecked = { viewModel.selectUser(it) }
                    )
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
        when (state.warning.resourceWarning) {
            0 -> ErrorDialog(errorText = state.warning.textWarning) { viewModel.onDismiss() }
            else -> ErrorDialog(
                errorText = "${stringResource(state.warning.resourceWarning)}\n${
                    stringResource(
                        R.string.warning_local_changed
                    )
                }"
            ) { viewModel.onDismissSaved() }
        }
    }
    if (state.saved) {
        scope.launch {
            notifyWidgetAboutChanged(
                context,
                state.listUuid,
                false
            )
        }
        onBack()
    }
}

@Composable
fun UserRow(
    userName: String,
    isSelected: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colorScheme.primary
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    onClick = { onCheckedChange() }
                )
        ) {
            Text(
                text = userName,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersSheet(
    listUsers: List<UiListUser>,
    onDismiss: () -> Unit,
    onChecked: (String) -> Unit,
    multiplier: Float,
    modifier: Modifier = Modifier,
) {
    val usersSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val stateLazyList = rememberLazyListState()
    val isDark = isSystemInDarkTheme()
    LaunchedEffect(Unit) {
        if (listUsers.isNotEmpty()) stateLazyList.scrollToItem(0)
    }

    val heightSheet = LocalConfiguration.current.screenHeightDp.dp * multiplier

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = usersSheetState,
        modifier = modifier,
        dragHandle = {
            BottomSheetDefaults.DragHandle(width = 16.dp)
        },
        scrimColor = if (isDark) MaterialTheme.colorScheme.background.copy(alpha = 0.6f) else BottomSheetDefaults.ScrimColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = heightSheet)
                .padding(top = 2.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                )
        ) {
            LazyColumn(
                state = stateLazyList,
                modifier = Modifier.clipToBounds(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    listUsers,
                    key = { list -> list.userUuid },
                    contentType = { "user_row" }
                ) { item ->
                    UserRow(
                        userName = item.userName,
                        isSelected = item.isSelected,
                        onCheckedChange = { onChecked(item.userUuid) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionarySheet(
    list: List<String>,
    onDismiss: () -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dictionaryState = rememberModalBottomSheetState()
    val stateLazyList = rememberLazyListState()
    val isDark = isSystemInDarkTheme()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = dictionaryState,
        modifier = modifier,
        dragHandle = {
            BottomSheetDefaults.DragHandle(width = 16.dp)
        },
        scrimColor = if (isDark) MaterialTheme.colorScheme.background.copy(alpha = 0.6f) else BottomSheetDefaults.ScrimColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                )
        ) {
            LazyColumn(
                state = stateLazyList,
                modifier = Modifier.clipToBounds(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(list, key = { text -> text }) { item ->
                    Text(
                        text = item,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                onClick = { onClick(item) }
                            )
                    )
                }
            }
        }
    }

}