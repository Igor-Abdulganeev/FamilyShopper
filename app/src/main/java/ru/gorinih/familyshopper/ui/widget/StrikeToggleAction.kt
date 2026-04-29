package ru.gorinih.familyshopper.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
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
        val tagName = parameters[KEY_TAG_NAME] ?: return
        val tagStrike = parameters[KEY_TAG_STRIKE] ?: return
        val listId = parameters[KEY_LIST_ID] ?: return
        val version = parameters[KEY_LIST_VERSION] ?: return
        val nextVersion = version + 1


        withContext(Dispatchers.IO) {
            database.strikeTag(
                listId = listId,
                tagName = tagName,
                tagStrike = !tagStrike,
                listVersion = nextVersion
            )
        }

        val store = context.dataStore
        val listVersion = intPreferencesKey(WIDGET_VERSION)
        WidgetScope.scope.launch {
            store.updateData {
                it.toMutablePreferences().apply {
                    set(listVersion, nextVersion)
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
    }
}