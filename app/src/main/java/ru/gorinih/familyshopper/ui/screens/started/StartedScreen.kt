package ru.gorinih.familyshopper.ui.screens.started
/*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.navigation.NavigationKey
import ru.gorinih.familyshopper.ui.views.shadow
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme


/**
 * Created by Igor Abdulganeev on 06.04.2026
 */
@Composable
fun StartedScreen(
    modifier: Modifier = Modifier,
    router: (NavigationKey) -> Unit,
    backClick: ()-> Unit,
    navigationActions: (NavigationActions) -> Unit,
) {
    LaunchedEffect(Unit) {
        navigationActions(NavigationActions(onNavigationClick = {backClick()}))
    }
    Column(modifier = modifier.padding(horizontal = 2.dp)) {
/*
        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth().shadow(
                colorLight = MaterialTheme.colorScheme.primary,
                shadowRadius = 4.dp,
                borderRadius = 16.dp,
                offsetYLight = 2.dp,
                offsetXLight = 3.dp,
                alphaShadowLight = 0.3f
            ),
            onClick = {
                router(NavigationKey.SettingsScreen)
            }
        ) {
            Text("настройки")
        }
*/
        /*
                Button(
                    modifier = Modifier.padding(8.dp).fillMaxWidth().shadow(
                        colorLight = MaterialTheme.colorScheme.primary,
                        shadowRadius = 4.dp,
                        borderRadius = 16.dp,
                        offsetYLight = 2.dp,
                        offsetXLight = 3.dp,
                        alphaShadowLight = 0.3f
                    ),
                    onClick = {
                        router(NavigationKey.DictionariesScreen)
                    },
                ) {
                    Text("все товары")
                }
        */
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .shadow(
                    colorLight = MaterialTheme.colorScheme.primary,
                    shadowRadius = 4.dp,
                    borderRadius = 16.dp,
                    offsetYLight = 2.dp,
                    offsetXLight = 3.dp,
                    alphaShadowLight = 0.3f
                ),
            onClick = {
                router(NavigationKey.EditListScreen(listUuid = ""))
            },
        ) {
            Text("новый список")
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .shadow(
                    colorLight = MaterialTheme.colorScheme.primary,
                    shadowRadius = 4.dp,
                    borderRadius = 16.dp,
                    offsetYLight = 2.dp,
                    offsetXLight = 3.dp,
                    alphaShadowLight = 0.3f
                ),
            onClick = {
                router(NavigationKey.ListEntityScreen)
            },
        ) {
            Text("списки")
        }
    }
}

@Preview
@Composable
fun PreviewButtons() {
    FamilyShopperTheme() {
        Column(Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .shadow(
                        colorLight = MaterialTheme.colorScheme.primary,
                        shadowRadius = 3.dp,
                        borderRadius = 16.dp,
                        offsetYLight = 3.dp,
                        offsetXLight = 3.dp,
                        alphaShadowLight = 0.1f
                    ),
                onClick = { },
            ) {
                Text("новый список")
            }

        }
    }
}

 */