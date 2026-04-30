package ru.gorinih.familyshopper.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

class FamilyWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        FamilyWidgetFactory(applicationContext, intent)
}