package ru.gorinih.familyshopper.ui.widget

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListObject
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme
import ru.gorinih.familyshopper.ui.views.CardListSimpleItem

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

class FamilyConfigWidgetActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setResult(RESULT_CANCELED)
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finishAfterTransition()
        setContent {
            FamilyShopperTheme {
                val viewModel: FamilyWidgetViewModel = koinViewModel()
                val listOfProducts = viewModel.stateList.collectAsState(emptyList())
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    ShowListToSelect(
                        listOfProducts,
                        modifier = Modifier.padding(innerPadding)
                    ) { listId -> //, listVersion, listLegend, listOwner ->
                        viewModel.saveWidget(appWidgetId, listId)

                        val manager = AppWidgetManager.getInstance(this)
                        val ids = manager.getAppWidgetIds(ComponentName(this, FamilyWidgetProvider::class.java))

                        @Suppress("DEPRECATION")
                        manager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list)

                        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        setResult(RESULT_OK, result)
                        finishAfterTransition()
                    }
                }
            }
        }
    }
}

@Composable
fun ShowListToSelect(
    listOfProducts: State<List<UiListObject>>,
    modifier: Modifier = Modifier,
    onListSelected: (String) -> Unit,
) {
    val stateList = rememberLazyListState()
    Column(
        modifier = modifier
    ) {
        LazyColumn(state = stateList) {
            items(listOfProducts.value, key = { list -> list.listId }) { item ->
                CardListSimpleItem(
                    item = item,
                    onClick = onListSelected
                )
            }
        }
    }
}
