package ru.gorinih.familyshopper.ui.widget

import android.appwidget.AppWidgetManager
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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_EDIT
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_FORCE_UPDATE
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_LIST
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference.Companion.WIDGET_VERSION
import ru.gorinih.familyshopper.di.dataStore
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListObject
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme
import ru.gorinih.familyshopper.ui.views.CardListSimpleItem

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetConfigurationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setResult(RESULT_CANCELED)
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        setContent {
            FamilyShopperTheme {
                val viewModel: WidgetViewModel = koinViewModel()
                val listOfProducts = viewModel.stateList.collectAsState(emptyList())
                val uuid = viewModel.takeUserUuid()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    ShowListToSelect(
                        listOfProducts,
                        modifier = Modifier.padding(innerPadding)
                    ) { listId, listVersion, listLegend, listOwner ->
                        val isEdit =
                            (uuid == listOwner && listLegend != TypeLegendList.VIEW) || (listLegend == TypeLegendList.ALL || listLegend == TypeLegendList.ADD)
                        saveListUuid(this, appWidgetId, listId, listVersion, isEdit)
                    }
                }
            }
        }
    }

    private fun saveListUuid(
        context: Context,
        appWidgetId: Int,
        listUuid: String,
        version: Int,
        isEdit: Boolean,
    ) {
        val store = context.dataStore
        val listIdKey = stringPreferencesKey("${WIDGET_LIST}_$appWidgetId")
        val listVersion = intPreferencesKey("${WIDGET_VERSION}_$appWidgetId")
        val listEdit = booleanPreferencesKey("${WIDGET_EDIT}_$appWidgetId")
        val listForceUpdate = longPreferencesKey("${WIDGET_FORCE_UPDATE}_$appWidgetId")
        lifecycleScope.launch {
            store.updateData {
                it.toMutablePreferences().apply {
                    set(listIdKey, listUuid)
                    set(listVersion, version)
                    set(listEdit, isEdit)
                    set(listForceUpdate, System.currentTimeMillis())
                }
            }
            Log.i("GINES","UP listIdKey=$listUuid  listVersion=$version  listEdit=$isEdit")
            withContext(Dispatchers.Main.immediate) {
                val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setResult(RESULT_OK, result)
                finishAfterTransition()
            }
        }
    }

}

@Composable
fun ShowListToSelect(
    listOfProducts: State<List<UiListObject>>,
    modifier: Modifier = Modifier,
    onListSelected: (String, Int, TypeLegendList, String) -> Unit,
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
