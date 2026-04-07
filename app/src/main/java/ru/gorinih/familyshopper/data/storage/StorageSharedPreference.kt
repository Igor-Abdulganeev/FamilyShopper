package ru.gorinih.familyshopper.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.gorinih.familyshopper.domain.StorageRepository
import java.util.UUID

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class StorageSharedPreference(
    context: Context
): StorageRepository {
    private val preference: SharedPreferences =
        context.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE)

    override fun getClientUUID(): String = preference.getString(CLIENT_UUID, "").takeIf { !it.isNullOrBlank() }
        ?: UUID.randomUUID().toString().also {
            setClientUUID(it)
        }

    override fun setClientUUID(uuid: String) = preference.edit {
        putString(CLIENT_UUID, uuid)
    }

    override fun getGroupUUID(): String = preference.getString(GROUP_UUID, "") ?: ""

    override fun setGroupUUID(uuid: String) = preference.edit {
        putString(GROUP_UUID, uuid)
    }


    companion object {
        private const val GROUP_UUID = "family_shopper_uuid_group"
        private const val CLIENT_UUID = "family_shopper_uuid_client"

        private const val SETTING_FILE_NAME = "family_settings"
    }
}