package ru.gorinih.familyshopper.ui.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_EDIT
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_FORCE_UPDATE
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_LIST
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_VERSION
import ru.gorinih.familyshopper.di.dataStore
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.ui.widget.models.WidgetItem
import ru.gorinih.familyshopper.ui.widget.models.WidgetTagItem
import ru.gorinih.familyshopper.ui.widget.models.toListWidgetItem

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetLists : GlanceAppWidget(), KoinComponent {
    private val database: DatabaseRepository by inject()

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        Log.e("GINES", "PROVIDEGlance")
        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = try {
            manager.getAppWidgetId(id)
        } catch (_: IllegalArgumentException) {
            AppWidgetManager.INVALID_APPWIDGET_ID
        }
        val listIdKey = stringPreferencesKey("${WIDGET_LIST}_$appWidgetId")
        val listVersion = intPreferencesKey("${WIDGET_VERSION}_$appWidgetId")
        val listEdit = booleanPreferencesKey("${WIDGET_EDIT}_$appWidgetId")
        val listForceUpdate = longPreferencesKey("${WIDGET_FORCE_UPDATE}_$appWidgetId")
        var isEdit = false
        var time = 0L

        val store = context.dataStore.data.map { pref ->
            isEdit = pref[listEdit] ?: false
            time = pref[listForceUpdate] ?: 0
            pref[listIdKey] to pref[listVersion]
        }.distinctUntilChanged()

        provideContent {
            Log.e("GINES", "PROVIDEContent")
            GlanceTheme {
                val data by store.collectAsState(initial = null to 0)
                val listUuid = data.first ?: ""
                val version = data.second ?: 0

                val list = produceState(initialValue = WidgetItem(), listUuid, version, time) {
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
                    } catch (ex: Throwable) {
                        Log.e("GINES", "ОШИБКА ${ex.localizedMessage}")
                        WidgetItem()
                    }
                }

                val emptyText = context.getString(R.string.widget_warning_empty_data)
                val intent = Intent(context, WidgetConfigurationActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (listUuid.isNotEmpty()) // иногда кэшированные пустышки мигают, отсеим их
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
    var listTag by remember { mutableStateOf(dataWidget.tags) }
    val colorHeader = when (dataWidget.listLegend) {
        1 -> ColorProvider(Color(0xFFD7F7E7))
        2 -> ColorProvider(Color(0xFFDDE9FF))
        3 -> ColorProvider(Color(0xFFFFF6DB))
        else -> ColorProvider(Color(0xFFFFEBEB))
    }

    LaunchedEffect(dataWidget) {
        Log.d("GINES", "color = ${dataWidget.listLegend}")
        Log.d("GINES", "tags = ${dataWidget.tags.map { it.tagName }}")
        listTag = dataWidget.tags
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
        when (listTag.isEmpty()) {
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
                        listTag,
                        itemId = { index, tag ->
                            (dataWidget.listUuid.hashCode()
                                .toLong() * 31 + tag.tagName.hashCode() + index)
                        }) { index, tag ->
                        WidgetItemView(
                            item = tag,
                            listId = dataWidget.listUuid,
                            listVersion = dataWidget.listVersion,
                            isEdit,
                            onClick = { select ->
                                val mutableList = listTag.toMutableList()
                                val newTag = tag.copy(isStrike = select)
                                mutableList.remove(tag)
                                mutableList.add(index, newTag)
                                listTag = mutableList
                            })
                    }
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun WidgetItemView(
    item: WidgetTagItem,
    listId: String,
    listVersion: Int,
    isEdit: Boolean,
    onClick: (Boolean) -> Unit,
) {
    var edit by remember { mutableStateOf(item.isStrike) }
    val modifier = if (isEdit) {
        GlanceModifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                edit = !edit
                onClick(edit)
                actionRunCallback<StrikeToggleAction>(
                    actionParametersOf(
                        StrikeToggleAction.KEY_LIST_ID to listId,
                        StrikeToggleAction.KEY_TAG_NAME to item.tagName,
                        StrikeToggleAction.KEY_TAG_STRIKE to item.isStrike,
                        StrikeToggleAction.KEY_LIST_VERSION to listVersion,
                        StrikeToggleAction.KEY_ACTION_TYPE to StrikeToggleAction.ACTION_STRIKE
                    )
                )
            }
     } else {
        GlanceModifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    }
    Row(
        modifier = modifier
    ) {
        if (isEdit) {
            CheckBox(
                checked = edit,
                onCheckedChange = {
                    edit = !edit
                    onClick(edit)
                },
                modifier = GlanceModifier.padding(horizontal = 4.dp)
            )
        }
        Text(
            text = item.tagName,
            modifier = GlanceModifier,
            maxLines = 1,
            style = TextStyle(
                color = ColorProvider(
                    if (edit) R.color.widget_text_passive
                    else R.color.widget_text_active
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                textDecoration = if (edit) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}