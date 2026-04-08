package ru.gorinih.familyshopper.ui.screens.started

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.gorinih.familyshopper.navigation.NavigationKey

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */
@Composable
fun StartedScreen(
    modifier: Modifier = Modifier,
    router: (NavigationKey) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 2.dp)) {
        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            onClick = {
                router(NavigationKey.SettingsScreen)
            }
        ) {
            Text("настройки")
        }
        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            onClick = {
                router(NavigationKey.DictionariesScreen)
            },
        ) {
            Text("все товары")
        }
        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            onClick = {
                router(NavigationKey.EditListScreen(listUuid = ""))
            },
        ) {
            Text("новый список")
        }
    }
}