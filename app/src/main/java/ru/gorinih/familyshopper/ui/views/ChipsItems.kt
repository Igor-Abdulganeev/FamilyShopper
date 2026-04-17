package ru.gorinih.familyshopper.ui.views

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */
enum class AuthorFilter {
    MY,
    OTHERS,
    ALL,
}

enum class SortType { DATE, TYPE, NOTHING }

enum class SortDirection { UP, DOWN, NOTHING }

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