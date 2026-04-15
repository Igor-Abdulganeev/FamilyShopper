package ru.gorinih.familyshopper.domain

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

interface StorageRepository {
    fun getClientUUID(): String

    fun setClientUUID(uuid: String)

    fun getGroupUUID(): String

    fun setGroupUUID(uuid: String)

    fun getStartedKey(): Boolean

    fun setStartedKey()

    fun getUserName(): String

    fun setUserName(name: String)

    fun getBackgroundState(): Boolean

    fun setBackgroundState(rainbow: Boolean)

    fun getTypeList(): Int

    fun setTypeList(type: Int)
}