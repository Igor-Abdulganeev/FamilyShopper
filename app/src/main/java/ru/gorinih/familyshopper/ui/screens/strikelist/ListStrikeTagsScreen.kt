package ru.gorinih.familyshopper.ui.screens.strikelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Repeat
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
        viewModel.saveChanged()
        backPressed()
    }

    val state = viewModel.shoppedList
    var isClicked by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        handleExit()
    }
    DisposableEffect(Unit) {
        navigationActions(NavigationActions(onNavigationClick = handleExit))

        onDispose {
            navigationActions(NavigationActions(onNavigationClick = { backPressed() }))
        }
    }

    val brush =
        Brush.linearGradient(
            colors =  if(isSystemInDarkTheme()) {
                when (state.listLegend) {
                    TypeLegendList.ALL -> listOf(
                        ListDarkGreen,
                        MaterialTheme.colorScheme.onSecondary,
                        ListDarkGreen
                    )

                    TypeLegendList.ADD -> listOf(
                        ListDarkBlue,
                        MaterialTheme.colorScheme.onSecondary,
                        ListDarkBlue
                    )

                    TypeLegendList.VIEW -> listOf(
                        ListDarkYellow,
                        MaterialTheme.colorScheme.onSecondary,
                        ListDarkYellow
                    )

                    TypeLegendList.PRIVATE -> listOf(
                        ListDarkRed,
                        MaterialTheme.colorScheme.onSecondary,
                        ListDarkRed
                    )
                }
            } else {
                when (state.listLegend) {
                    TypeLegendList.ALL -> listOf(
                        ListLightGreen,
                        MaterialTheme.colorScheme.onSecondary,
                        ListLightGreen
                    )

                    TypeLegendList.ADD -> listOf(
                        ListLightBlue,
                        MaterialTheme.colorScheme.onSecondary,
                        ListLightBlue
                    )

                    TypeLegendList.VIEW -> listOf(
                        ListLightYellow,
                        MaterialTheme.colorScheme.onSecondary,
                        ListLightYellow
                    )

                    TypeLegendList.PRIVATE -> listOf(
                        ListLightRed,
                        MaterialTheme.colorScheme.onSecondary,
                        ListLightRed
                    )
                }
            }
    )

    val colors = if(isSystemInDarkTheme()) {
        when(state.listLegend) {
            TypeLegendList.ALL -> listOf(
                ListDarkGreen,
                MaterialTheme.colorScheme.surface
            )

            TypeLegendList.ADD -> listOf(
                ListDarkBlue,
                MaterialTheme.colorScheme.primaryContainer
            )

            TypeLegendList.VIEW -> listOf(
                ListDarkYellow,
                MaterialTheme.colorScheme.surface
            )

            TypeLegendList.PRIVATE -> listOf(
                ListDarkRed,
                MaterialTheme.colorScheme.surface
            )
        }
    } else {
        when(state.listLegend) {
            TypeLegendList.ALL -> listOf(
                ListLightGreen,
                MaterialTheme.colorScheme.onSecondary
            )

            TypeLegendList.ADD -> listOf(
                ListLightBlue,
                MaterialTheme.colorScheme.onSecondary
            )

            TypeLegendList.VIEW -> listOf(
                ListLightYellow,
                MaterialTheme.colorScheme.onSecondary
            )

            TypeLegendList.PRIVATE -> listOf(
                ListLightRed,
                MaterialTheme.colorScheme.onSecondary
            )
        }
    }


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
                    }
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
            if (state.listLegend == TypeLegendList.ALL || state.listLegend == TypeLegendList.ADD
                || state.isEditable
            ) {
                IconButton(
                    onClick = { viewModel.updateList() }
                ) {
                    Icon(Icons.Default.Repeat, contentDescription = null)
                }
            } else Spacer(Modifier.width(48.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
            AnimatedAgsl(
                modifier = Modifier.weight(1f),
                brush = brush,
                startedColor = colors.first(),
                endedColor = colors.last(),
                isAnimate = state.background
            ) {
                TagsList(
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
                    }
                )
            }
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

