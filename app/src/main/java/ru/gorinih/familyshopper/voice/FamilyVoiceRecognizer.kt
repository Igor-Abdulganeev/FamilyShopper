package ru.gorinih.familyshopper.voice

import kotlinx.coroutines.flow.Flow

/**
 * Created by Igor Abdulganeev on 14.05.2026
 */

interface FamilyVoiceRecognizer {
    suspend fun initRecognizer(): Boolean
    fun startListening(): Flow<String>
    fun isPrepared(): Boolean
    fun closeRecognizer()
}
