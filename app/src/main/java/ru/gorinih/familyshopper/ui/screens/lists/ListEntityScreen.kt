package ru.gorinih.familyshopper.ui.screens.lists

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.navigation.NavigationKey
import ru.gorinih.familyshopper.navigation.ScreenLayoutType
import ru.gorinih.familyshopper.navigation.rememberScreenConfiguration
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.models.TypeLegendList
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
import ru.gorinih.familyshopper.ui.views.ChipPanel
import ru.gorinih.familyshopper.ui.views.ErrorDialog
import ru.gorinih.familyshopper.ui.views.MaterialGroupBox
import ru.gorinih.familyshopper.ui.views.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.views.QueryDialog
import ru.gorinih.familyshopper.ui.views.shadow
import kotlin.math.roundToInt

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Composable
fun ListEntityScreen(
    router: (NavigationKey) -> Unit,
    addList: () -> Unit,
    backClick: () -> Unit,
    navigationActions: (NavigationActions) -> Unit,
    viewModel: ListEntityVewModel = koinViewModel()
) {

    val state = viewModel.listsState
    val stateLazy = rememberLazyListState()
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var isClicked by remember { mutableStateOf(false) }
    val screen = rememberScreenConfiguration()

    LaunchedEffect(Unit) {
        navigationActions(NavigationActions(onNavigationClick = { backClick() }))
    }

    LaunchedEffect(state.lists) {
        if (state.lists.isNotEmpty()) stateLazy.animateScrollToItem(0)
    }

    BackHandler(enabled = false) {}

    when (screen) {
        ScreenLayoutType.SINGLE_PANE -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    IconButton(
                        enabled = !isClicked,
                        onClick = {
                            isClicked = true
                            addList()
                        },
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null)
                    }
                    ChipPanel(
                        modifier = Modifier.weight(1f),
                        startSelectedAuthorFilter = state.filterRule,
                        sortDirection = state.sortDirection,
                        sortType = state.sortType,
                        onSelectAuthorFilter = { filterType -> viewModel.filter(filterType) },
                        onSorted = { type, direction -> viewModel.sorter(type, direction) }
                    )

                    if (state.isUpdate) {
                        IconButton(
                            onClick = { viewModel.updateList() },
                            modifier = Modifier.weight(0.3f)
                        ) {
                            Icon(Icons.Default.Repeat, contentDescription = null)
                        }
                    }
                }

                if (state.lists.isEmpty()) {
                    if (state.filterRule != AuthorFilter.OTHERS)
                        AssistChip(
                            modifier = Modifier.padding(16.dp),
                            onClick = { addList() },
                            label = { Text(text = stringResource(R.string.label_empty_list_comand_text)) },
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    else
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                        {
                            Text(
                                text = stringResource(R.string.label_empty_list),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                } else {
                    LazyColumn(state = stateLazy) {
                        items(state.lists, key = { item -> item.listId }) { item ->
                            val painter = GlassCircleImageHolder.getImage(item.listLegend.listId)
                            CardListItem(
                                item = item,
                                painter = painter,
                                onClick = {
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime > 500L) {
                                        lastClickTime = currentTime
                                        router(NavigationKey.ListStrikeTagsScreen(listUuid = item.listId))
                                    }
                                },
                                onDelete = {
                                    viewModel.startDeleteList(item.listId)
                                },
                                onLocalDelete = {
                                    viewModel.startLocalDeleteList(item.listId)
                                },
                                onEdit = {
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime > 500L) {
                                        lastClickTime = currentTime
                                        router(NavigationKey.EditListScreen(listUuid = item.listId))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        ScreenLayoutType.TWO_PANE -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(0.1f)) {
                    IconButton(
                        enabled = !isClicked,
                        onClick = {
                            isClicked = true
                            addList()
                        },
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null)
                    }
                    if (state.isUpdate) {
                        IconButton(
                            onClick = { viewModel.updateList() },
                            modifier = Modifier.weight(0.3f)
                        ) {
                            Icon(Icons.Default.Repeat, contentDescription = null)
                        }
                    }
                }

                if (state.lists.isEmpty()) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.filterRule != AuthorFilter.OTHERS)
                            AssistChip(
                                onClick = { addList() },
                                label = { Text(text = stringResource(R.string.label_empty_list_comand_text)) },
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        else
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )
                            )
                            {
                                Text(
                                    text = stringResource(R.string.label_empty_list),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                    }
                } else {
                    LazyColumn(state = stateLazy, modifier = Modifier.weight(1f)) {
                        items(state.lists, key = { item -> item.listId }) { item ->
                            val painter = GlassCircleImageHolder.getImage(item.listLegend.listId)
                            CardListItem(
                                item = item,
                                painter = painter,
                                onClick = {
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime > 500L) {
                                        lastClickTime = currentTime
                                        router(NavigationKey.ListStrikeTagsScreen(listUuid = item.listId))
                                    }
                                },
                                onDelete = {
                                    viewModel.startDeleteList(item.listId)
                                },
                                onLocalDelete = {
                                    viewModel.startLocalDeleteList(item.listId)
                                },
                                onEdit = {
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime > 500L) {
                                        lastClickTime = currentTime
                                        router(NavigationKey.EditListScreen(listUuid = item.listId))
                                    }
                                }
                            )
                        }
                    }
                }
                ChipPanel(
                    modifier = Modifier.weight(0.25f),
                    startSelectedAuthorFilter = state.filterRule,
                    sortDirection = state.sortDirection,
                    sortType = state.sortType,
                    onSelectAuthorFilter = { filterType -> viewModel.filter(filterType) },
                    onSorted = { type, direction -> viewModel.sorter(type, direction) }
                )
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
            errorText = if (state.warning.resourceWarning != 0) "${stringResource(state.warning.resourceWarning)}\n${
                stringResource(
                    R.string.warning_local_changed
                )
            }"
            else state.warning.textWarning
        ) {
            viewModel.onDismiss()
        }
    }
    if (state.deleting.isDelete) {
        QueryDialog(
            text = stringResource(
                state.deleting.queryText,
                state.lists.firstOrNull { it.listId == state.deleting.deletedId }?.listName ?: ""
            ),
            onDone = { viewModel.deleteList(state.deleting.deletedId) },
            onCancel = { viewModel.stopDeleteList() }
        )
    }
    if (state.localDeleting.isDelete) {
        QueryDialog(
            text = stringResource(
                state.localDeleting.queryText,
                state.lists.firstOrNull { it.listId == state.localDeleting.deletedId }?.listName ?: ""
            ),
            onDone = { viewModel.localDeleteList(state.localDeleting.deletedId) },
            onCancel = { viewModel.stopDeleteList() }
        )
    }
}

