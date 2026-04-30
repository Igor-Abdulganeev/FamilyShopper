package ru.gorinih.familyshopper.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
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
                    ) { listId ->
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
        if (listOfProducts.value.isNotEmpty()) {
            LazyColumn(state = stateList) {
                items(listOfProducts.value, key = { list -> list.listId }) { item ->
                    CardListSimpleItem(
                        item = item,
                        onClick = onListSelected
                    )
                }
            }
        } else {
            Text(text = stringResource(R.string.label_empty_list),
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(16.dp),
                style = TextStyle(fontSize = 24.sp))
        }
    }
}
