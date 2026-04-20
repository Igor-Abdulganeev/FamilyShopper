package ru.gorinih.familyshopper.ui.views

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */

@Composable
fun ChipPanelSelectTypeList(
    legend: Int,
    isOwner: Boolean,
    textChips: List<String>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showText: Boolean = false
) {
    FlowColumn(
        modifier = modifier
    ) {
        textChips.forEachIndexed { index, text ->
            val colorTypeTextButton = when {
                index + 1 == legend && isOwner -> MaterialTheme.colorScheme.onSurface
                index + 1 == legend && !isOwner -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                isOwner -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            }
            val select = index + 1 == legend
            FilterChip(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                onClick = { onClick(index) },
                label = {
                    if (showText) {
                        Text(text)
                    }
                },
                leadingIcon = {
                   if (select) Icon(Icons.Default.Done, contentDescription = null)
                },
                enabled = isOwner,
                selected = select,
                colors = FilterChipDefaults.filterChipColors(
                    selectedLabelColor = colorTypeTextButton,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = GlassCircleImageHolder.getColor(index + 1)
                        .copy(alpha = 0.5f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledSelectedContainerColor = GlassCircleImageHolder.getColor(index + 1)
                        .copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = null
            )
        }

    }
}

@Composable
fun ChipPanel(
    modifier: Modifier = Modifier,
    startSelectedAuthorFilter: AuthorFilter = AuthorFilter.ALL,
    sortDirection: SortDirection = SortDirection.NOTHING,
    sortType: SortType = SortType.NOTHING,
    onSelectAuthorFilter: (AuthorFilter) -> Unit,
    onSorted: (SortType, SortDirection) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),

        ) {
        FlowRow(
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (filter in AuthorFilter.entries) {
                FilterChipItem(
                    label = when (filter) {
                        AuthorFilter.MY -> stringResource(R.string.label_filter_chip_author_owner)
                        AuthorFilter.OTHERS -> stringResource(R.string.label_filter_chip_author_other)
                        AuthorFilter.ALL -> stringResource(R.string.label_filter_chip_author_all)
                    },
                    selected = startSelectedAuthorFilter == filter,
                    onClick = {
                        onSelectAuthorFilter(filter)
                    }
                )
            }
        }
        FlowRow(
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SorterChipItem(
                label = stringResource(R.string.label_sort_chip_date),
                icon = if (sortType == SortType.DATE) when (sortDirection) {
                    SortDirection.UP -> Icons.Default.ArrowDropUp
                    SortDirection.DOWN -> Icons.Default.ArrowDropDown
                    SortDirection.NOTHING -> null
                } else null,
                onClick = {
                    val type = when (sortDirection) {
                        SortDirection.UP -> SortDirection.DOWN
                        else -> SortDirection.UP
                    }
                    onSorted(SortType.DATE, type)
                }
            )

            SorterChipItem(
                label = stringResource(R.string.label_sort_chip_type),
                icon = if (sortType == SortType.TYPE) when (sortDirection) {
                    SortDirection.UP -> Icons.Default.ArrowDropUp
                    SortDirection.DOWN -> Icons.Default.ArrowDropDown
                    SortDirection.NOTHING -> null
                } else null,
                onClick = {
                    val type = when (sortDirection) {
                        SortDirection.UP -> SortDirection.DOWN
                        else -> SortDirection.UP
                    }
                    onSorted(SortType.TYPE, type)
                }
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(label) },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
        ),
        border = null
    )
}

@Composable
fun SorterChipItem(
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        trailingIcon = {
            icon?.let {
                Icon(it, contentDescription = null)
            }
        },
        border = null
    )
}

@Preview(uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun PreviewLightFilterChipItem() {
    FamilyShopperTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                FilterChipItem(
                    onClick = {},
                    label = "filter",
                    selected = true
                )
                FilterChipItem(
                    onClick = {},
                    label = "noFilter",
                    selected = false
                )

            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {

            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewNightFilterChipItem() {
    FamilyShopperTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                FilterChipItem(
                    onClick = {},
                    label = "filter",
                    selected = true
                )
                FilterChipItem(
                    onClick = {},
                    label = "noFilter",
                    selected = false
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {

            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PreviewChipPanel() {
    FamilyShopperTheme {
        ChipPanel(
            startSelectedAuthorFilter = AuthorFilter.OTHERS,
            sortDirection = SortDirection.UP,
            sortType = SortType.DATE,
            onSelectAuthorFilter = {},
            onSorted = { _, _ -> }
        )
    }
}

@Composable
fun EditableCommentChip(
    tagId: String,
    comment: String,
    onCommentChange: (String) -> Unit,
    onFocusRegister: (String, () -> Unit) -> Unit,
    onFocusUnRegister: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEdit by remember { mutableStateOf(false) }
    var internalComment by rememberSaveable(comment) { mutableStateOf(comment) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequest = remember { FocusRequester() }


    LaunchedEffect(isEdit) {
        if (isEdit) {
            focusRequest.requestFocus()
            onFocusRegister("comment_$tagId") {
                onCommentChange(internalComment)
                isEdit = false
            }
        } else {
            onFocusUnRegister("comment_$tagId")
        }
    }

    fun saveAndExit() {
        onCommentChange(internalComment)
        isEdit = false
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    AnimatedContent(
        targetState = isEdit,
        transitionSpec = {
            fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
        },
        modifier = modifier
    ) { isEditing ->
        when (isEditing) {
            true -> {
                RoundedTextField(
                    value = internalComment,
                    onValueChange = { str -> internalComment = str },
                    modifier = Modifier
                        .width(180.dp)
                        .focusRequester(focusRequest),
                    action = { saveAndExit() },
                    isSingleLine = false,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                saveAndExit()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            false -> {
                Surface(
                    modifier = Modifier
                        .height(32.dp)
                        .widthIn(min = 40.dp, max = 140.dp)
                        .clickable(
                            onClick = { isEdit = true }
                        ),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    val displayText = comment.ifBlank { "+" }
                    Text(
                        text = displayText,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
