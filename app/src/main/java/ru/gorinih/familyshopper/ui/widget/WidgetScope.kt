package ru.gorinih.familyshopper.ui.widget

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

object WidgetScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}