package ru.gorinih.familyshopper.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_LIST
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_VERSION
import ru.gorinih.familyshopper.di.dataStore

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetListsReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetLists()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        WidgetScope.scope.launch {
            context.dataStore.updateData { preferences ->
                val pref = preferences.toMutablePreferences()
                appWidgetIds.forEach { appWidgetId ->
                    pref.remove(stringPreferencesKey("${WIDGET_LIST}_$appWidgetId"))
                    pref.remove(intPreferencesKey("${WIDGET_VERSION}_$appWidgetId"))
                }
                pref
            }
        }

    }
}