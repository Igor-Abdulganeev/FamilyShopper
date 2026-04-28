package ru.gorinih.familyshopper.ui.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_LIST
import ru.gorinih.familyshopper.di.dataStore
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.ui.widget.models.WidgetItem
import ru.gorinih.familyshopper.ui.widget.models.toListWidgetItem

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetLists : GlanceAppWidget(), KoinComponent {
    val listIdKey = stringPreferencesKey(WIDGET_LIST)
    private val database: DatabaseRepository by inject()

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val store = context.dataStore
        val initial = store.data.first()

        provideContent {
            val data by store.data.collectAsState(initial)
            val listUuid = data[listIdKey] ?: ""

            val list = produceState(initialValue = emptyList(), listUuid) {
                value = if (listUuid.isNotBlank()) database.takeList(listUuid)
                    .toListWidgetItem() else emptyList()
            }

            WidgetListScreen(list.value)
        }
    }
}

@Composable
fun WidgetListScreen(
    list: List<WidgetItem>
) {
    when (list.isEmpty()) {
        true -> {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(
                    colorProvider = ColorProvider(MaterialTheme.colorScheme.background)
                ), contentAlignment = Alignment.Center
            ) {
                Text(text = "Список пуст или не выбран")
               // Text(text = stringResource(R.string.widget_warning_empty_data))
            }
        }

        false -> {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize().padding(4.dp).background(
                    colorProvider = ColorProvider(MaterialTheme.colorScheme.background)
                )
            ) {
                items(list) { item ->
                    WidgetItemView(item)
                }
            }
        }
    }
}

@Composable
fun WidgetItemView(
    item: WidgetItem
) {
    Row(modifier = GlanceModifier.fillMaxWidth().padding(2.dp)) {
        Text(
            text = item.tagName,
            modifier = GlanceModifier,
            maxLines = 1,
            style = TextStyle(
                color = ColorProvider(
                    color = if (item.isStrike) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                textDecoration = if (item.isStrike) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}