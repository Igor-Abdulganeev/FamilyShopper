package ru.gorinih.familyshopper.ui.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_EDIT
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_FORCE_UPDATE
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_LIST
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.ui.widget.models.WidgetItem
import ru.gorinih.familyshopper.ui.widget.models.WidgetTagItem
import ru.gorinih.familyshopper.ui.widget.models.toListWidgetItem

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetLists : GlanceAppWidget(), KoinComponent {
    private val database: DatabaseRepository by inject()

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = try {
            manager.getAppWidgetId(id)
        } catch (_: IllegalArgumentException) {
            AppWidgetManager.INVALID_APPWIDGET_ID
        }
        val listIdKey = stringPreferencesKey("${WIDGET_LIST}_$appWidgetId")
        val listEdit = booleanPreferencesKey("${WIDGET_EDIT}_$appWidgetId")
        val listForceUpdate = longPreferencesKey("${WIDGET_FORCE_UPDATE}_$appWidgetId")
        val emptyText = context.getString(R.string.widget_warning_empty_data)
        val intent = Intent(context, WidgetConfigurationActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val listUuid = prefs[listIdKey] ?: emptyText
                val isEdit = prefs[listEdit] ?: false
                val isUpdate = prefs[listForceUpdate]
                val list = produceState(initialValue = WidgetItem(), listUuid, isUpdate) {
                    value = try {
                        if (listUuid.isNotBlank()) {
                            withTimeout(3000) {
                                withContext(Dispatchers.IO) {
                                    val data = database.takeList(listUuid)
                                        .toListWidgetItem()
                                    val tags = data.tags.sortedBy { it.tagName }
                                    data.copy(tags = tags)
                                }
                            }
                        } else WidgetItem()
                    } catch (_: Throwable) {
                        WidgetItem()
                    }
                }
                WidgetListScreen(intent, list.value, emptyText, isEdit)
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
fun WidgetListScreen(
    intent: Intent,
    dataWidget: WidgetItem,
    emptyText: String,
    isEdit: Boolean,
) {
    val colorHeader = when (dataWidget.listLegend) {
        1 -> ColorProvider(Color(0xFFD7F7E7))
        2 -> ColorProvider(Color(0xFFDDE9FF))
        3 -> ColorProvider(Color(0xFFFFF6DB))
        4 -> ColorProvider(Color(0xFFFFEBEB))
        else -> ColorProvider(Color(0xFF9AA3B2))
    }
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(R.color.widget_background)
            .cornerRadius(8.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().background(colorHeader),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dataWidget.listName,
                style = TextStyle(color = ColorProvider(R.color.widget_text_active)),
                modifier = GlanceModifier.defaultWeight()
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 8.dp)
            )
            Box(
                modifier = GlanceModifier.padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_edit_24),
                    contentDescription = null,
                    modifier = GlanceModifier
                        .clickable(
                            actionStartActivity(intent)
                        )
                )
            }
        }

        Box(
            modifier = GlanceModifier.fillMaxWidth().height(1.dp)
                .background(R.color.widget_text_passive)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {}
        when (dataWidget.tags.isEmpty()) {
            true -> {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emptyText,
                        style = TextStyle(color = ColorProvider(R.color.widget_text_active))
                    )
                }
            }

            false -> {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize().padding(4.dp)
                ) {
                    itemsIndexed(
                        items = dataWidget.tags,
                        itemId = { index, tag ->
                            (dataWidget.listUuid.hashCode()
                                .toLong() * 31 + tag.tagName.hashCode() + index)
                        }) { _, tag ->
                        WidgetItemView(
                            item = tag,
                            listId = dataWidget.listUuid,
                            listVersion = dataWidget.listVersion,
                            isEdit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetItemView(
    item: WidgetTagItem,
    listId: String,
    listVersion: Int,
    isEdit: Boolean,
) {
    val modifier = if (isEdit) {
        GlanceModifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(
                actionRunCallback<StrikeToggleAction>(
                    actionParametersOf(
                        StrikeToggleAction.KEY_LIST_ID to listId,
                        StrikeToggleAction.KEY_TAG_NAME to item.tagName,
                        StrikeToggleAction.KEY_TAG_STRIKE to item.isStrike,
                        StrikeToggleAction.KEY_LIST_VERSION to listVersion,
                        StrikeToggleAction.KEY_ACTION_TYPE to StrikeToggleAction.ACTION_STRIKE
                    )
                )
            )
    } else {
        GlanceModifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEdit) {
            when (item.isStrike) {
                true -> Image(
                    provider = ImageProvider(R.drawable.ic_check_24),
                    contentDescription = null
                )

                false -> Image(
                    provider = ImageProvider(R.drawable.ic_unchecked_24),
                    contentDescription = null
                )
            }
        }
        Text(
            text = item.tagName,
            modifier = GlanceModifier.padding(start = 16.dp),
            maxLines = 1,
            style = TextStyle(
                color = ColorProvider(
                    if (item.isStrike) R.color.widget_text_passive
                    else R.color.widget_text_active
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                textDecoration = if (item.isStrike) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}