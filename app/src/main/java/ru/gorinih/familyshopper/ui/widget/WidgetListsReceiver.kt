package ru.gorinih.familyshopper.ui.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetListsReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetLists()

}