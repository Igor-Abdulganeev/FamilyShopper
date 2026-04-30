package ru.gorinih.familyshopper.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.widget.FamilyWidgetProvider.Companion.EXTRA_LIST_ID
import ru.gorinih.familyshopper.ui.widget.FamilyWidgetProvider.Companion.EXTRA_LIST_VERSION
import ru.gorinih.familyshopper.ui.widget.FamilyWidgetProvider.Companion.EXTRA_TAG_ID
import ru.gorinih.familyshopper.ui.widget.FamilyWidgetProvider.Companion.EXTRA_TAG_STRIKE
import ru.gorinih.familyshopper.ui.widget.models.WidgetItem
import ru.gorinih.familyshopper.ui.widget.models.WidgetTagItem
import ru.gorinih.familyshopper.ui.widget.models.toListWidgetItem

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

class FamilyWidgetFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val database: DatabaseRepository by inject()
    private val pref: StorageRepository by inject()
    private val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

    private var tagItems = listOf<WidgetTagItem>()
/*
    private var isEdit = false
    private var version = 0
*/


    override fun getCount(): Int = tagItems.count()

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun hasStableIds(): Boolean = true

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun onDataSetChanged() {
        WidgetScope.scope.launch {
            val listUuid = pref.getWidget(appWidgetId)
            val data = try {
                if (listUuid.isNotBlank()) {
                    withTimeout(3000) {
                        withContext(Dispatchers.IO) {
                            val list = database.takeList(listUuid)
                                .toListWidgetItem()
                            val tags = list.tags.sortedBy { it.tagName }
/*
                            val user = pref.getClientUUID()
                            isEdit =
                                (user == list.listOwner && list.listLegend != TypeLegendList.VIEW.listId) || (list.listLegend == TypeLegendList.ALL.listId || list.listLegend == TypeLegendList.ADD.listId)
                            version = list.listVersion
*/
                            list.copy(tags = tags)
                        }
                    }
                } else WidgetItem()
            } catch (ex: Throwable) {
                Log.e("GINES", "Error ${ex.localizedMessage}")
                WidgetItem()
            }
            val manager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.header_text, data.listName)
            val headerColor = when (data.listLegend) {
                2 -> context.getColor(R.color.widget_header_legend_add)
                3 -> context.getColor(R.color.widget_header_legend_view)
                4 -> context.getColor(R.color.widget_header_legend_private)
                else -> context.getColor(R.color.widget_header_legend_all)
            }
            views.setInt(R.id.header_layout, "setBackgroundColor", headerColor)

            manager.partiallyUpdateAppWidget(appWidgetId, views)

            if ( tagItems != data.tags) {
                tagItems = data.tags
            @Suppress("DEPRECATION")
             manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
            }

        }
    }

    @Suppress("DEPRECATION")
    override fun getViewAt(position: Int): RemoteViews {
        val currentItems = tagItems
        if (position < 0 || position >= currentItems.size) return RemoteViews(
            context.packageName,
            R.layout.widget_item_tag
        )
        Log.e("GINES","tagItems = ${currentItems.count()} / $position")

        val tag = currentItems[position]
        val rv = RemoteViews(context.packageName, R.layout.widget_item_tag)
/*
        val listUuid = pref.getWidget(appWidgetId)
        val intent = Intent().apply {
            putExtra(EXTRA_TAG_ID, tag.tagName)
            putExtra(EXTRA_TAG_STRIKE, tag.isStrike)
            putExtra(EXTRA_LIST_ID, listUuid)
            putExtra(EXTRA_LIST_VERSION, version)
        }

        rv.setOnClickFillInIntent(R.id.item_checkbox, intent)
        rv.setOnClickFillInIntent(R.id.item_text, intent)
*/

/*
        when(isEdit){
            true -> {
                rv.setOnClickFillInIntent(R.id.item_checkbox, intent)
                rv.setOnClickFillInIntent(R.id.item_text, intent)
            }
            false -> {
                rv.setOnClickFillInIntent(R.id.item_checkbox, Intent())
                rv.setOnClickFillInIntent(R.id.item_text, Intent())
            }
        }

        rv.setViewVisibility(R.id.item_checkbox, if (isEdit) View.VISIBLE else View.GONE)
        rv.setBoolean(R.id.item_checkbox, "setEnabled", isEdit)
        rv.setBoolean(R.id.item_checkbox, "setClickable", isEdit)
        rv.setBoolean(R.id.item_text, "setEnabled", isEdit)
        rv.setBoolean(R.id.item_text, "setClickable", isEdit)
*/

        when (tag.isStrike) {
            true -> {
                rv.setTextColor(R.id.item_text, "#FF6B7280".toColorInt())
                rv.setTextViewText(R.id.item_text, Html.fromHtml("<s>${tag.tagName}</s>"))
                rv.setImageViewResource(R.id.item_checkbox, R.drawable.ic_checkbox_checked_24)
            }

            false -> {
                rv.setTextColor(R.id.item_text, "#FF1C1C1E".toColorInt()) // Темный если активно
                rv.setTextViewText(R.id.item_text, tag.tagName)
                rv.setImageViewResource(R.id.item_checkbox, R.drawable.ic_checkbox_unchecked_24)
            }
        }
        return rv
    }
}