package ru.gorinih.familyshopper.ui.screens.lists

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListObject
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme
import ru.gorinih.familyshopper.ui.theme.ListDarkBlue
import ru.gorinih.familyshopper.ui.theme.ListDarkGreen
import ru.gorinih.familyshopper.ui.theme.ListDarkRed
import ru.gorinih.familyshopper.ui.theme.ListDarkYellow
import ru.gorinih.familyshopper.ui.theme.ListLightBlue
import ru.gorinih.familyshopper.ui.theme.ListLightGreen
import ru.gorinih.familyshopper.ui.theme.ListLightRed
import ru.gorinih.familyshopper.ui.theme.ListLightYellow
import ru.gorinih.familyshopper.ui.views.ErrorDialog
import ru.gorinih.familyshopper.ui.views.MaterialGroupBox
import ru.gorinih.familyshopper.ui.views.ProgressLoadingOverlay

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Composable
fun ListEntityScreen(
    router: (String) -> Unit,
    addList: () -> Unit,
    viewModel: ListEntityVewModel = koinViewModel()
) {

    val state = viewModel.listsState
    val stateLazy = rememberLazyListState()

    BackHandler(enabled = false) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { addList() }
            ) {
                Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = stringResource(R.string.label_sync_tags))
                IconButton(
                    onClick = { viewModel.updateList() }
                ) {
                    Icon(Icons.Default.Repeat, contentDescription = null)
                }
            }
        }
        LazyColumn(state = stateLazy) {
            items(state.lists, key = { item -> item.listId }) { item ->
                val painter = GlassCircleImageHolder.getImage(item.listLegend)
                CardListItem(item, painter) {
                    router(item.listId)
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

    if (state.error != null) {
        ErrorDialog(errorText = state.error) {
            viewModel.onDismiss()
        }
    }
}

@Composable
fun CardListItem(
    item: UiListObject,
    painter: Painter? = null,
    onClick: () -> Unit,
) {
    val title = item.listName.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.label_empty_list_name)
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
/*
        val tintBackground = when(item.listLegend) {
            1 -> Color.Green
            2 -> Color.Blue
            3 -> Color.Yellow
            else -> Color.Red
        }.run { copy(alpha = 0.1f) }
*/
        val brush = Brush.horizontalGradient(
            colors = if (isDark) {
                when (item.listLegend) {
                    1 -> listOf(
                        ListDarkGreen,
                        MaterialTheme.colorScheme.primary,
                    )

                    2 -> listOf(
                        ListDarkBlue,
                        MaterialTheme.colorScheme.primary,
                    )

                    3 -> listOf(
                        ListDarkYellow,
                        MaterialTheme.colorScheme.primary,
                    )

                    else -> listOf(
                        ListDarkRed,
                        MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                when (item.listLegend) {
                    1 -> listOf(
                        ListLightGreen,
                        MaterialTheme.colorScheme.primary,
                    )

                    2 -> listOf(
                        ListLightBlue,
                        MaterialTheme.colorScheme.primary,
                    )

                    3 -> listOf(
                        ListLightYellow,
                        MaterialTheme.colorScheme.primary,
                    )

                    else -> listOf(
                        ListLightRed,
                        MaterialTheme.colorScheme.primary,
                    )
                }
            },
            startX = 0.0f,
            endX = 550f
        )

        MaterialGroupBox(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
            onClick = { onClick() },
            color = MaterialTheme.colorScheme.primary, //tintBackground
            brush = brush
        ) {
            Column() {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            drawStyle = Stroke(
                                width = 4f,
                                join = StrokeJoin.Round
                            )
                        ),
                        color = if (isDark) Color.Black else Color.White
                    )
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (painter != null) {
                        Image(painter, contentDescription = null, Modifier.size(20.dp))
                    }
                    Text(
                        text = "${item.countTags} / ${item.countStrikes}",
                        modifier = Modifier.padding(start = 32.dp)
                    )
                    Text(
                        text = item.listDatetime,
                        style = TextStyle(
                            fontSize = 12.sp,
                            baselineShift = BaselineShift.Subscript
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )

                }

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMat() {
    FamilyShopperTheme {
        Column(Modifier.padding(top = 32.dp)) {
            CardListItem(
                UiListObject(
                    listId = "sdgsgsd",
                    listVersion = 1,
                    listName = "Тестовый зеленый",
                    listLegend = 1,
                    listOwner = "asdas0,",
                    listTo = emptyList(),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 10,
                    countStrikes = 2,
                ),
                GlassCircleImageHolder.getImage(1)
            ) {}
            CardListItem(
                UiListObject(
                    listId = "sdgsfggsd",
                    listVersion = 1,
                    listName = "Тестовый синий",
                    listLegend = 2,
                    listOwner = "asdas0,",
                    listTo = emptyList(),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 5,
                    countStrikes = 0
                ),
                GlassCircleImageHolder.getImage(2)
            ) {}
            CardListItem(
                UiListObject(
                    listId = "sdgsgsd",
                    listVersion = 1,
                    listName = "Тестовый желтый",
                    listLegend = 3,
                    listOwner = "asdas0,",
                    listTo = emptyList(),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 4,
                    countStrikes = 4
                ),
                GlassCircleImageHolder.getImage(3)

            ) {}
            CardListItem(
                UiListObject(
                    listId = "sdgsgsd",
                    listVersion = 1,
                    listName = "Тестовый красный",
                    listLegend = 4,
                    listOwner = "asdas0,",
                    listTo = emptyList(),
                    listDatetime = "10.04.2026 12:09",
                    countTags = 0,
                    countStrikes = 0
                ),
                GlassCircleImageHolder.getImage(4)

            ) {}
        }
    }
}