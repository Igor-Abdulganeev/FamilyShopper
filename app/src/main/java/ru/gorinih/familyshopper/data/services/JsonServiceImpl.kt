package ru.gorinih.familyshopper.data.services

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

/**
 * Created by Igor Abdulganeev on 03.04.2026
 */

class JsonServiceImpl: JsonService {

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    override fun jsonPrint(jsonString: String?): String? = try {
        val jsonElement = JsonParser.parseString(jsonString)
        gson.toJson(jsonElement)
    } catch (_: Throwable) {
        jsonString
    }

}