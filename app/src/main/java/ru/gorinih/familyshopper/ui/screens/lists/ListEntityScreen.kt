package ru.gorinih.familyshopper.ui.screens.lists

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ru.gorinih.familyshopper.ui.screens.ErrorDialog
import ru.gorinih.familyshopper.ui.screens.GlassCircleImage
import ru.gorinih.familyshopper.ui.screens.MaterialGroupBox
import ru.gorinih.familyshopper.ui.screens.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListObject
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Composable
fun ListEntityScreen(
    router: (String) -> Unit,
    viewModel: ListEntityVewModel = koinViewModel()
) {

    val state = viewModel.listsState
    val stateLazy = rememberLazyListState()

    BackHandler(enabled = false) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
        Row(
           modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(text=stringResource(R.string.label_sync_tags))
            IconButton(
                onClick = { viewModel.updateList() }
            ) {
                Icon(Icons.Default.Repeat, contentDescription = null)
            }
        }
        LazyColumn(state = stateLazy) {
            items(state.lists, key = { item -> item.listId }) { item ->
                val painter =
                    if (state.typedList.containsKey(item.listLegend)) state.typedList[item.listLegend] else null
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

        MaterialGroupBox(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
            onClick = { onClick() },
            title = item.listName.takeIf { it.isNotBlank() }
                ?: stringResource(R.string.label_empty_list_name),
            color = MaterialTheme.colorScheme.primary, //tintBackground

        ) {
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
                    text ="${item.countTags} / ${item.countStrikes}",
                    modifier = Modifier.padding(start = 32.dp)
                )
                Text(
                    text = item.listDatetime,
                    style = TextStyle(fontSize = 12.sp, baselineShift = BaselineShift.Subscript),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.End
                )

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMat() {
    val typedList = mapOf<Int, Painter>(
        1 to GlassCircleImage(Color.Green),
        2 to GlassCircleImage(Color.Blue),
        3 to GlassCircleImage(Color.Yellow),
        4 to GlassCircleImage(Color.Red),
    )

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
                typedList[1]
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
                typedList[2]
            ){}
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
                typedList[3]

            ){}
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
                typedList[4]

            ){}
        }
    }
}