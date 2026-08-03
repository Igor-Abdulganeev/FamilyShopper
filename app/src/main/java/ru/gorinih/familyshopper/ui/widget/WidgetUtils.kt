package ru.gorinih.familyshopper.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_FORCE_UPDATE
import ru.gorinih.familyshopper.ui.views.WidgetNotifier

/**
 * Created by Igor Abdulganeev on 29.04.2026
 */

class WidgetUtils(
    private val context: Context
) : WidgetNotifier {
    override suspend fun notifyWidgetChanged() {
        val context = context.applicationContext
        notifyWidgetAboutChanged(context)
    }

    private suspend fun notifyWidgetAboutChanged(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(WidgetLists::class.java)
        glanceIds.forEach { glanceId ->
            val listForceUpdate = longPreferencesKey(WIDGET_FORCE_UPDATE)
            updateAppWidgetState(context, glanceId) { updateState ->
                updateState[listForceUpdate] = System.currentTimeMillis()
            }
            WidgetLists().update(context, glanceId)
        }
    }
}

