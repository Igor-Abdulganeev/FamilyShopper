package ru.gorinih.familyshopper.data.remote.interceptors

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.promisesBody
import okio.Buffer
import ru.gorinih.familyshopper.data.services.JsonService

/**
 * Created by Igor Abdulganeev on 03.04.2026
 */

class LoggerInterceptor(
    private val jsonService: JsonService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val stringBuilder = StringBuilder()
        val headers = request.headers
        with(stringBuilder) {
            val requestBody = request.body
            val method = request.method
            val url = request.url
            append("--> ")
            append(method)
            append(" ")
            append(url)

            appendLine()
            append("Request: ")

            var i = 0
            val count = headers.size
            while (i < count) {
                val name = headers.name(i)
                val value = headers.value(i)
                appendLine()
                append(name)
                append(": ")
                append(value)
                i++
            }
            requestBody?.let { body ->
                val buffer = Buffer()
                body.writeTo(buffer)
                val charset = body.contentType()?.charset() ?: Charsets.UTF_8
                val requestBodyString = buffer.readString(charset).let {
                    jsonService.jsonPrint(it)
                }
                appendLine()
                appendLine(requestBodyString)
                append("----")
            }
        }
        /**
         * выводим лог отправленного запроса
         */
        Log.i("", stringBuilder.toString())

        val response = chain.proceed(request)
        val responseBody: ResponseBody = response.body ?: run {
            with(stringBuilder) {
                appendLine()
                append("no body")
                appendLine()
                append("<--")
            }
            return response
        }

        stringBuilder.appendLine()
        when {
            !response.promisesBody() -> stringBuilder.append("no body")
            bodyEncoded(headers) -> stringBuilder.append("body encoded")
            else -> {
                val charset = responseBody.contentType()?.charset() ?: Charsets.UTF_8
                val responseBodyString = responseBody.source()
                    .apply {
                        request(Long.MAX_VALUE)
                    }
                    .buffer
                    .clone()
                    .readString(charset)
                    .let {
                        jsonService.jsonPrint(it)
                    }
                with(stringBuilder) {
                    append("Response")
                    appendLine(": ")
                    append(responseBodyString)
                }
            }
        }
        stringBuilder
            .appendLine()
            .append("<--")
        Log.d("", stringBuilder.toString())

        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

}