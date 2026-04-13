package ru.gorinih.familyshopper.ui.screens.strikelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.screens.ErrorDialog
import ru.gorinih.familyshopper.ui.screens.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.screens.TagsList

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

@Composable
fun ListStrikeTagsScreen(
    listUuid: String = "",
    route: (String) -> Unit = {},
    backPressed: ()-> Unit,
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
    BackHandler(enabled = true) {
        handleExit()
    }
    DisposableEffect(Unit) {
        navigationActions(NavigationActions(onNavigationClick = handleExit))

        onDispose {
            navigationActions(NavigationActions(onNavigationClick = {backPressed()}))
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            if (state.isEditable) {
                IconButton(
                    onClick = { route(listUuid) }
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null)
                }
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
            TagsList(
                list = state.tagNames,
                isDeleteTag = false,
                modifier = Modifier.weight(1f),
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
    if (state.loading) ProgressLoadingOverlay()
    if (state.warning.isWarning) ErrorDialog(
        errorText = when (state.warning.resourceWarning) {
            0 -> state.warning.textWarning
            else -> stringResource(state.warning.resourceWarning)
        }
    ) { viewModel.dismissWarning() }
}

