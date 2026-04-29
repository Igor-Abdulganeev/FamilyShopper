package ru.gorinih.familyshopper.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_VERSION
import ru.gorinih.familyshopper.di.dataStore
import ru.gorinih.familyshopper.domain.DatabaseRepository

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class StrikeToggleAction : ActionCallback, KoinComponent {

    private val database: DatabaseRepository by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val tagName = parameters[KEY_TAG_NAME] ?: ""
        val tagStrike = parameters[KEY_TAG_STRIKE] ?: false
        val listId = parameters[KEY_LIST_ID] ?: ""
        val version = parameters[KEY_LIST_VERSION] ?: 0
        val action = parameters[KEY_ACTION_TYPE] ?: return

        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = try {
            manager.getAppWidgetId(glanceId)
        } catch (_: IllegalArgumentException) {
            AppWidgetManager.INVALID_APPWIDGET_ID
        }

        val nextVersion = version + 1

        when(action){
            ACTION_REFRESH -> {
            }
            ACTION_STRIKE -> {
                withContext(Dispatchers.IO) {
                    database.strikeTag(
                        listId = listId,
                        tagName = tagName,
                        tagStrike = !tagStrike,
                        listVersion = nextVersion
                    )
                }
            }
        }



        val store = context.dataStore
        val listVersion = intPreferencesKey("${WIDGET_VERSION}_$appWidgetId")
        WidgetScope.scope.launch {
            store.updateData {
                it.toMutablePreferences().apply {
                    if (action == ACTION_STRIKE) set(listVersion, nextVersion)
                }
            }
        }
        WidgetLists().update(context, glanceId)
    }

    companion object {
        val KEY_TAG_NAME = ActionParameters.Key<String>("family_shopper_tag_name")
        val KEY_TAG_STRIKE = ActionParameters.Key<Boolean>("family_shopper_tag_strike")
        val KEY_LIST_ID = ActionParameters.Key<String>("family_shopper_list_id")
        val KEY_LIST_VERSION = ActionParameters.Key<Int>("family_shopper_list_version")
        val KEY_ACTION_TYPE = ActionParameters.Key<String>("family_shopper_action_type")

        const val ACTION_REFRESH = "family_shopper_action_REFRESH"
        const val ACTION_STRIKE = "family_shopper_action_strike"
    }
}