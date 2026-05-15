package ru.gorinih.familyshopper.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Igor Abdulganeev on 12.05.2026
 */

class FamilyVoiceRecognizerImpl(
    private val context: Context
) : FamilyVoiceRecognizer {
    private var speechService: SpeechService? = null
    private var modelSpeech: Model? = null
    private var recognizer: Recognizer? = null

    override fun isPrepared(): Boolean = recognizer != null

    override fun startListening(): Flow<String> = callbackFlow {
        val isListening = AtomicBoolean(true)
        if (recognizer == null){
            close(IllegalStateException("Not init voice recognizer"))
            return@callbackFlow
        }

        val listener = object : RecognitionListener {
            override fun onPartialResult(hypothesis: String?) {
                if (isListening.get()) {
                    hypothesis?.let {
                        val json = Json.parseToJsonElement(it)
                        val text = json.jsonObject["partial"]?.jsonPrimitive?.content ?: ""
                        if (text.isNotBlank()) trySend(text)
                    }
                }
            }

            override fun onResult(hypothesis: String?) {
            }

            override fun onFinalResult(hypothesis: String?) {
            }

            override fun onError(ex: Exception?) {
                isListening.set(false)
                close(ex)
            }

            override fun onTimeout() {
                isListening.set(false)
                close()
            }
        }

        try {
            speechService = SpeechService(recognizer, 16000.0f)
            speechService?.startListening(listener)
        } catch (ex: Throwable) {
            close(ex)
        }

        awaitClose {
            isListening.set(false)
            stopListening()
        }
    }

    override suspend fun initRecognizer(): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        if (modelSpeech != null && recognizer != null) return true
        return try {
            val loadingModel = suspendCancellableCoroutine<Model> { continuation ->
                StorageService.unpack(
                    context,
                    "model-ru",
                    "model",
                    { model ->
                        if (continuation.isActive) continuation.resume(model) { _, modelToClose, _ ->
                            modelToClose?.close()
                         }
                    },
                    { exception ->
                        if (continuation.isActive) continuation.resumeWith(Result.failure(exception))
                    }
                )
            }
            modelSpeech = loadingModel
            recognizer = Recognizer(
                modelSpeech,
                16000.0f
            )
            true
        } catch (_: Throwable) {
            false
        }
    }

    override fun closeRecognizer() {
        stopListening()
        recognizer?.close()
        recognizer = null
        modelSpeech?.close()
        modelSpeech = null
    }

    private fun stopListening() {
        try {
            speechService?.apply {
                stop()
                shutdown()
            }
        } catch (_: Throwable){}
        finally {
            speechService = null
        }
    }
}