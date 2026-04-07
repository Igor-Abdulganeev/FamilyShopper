package ru.gorinih.familyshopper.ui.screens.dictionary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.models.UiDictionary
import ru.gorinih.familyshopper.ui.screens.ErrorDialog
import ru.gorinih.familyshopper.ui.screens.ProgressLoadingOverlay
import ru.gorinih.familyshopper.ui.screens.RoundedTextField

/**
 * Редактирование тэгов
 */

@Composable
fun EditDictionariesScreen(
    modifier: Modifier = Modifier,
    viewModel: EditDictionariesViewModel = koinViewModel()
) {
    val state by viewModel.dictionaryState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = 0) { state.list.size }
    var addedTag by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        if (state.canSync) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.label_sync_tags))
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        viewModel.refreshDictionaries()
                    }
                ) {
                    Icon(Icons.Default.Repeat, contentDescription = null)
                }
            }
        }
        RoundedTextField(
            value = addedTag,
            onValueChange = { text ->
                addedTag = text
                /*
                                    currentPosition = if (text.length == 1) {
                                        getAlphabet.indexOf(text.first().uppercaseChar())
                                    } else -1
                */
            },
            placeholder = stringResource(R.string.label_enter_new_tag),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (addedTag.isNotBlank()) {
                            viewModel.addTag(addedTag)
                            addedTag = ""
                        }
                    },
                ) { Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null) }
            }
        )
        when(state.list.count()) {
            0 -> EmptyAlphabet(modifier)
            else -> AlphabetTabs(
                alphabet = state.list,
                pagerState = pagerState,
            ) { tagName ->
                viewModel.deleteTag(tagName)
            }
        }
    }

    when {
        state.isLoading -> {
            ProgressLoadingOverlay()
        }
        state.error != null -> {
            ErrorDialog(state.error ?: "") {
                viewModel.errorDismiss()
            }
        }
    }
}

@Composable
fun EmptyAlphabet(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
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
        Text("Отсутствуют данные", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun AlphabetTabs(
    alphabet: List<UiDictionary>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onTagClick: (String) -> Unit,
) {
    val customPageSize = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int,
        ): Int = (availableSpace - 2 * pageSpacing) / 2
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            pageSize = customPageSize,
            contentPadding = PaddingValues(horizontal = 8.dp), // Показывает 2 вкладки
            userScrollEnabled = true
        ) { page ->
            val tab = alphabet[page]
            WordListTab(
                data = tab,
                onTagClick
            )
            VerticalDivider(thickness = 1.dp)
        }
    }
}

@Composable
private fun WordListTab(
    data: UiDictionary,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок вкладки — буква алфавита
        Text(
            text = data.tagId,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(thickness = 1.dp)
        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(data.tagNames, key = { item -> data.tagId to item.tagName }) { item ->
                Row(
                    modifier = Modifier.animateItem(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = {
                            onClick(item.tagName)
                        }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                    Text(
                        text = item.tagName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
