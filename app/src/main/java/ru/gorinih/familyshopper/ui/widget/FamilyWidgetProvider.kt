package ru.gorinih.familyshopper.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

class FamilyWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val pref: StorageRepository by inject()
    private val database: DatabaseRepository by inject()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val widgetView = RemoteViews(context.packageName, R.layout.widget_layout)

        val intent: Intent = Intent(context, FamilyWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = toUri(Intent.URI_INTENT_SCHEME).toUri()
        }

        widgetView.setRemoteAdapter(R.id.widget_list, intent)
        widgetView.setEmptyView(R.id.widget_list, R.id.empty_view)

        widgetView.setInt(R.id.header_layout, "setBackgroundColor", "#FFD1D5DB".toColorInt())
        widgetView.setInt(R.id.widget_list, "setBackgroundColor", "#FFEDEFF3".toColorInt())

        val configIntent = Intent(context, FamilyWidgetProvider::class.java).apply {
            action = ACTION_CLICK_EDIT
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pendingConfigIntent = PendingIntent.getBroadcast(
            context, appWidgetId, configIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        widgetView.setOnClickPendingIntent(R.id.btn_edit, pendingConfigIntent)

        val itemActionIntent = Intent(context, FamilyWidgetProvider::class.java).apply {
            action = ACTION_CLICK_ITEM
        }
        val pendingItemClickIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            itemActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        widgetView.setPendingIntentTemplate(R.id.widget_list, pendingItemClickIntent)

        appWidgetManager.updateAppWidget(appWidgetId, widgetView)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { appWidgetId ->
            pref.removeWidget(appWidgetId)
        }
        super.onDeleted(context, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        super.onReceive(context, intent)
        when (intent?.action) {
            ACTION_CLICK_EDIT -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTimeEdit < PERIODIC_CLICK) return
                lastClickTimeEdit = currentTime

                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

                val configIntent = Intent(context, FamilyConfigWidgetActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(configIntent)
            }

            ACTION_CLICK_ITEM -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTimeItem < PERIODIC_CLICK) return
                lastClickTimeItem = currentTime
                val tagId = intent.getStringExtra(EXTRA_TAG_ID) ?: return
                val isStrike = intent.getBooleanExtra(EXTRA_TAG_STRIKE, false)
                val listId = intent.getStringExtra(EXTRA_LIST_ID) ?: return
                val version = intent.getIntExtra(EXTRA_LIST_VERSION, 0) +1

                WidgetScope.scope.launch {
                    database.strikeTag(listId = listId, tagName = tagId, tagStrike = !isStrike, version)
                }
                val manager = AppWidgetManager.getInstance(context)
                val provider = ComponentName(context, FamilyWidgetProvider::class.java)
                val ids = manager.getAppWidgetIds(provider)

                @Suppress("DEPRECATION")
                manager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list)
            }
        }
        pendingResult.finish()
    }

    companion object {
        private var lastClickTimeEdit = 0L
        private var lastClickTimeItem = 0L
        private const val PERIODIC_CLICK = 400L

        private const val ACTION_CLICK_EDIT = "family_shopper_widget_action_click_edit"
        private const val ACTION_CLICK_ITEM = "family_shopper_widget_action_click_item"

        const val EXTRA_TAG_ID = "family_shopper_widget_tag_id"
        const val EXTRA_TAG_STRIKE = "family_shopper_widget_tag_strike"
        const val EXTRA_LIST_ID = "family_shopper_widget_list_id"
        const val EXTRA_LIST_VERSION = "family_shopper_widget_list_version"
    }
}