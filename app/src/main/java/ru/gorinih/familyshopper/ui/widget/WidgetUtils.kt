package ru.gorinih.familyshopper.ui.widget

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_LIST
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_VERSION
import ru.gorinih.familyshopper.di.dataStore

/**
 * Created by Igor Abdulganeev on 29.04.2026
 */

suspend fun notifyWidgetAboutChanged(context: Context, uuid: String, isDelete: Boolean) {
    val manager = GlanceAppWidgetManager(context)
    val store = context.dataStore
    try {
        val glanceIds = manager.getGlanceIds(WidgetLists::class.java)
        glanceIds.forEach { glanceId ->
            val appWidgetId = manager.getAppWidgetId(glanceId)
            val listId = stringPreferencesKey("${WIDGET_LIST}_$appWidgetId")
            val listVersion = intPreferencesKey("${WIDGET_VERSION}_$appWidgetId")

            store.updateData { pref ->
                val mutablePref = pref.toMutablePreferences()
                val savedUuid = mutablePref[listId]
                if (savedUuid == uuid) {
                    when (isDelete) {
                        true -> {
                            mutablePref.remove(listVersion)
                            mutablePref.remove(listId)
                        }

                        false -> {
                            val version = (mutablePref[listVersion] ?: 0) + 1
                            mutablePref[listVersion] = version
                        }
                    }
                    Log.i("GINES","UPDATE $uuid")
                    WidgetLists().update(context, glanceId)
                }
                mutablePref
            }
        }
    } catch (ex: IllegalArgumentException) {
        Log.e("GINES","UPDATE ${ex.localizedMessage}")
    }

}