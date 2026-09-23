package ru.gorinih.familyshopper.voice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Igor Abdulganeev on 04.08.2026
 */

class FamilyVoiceRecognizerImpl : FamilyVoiceRecognizer {
    override suspend fun initRecognizer(): Boolean = false

    override fun startListening(): Flow<String> = flow { emit("") }

    override fun isPrepared(): Boolean = false

    override suspend fun closeRecognizer() {}
}