@Composable
fun CardListItem(
    item: UiListObject,
    painter: Painter? = null,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onLocalDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val title = item.listName.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.label_empty_list_name)
    val isDark = isSystemInDarkTheme()
    val density = LocalDensity.current

    val widthSwipe = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    } * 0.2f // сдвинем на размер...
    val anchors = DraggableAnchors {
        SwipedAnchor.START at -widthSwipe
        SwipedAnchor.MEDIAN at 0f
        SwipedAnchor.END at widthSwipe
    }
    val stateSwipe = remember {
        AnchoredDraggableState(
            initialValue = SwipedAnchor.MEDIAN,
            anchors = anchors
        )
    }
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = stateSwipe,
        positionalThreshold = { distance -> distance * 0.5f },
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
    )
    val progress =
        (item.countStrikes.toFloat() / (item.countTags.takeIf { it > 0 } ?: 1))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Row(
            Modifier
                .matchParentSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    if (item.isDelete) onDelete()
                    else onLocalDelete()
                },
            ) {
                if (item.isDelete)
                    Icon(Icons.Default.Delete, contentDescription = null)
                else
                    Icon(Icons.Default.DeleteOutline, contentDescription = null)
            }
            IconButton(
                onClick = { onEdit() },
                enabled = item.isEdit
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(stateSwipe.requireOffset().roundToInt(), 0) }
                .anchoredDraggable(
                    state = stateSwipe,
                    reverseDirection = false,
                    orientation = Orientation.Horizontal,
                    flingBehavior = flingBehavior,
                    interactionSource = null,
                    overscrollEffect = null,
                )
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
                onClick = { onClick() },
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
                        // цветовая точка
                        if (painter != null) {
                            Image(
                                painter, contentDescription = null,
                                Modifier
                                    .size(20.dp)
                                    .weight(0.2f)
                            )
                        }
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
                            text = item.listDatetime,
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

                    // автор и назначеные
                    val ownerName = if (item.userName.isNotBlank()) stringResource(
                        R.string.label_owner_name,
                        item.userName
                    )
                    else ""
                    val otherNames = if (item.listTo.isNotEmpty()) stringResource(
                        R.string.label_other_names,
                        item.listTo.joinToString(", ") { it.userName })
                    else ""
                    val names = when {
                        item.userName.isNotBlank() && item.listTo.isNotEmpty() -> "$ownerName      $otherNames"
                        item.userName.isNotBlank() -> ownerName
                        else -> otherNames
                    }
                    Text(
                        text = names,
                        modifier = Modifier.padding(start = 32.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis
                    )

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
                        text = stringResource(R.string.label_results_count, item.countStrikes.toString(), item.countTags.toString()),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PreviewMat() {
    FamilyShopperTheme {
        Column(
            Modifier
                .padding(top = 32.dp)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            CardListItem(
                UiListObject(
                    listId = "sdgsgsd",
                    listVersion = 1,
                    listName = "Тестовый зеленый",
                    listLegend = TypeLegendList.ALL,
                    listOwner = "asdas0,",
                    listTo = listOf(
                        UiListUser(
                            userUuid = "fkgjfkddfklgk",
                            userName = "Марья",
                            isSelected = false
                        ),
                        UiListUser(
                            userUuid = "fkgjfkddfkккlgk",
                            userName = "Олег",
                            isSelected = false
                        ),
                    ),
                    listDatetime = "10.04.2026",
                    countTags = 10,
                    countStrikes = 2,
                    userName = "Иван",
                    listDatetimeValue = 0,
                ),
                GlassCircleImageHolder.getImage(1),
                onClick = {},
                onDelete = {},
                onEdit = {},
                onLocalDelete = {}
            )
            CardListItem(
                UiListObject(
                    listId = "sdgsfggsd",
                    listVersion = 1,
                    listName = "Тестовый синий",
                    listLegend = TypeLegendList.ADD,
                    listOwner = "asdas0,",
                    listTo = emptyList(),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 5,
                    countStrikes = 3,
                    userName = "",
                    listDatetimeValue = 0
                ),
                GlassCircleImageHolder.getImage(2),
                onClick = {},
                onDelete = {},
                onEdit = {},
                onLocalDelete = {}
            )
            CardListItem(
                UiListObject(
                    listId = "sdgsgsd",
                    listVersion = 1,
                    listName = "Тестовый желтый",
                    listLegend = TypeLegendList.VIEW,
                    listOwner = "asdas0,",
                    listTo = listOf(
                        UiListUser(
                            userUuid = "fkgjfkddfklgk",
                            userName = "Марья",
                            isSelected = false
                        )
                    ),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 4,
                    countStrikes = 4,
                    userName = "Олег",
                    listDatetimeValue = 0
                ),
                GlassCircleImageHolder.getImage(3),
                onClick = {},
                onDelete = {},
                onEdit = {},
                onLocalDelete = {}
            )
            CardListItem(
                UiListObject(
                    listId = "sdgsgsd",
                    listVersion = 1,
                    listName = "Тестовый красный бла бла бла опять красный урря",
                    listLegend = TypeLegendList.PRIVATE,
                    listOwner = "asdas0,",
                    listTo = listOf(
                        UiListUser(
                            userUuid = "fkgjfkddfklgk",
                            userName = "",
                            isSelected = true
                        )
                    ),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 0,
                    countStrikes = 0,
                    userName = "Игорь",
                    listDatetimeValue = 0
                ),
                GlassCircleImageHolder.getImage(4),
                onClick = {},
                onDelete = {},
                onEdit = {},
                onLocalDelete = {}
            )
        }
    }
}

enum class SwipedAnchor {
    START,
    MEDIAN,
    END
}