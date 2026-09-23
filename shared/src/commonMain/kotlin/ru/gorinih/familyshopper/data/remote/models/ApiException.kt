package ru.gorinih.familyshopper.data.remote.models

/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

class ApiException(code: Int, message: String) : Exception(message)