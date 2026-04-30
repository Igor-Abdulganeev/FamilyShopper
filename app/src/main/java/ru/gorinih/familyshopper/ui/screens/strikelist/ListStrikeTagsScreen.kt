package ru.gorinih.familyshopper.ui.screens.strikelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.theme.ListDarkBlue
import ru.gorinih.familyshopper.ui.theme.ListDarkGreen
import ru.gorinih.familyshopper.ui.theme.ListDarkRed
import ru.gorinih.familyshopper.ui.theme.ListDarkYellow
import ru.gorinih.familyshopper.ui.theme.ListLightBlue
import ru.gorinih.familyshopper.ui.theme.ListLightGreen
import ru.gorinih.familyshopper.ui.theme.ListLightRed
import ru.gorinih.familyshopper.ui.theme.ListLightYellow
import ru.gorinih.familyshopper.ui.views.AnimatedAgsl
import ru.gorinih.familyshopper.ui.views.ErrorDialog
import ru.gorinih.familyshopper.ui.views.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.views.TagsList
import ru.gorinih.familyshopper.ui.widget.WidgetScope
import ru.gorinih.familyshopper.ui.widget.notifyWidgetAboutChanged

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

@Composable
fun ListStrikeTagsScreen(
    listUuid: String = "",
    route: (String) -> Unit = {},
    backPressed: () -> Unit,
    navigationActions: (NavigationActions) -> Unit,
    viewModel: ListStrikeTagsViewModel = koinViewModel(
        parameters = { parametersOf(listUuid) }
    )
) {
    val handleExit = {
        viewModel.updateIfChanged()
        backPressed()
    }

    val context = LocalContext.current.applicationContext
    val state = viewModel.shoppedList
    var isClicked by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        handleExit()
    }
    DisposableEffect(Unit) {
        navigationActions(NavigationActions(onNavigationClick = handleExit))

        onDispose {
            WidgetScope.scope.launch {
                notifyWidgetAboutChanged(
                    context,
                    state.listId,
                    false
                )
            }

            navigationActions(NavigationActions(onNavigationClick = { backPressed() }))
        }
    }

    val brush =
        Brush.linearGradient(
            colors = if (isSystemInDarkTheme()) {
                when (state.listLegend) {// цвета статика темная тема
                    TypeLegendList.ALL -> listOf(
                        MaterialTheme.colorScheme.background,
                        ListDarkGreen.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.ADD -> listOf(
                         MaterialTheme.colorScheme.background,
                        ListDarkBlue.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.VIEW -> listOf(
                        MaterialTheme.colorScheme.background,
                        ListDarkYellow.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.PRIVATE -> listOf(
                         MaterialTheme.colorScheme.background,
                        ListDarkRed.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.NOTHING -> listOf(
                        MaterialTheme.colorScheme.background,
                    )
                }
            } else { // цвета статика светлая тема
                when (state.listLegend) {
                    TypeLegendList.ALL -> listOf(
                        MaterialTheme.colorScheme.background,
                         ListLightGreen.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.ADD -> listOf(
                         MaterialTheme.colorScheme.background,
                        ListLightBlue.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.VIEW -> listOf(
                         MaterialTheme.colorScheme.background,
                        ListLightYellow.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.PRIVATE -> listOf(
                        MaterialTheme.colorScheme.background,
                        ListLightRed.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.background,
                    )

                    TypeLegendList.NOTHING -> listOf(
                        MaterialTheme.colorScheme.background,
                    )
                }
            }
        )

    val colors = if (isSystemInDarkTheme()) {
        when (state.listLegend) { //темный градиент
            TypeLegendList.ALL -> listOf(
                MaterialTheme.colorScheme.surface,
                ListDarkGreen,
            )

            TypeLegendList.ADD -> listOf(
                MaterialTheme.colorScheme.surface,
                ListDarkBlue,
            )

            TypeLegendList.VIEW -> listOf(
                ListDarkYellow,
                MaterialTheme.colorScheme.surface
            )

            TypeLegendList.PRIVATE -> listOf(
                MaterialTheme.colorScheme.surface,
                ListDarkRed,
            )

            TypeLegendList.NOTHING -> listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surface,
            )
        }
    } else { // светлый градиент
        when (state.listLegend) {
            TypeLegendList.ALL -> listOf(
                MaterialTheme.colorScheme.onSecondary,
                ListLightGreen,
            )

            TypeLegendList.ADD -> listOf(
                MaterialTheme.colorScheme.onSecondary,
                ListLightBlue,
             )

            TypeLegendList.VIEW -> listOf(
                MaterialTheme.colorScheme.onSecondary,
                ListLightYellow,
            )

            TypeLegendList.PRIVATE -> listOf(
                MaterialTheme.colorScheme.onSecondary,
                ListLightRed,
            )

            TypeLegendList.NOTHING -> listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSecondary,
            )
        }
    }

    AnimatedAgsl(
        modifier = Modifier.fillMaxSize(),
        brush = brush,
        startedColor = colors.first(),
        endedColor = colors.last(),
        isAnimate = state.background
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.isEditable) {
                    IconButton(
                        enabled = !isClicked,
                        onClick = {
                            isClicked = true
                            route(listUuid)
                        },
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null)
                    }
                } else IconButton(
                    enabled = false,
                    onClick = { }
                ) {
                    Spacer(Modifier.width(48.dp))
                }
                Text(text = state.listName)
                if (state.isUpdate && (state.listLegend == TypeLegendList.ALL || state.listLegend == TypeLegendList.ADD
                    || state.isEditable)
                ) {
                    Box(Modifier.width(48.dp).align(Alignment.CenterVertically)){
                        if (state.hiddenUpdate) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp).size(36.dp)
                            )
                        }
                        IconButton(
                            enabled = !state.hiddenUpdate,
                            onClick = { viewModel.updateList() },
                        ) {
                            Icon(Icons.Default.Repeat, contentDescription = null)
                        }
                    }
                } else Spacer(Modifier.width(48.dp))
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            TagsList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                list = state.tagNames,
                typeList = state.typeList,
                onClick = { name ->
                    viewModel.updateTag(name, ActionTag.STRIKE)
                },
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
                onFocusRegister = { _, _ -> },
                onFocusUnRegister = { },
                onClearCurrentField = {}

            )
        }
    }
    if (state.loading) ProgressLoadingOverlay()
    if (state.warning.isWarning) ErrorDialog(
        errorText = when (state.warning.resourceWarning) {
            0 -> state.warning.textWarning
            else -> stringResource(state.warning.resourceWarning)
        }
    ) { viewModel.onDismiss() }

}

