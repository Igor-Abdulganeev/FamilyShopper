package ru.gorinih.familyshopper.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.content.TextContent
import io.ktor.http.URLProtocol
import io.ktor.network.sockets.SocketTimeoutException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import ru.gorinih.familyshopper.data.remote.models.ApiException
import ru.gorinih.familyshopper.data.remote.models.ListObject
import ru.gorinih.familyshopper.data.remote.models.ListTagObject
import ru.gorinih.familyshopper.data.remote.models.ListVersionInfo
import ru.gorinih.familyshopper.data.remote.models.RemoteDictionary
import java.io.IOException

/**
 * Created by Igor Abdulganeev on 25.06.2026
 */

@OptIn(ExperimentalSerializationApi::class)
fun createHttpClient(
    baseUrl: String
): HttpClient {
    val json = Json {
        prettyPrint = true
    }
    val client = HttpClient {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = true
                }
            )
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 10000 // 10 sec
            socketTimeoutMillis = 15000
            requestTimeoutMillis = 30000
        }

        HttpResponseValidator {
            validateResponse { response ->
                val codeResponse = response.status.value
                if (codeResponse !in 200..299) {
                    val errorMessage = when (codeResponse) {
                        401 -> "Не авторизован (401)"
                        403 -> "Нет доступа (403)"
                        404 -> "Ресурс не найден (404)"
                        in 400..499 -> "Ошибка клиента: $codeResponse"
                        in 500..599 -> "Ошибка сервера: $codeResponse"
                        else -> "Неизвестная ошибка: $codeResponse"
                    }
                    throw ApiException(codeResponse, errorMessage)
                }
            }

            handleResponseException { cause -> // обработка ошибок сети
                when (cause) {
                    is ConnectTimeoutException -> throw ApiException(
                        408,
                        "Не удалось подключиться к серверу по таймауту"
                    )

                    is SocketTimeoutException -> throw ApiException(
                        408,
                        "Сервер перестал отвечать в процессе передачи"
                    )

                    is HttpRequestTimeoutException -> throw ApiException(
                        408,
                        "Время ожидания запроса истекло"
                    )
                    // Физическое отсутствие сети (нет вайфая, оборван кабель)
                    is IOException -> throw Exception("Проблема с сетью, проверьте соединение")
                    else -> throw cause
                }
            }
        }
    }
    client.receivePipeline.intercept(HttpReceivePipeline.State) { response ->
        val code = response.status.value
        val method = response.request.method.value
        val url = response.request.url.toString()
        val headers = response.headers
        val stringBuilder = StringBuilder()
        with(stringBuilder) {
            append("<-- ")
            append(method)
            append(" ")
            append(url)
            appendLine()
            append("$code Response: ")
            val countHeaders = headers.entries().count()
            var currentHeaderIndex = 0
            headers.forEach { key, values ->
                append("\n    -> $key: ${values.joinToString(", ")}")
                currentHeaderIndex++
                if (currentHeaderIndex == countHeaders) appendLine()
            }

            val bodyString = try {
                val bodyText = response.bodyAsText()
                val jsonElement = json.parseToJsonElement(bodyText)
                json.encodeToString(jsonElement)
            } catch (e: Exception) {
                "Не удалось прочитать тело ответа: ${e.message}"
            }
            if (bodyString.isNotEmpty()) {
                appendLine()
                appendLine(bodyString)
            }
            append("----")
        }
        println(stringBuilder.trim())
        proceedWith(response)
    }

    client.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
        val method = context.method.value
        val url = context.url.buildString()
        val headers = context.headers.build()
        val stringBuilder = StringBuilder()
        with(stringBuilder) {
            append("--> ")
            append(method)
            append(" ")
            append(url)
            appendLine()
            append("Request: ")
            val countHeaders = headers.entries().count()
            var currentHeaderIndex = 0
            headers.forEach { key, values ->
                append("\n    -> $key: ${values.joinToString(", ")}")
                currentHeaderIndex++
                if (currentHeaderIndex < countHeaders) appendLine()
            }


            val bodyString = when (val body = context.body) {
                is TextContent -> {
                    try {
                        val jsonElement = json.parseToJsonElement(body.text)
                        json.encodeToString(jsonElement)
                    } catch (e: Exception) {
                        "Не удалось прочитать тело запроса: ${e.message}"
                    }
                }

                is FormDataContent -> "\n   ${body.formData.toLogString()}"
                else -> ""
            }

            if (bodyString.isNotEmpty()) {
                appendLine()
                appendLine(bodyString)
            }
            append("----")
        }

        println(stringBuilder.trim())
        proceed()
    }


    return client
}

private fun io.ktor.http.Parameters.toLogString(): String {
    return entries().joinToString(", ") { "${it.key}=${it.value}" }
}

object AnyMapSerializer : KSerializer<Map<String, Any?>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AnyMap")

    override fun serialize(
        encoder: Encoder,
        value: Map<String, Any?>
    ) {
        val jsonObject = buildJsonObject {
            value.forEach { (key, item) ->
                val jsonElement = when (item) {
                    null -> JsonPrimitive(null)
                    is String -> JsonPrimitive(item)
                    is Number -> JsonPrimitive(item)
                    is Boolean -> JsonPrimitive(item)
                    is RemoteDictionary -> {
                        Json.encodeToJsonElement(RemoteDictionary.serializer(), item)
                    }

                    is ListObject -> {
                        Json.encodeToJsonElement(ListObject.serializer(), item)
                    }

                    is ListVersionInfo -> {
                        Json.encodeToJsonElement(ListVersionInfo.serializer(), item)
                    }

                    is ListTagObject -> {
                        Json.encodeToJsonElement(ListTagObject.serializer(), item)
                    }

                    else -> Json.encodeToJsonElement(item)
                }
                put(key, jsonElement)
            }
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any?> {
        throw UnsupportedOperationException("Десериализация AnyMap не поддерживается")
    }
}
