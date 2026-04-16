package ru.gorinih.familyshopper.ui.screens.settings

import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.views.RoundedTextField

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigationActions: (NavigationActions) -> Unit,
    backPressed: () -> Unit,
    firstTimeBackPressed: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state = viewModel.stateSettings
    var groupIdEditable by rememberSaveable { mutableStateOf(state.groupUUID.isBlank()) }
    var clientIdEditable by rememberSaveable { mutableStateOf(state.clientUUID.isBlank()) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.shareEvents.collect { uuid ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, uuid)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            context.startActivity(shareIntent)
            viewModel.shareDone()
        }
    }

    val handlerExit = {
        viewModel.saveUserName()
        when (state.isFirstTime) {
            true -> firstTimeBackPressed()
            false -> backPressed()
        }
    }

    BackHandler(enabled = true) {
        handlerExit()
    }
    DisposableEffect(Unit) {
        navigationActions(NavigationActions(onNavigationClick = {
            handlerExit()
        }))

        onDispose {
            navigationActions(NavigationActions(onNavigationClick = { handlerExit() }))
        }
    }

    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .verticalScroll(state = scrollState)
    ) {
        Text(
            text = stringResource(R.string.help_group_key_property),
            autoSize = TextAutoSize.StepBased(maxFontSize = TextAutoSizeDefaults.MaxFontSize * 0.1),
            style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
            textAlign = TextAlign.Justify,
            modifier = Modifier.alpha(0.5f)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        RoundedTextField(
            value = state.groupUUID,
            onValueChange = { str ->
                viewModel.updateGroupUuid(str)
            },
            label = stringResource(R.string.label_key_group),
            isEditable = groupIdEditable,
            trailingIcon = {
                Row() {
                    if (groupIdEditable) {
                        IconButton(
                            onClick = {
                                with(viewModel) {
                                    updateGroupUuid("")
                                    applyGroupUuid()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SettingsBackupRestore,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.createGroupUUID()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            groupIdEditable = !groupIdEditable
                            if (!groupIdEditable) viewModel.applyGroupUuid()
                        }
                    ) {
                        val icon = when (groupIdEditable) {
                            true -> Icons.Default.LockOpen
                            false -> Icons.Default.Lock
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }
                    IconButton(
                        onClick = { viewModel.onShareGroupUuid() }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    }
                }
            }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        Text(
            text = stringResource(R.string.help_client_key_property),
            autoSize = TextAutoSize.StepBased(maxFontSize = TextAutoSizeDefaults.MaxFontSize * 0.1),
            style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
            textAlign = TextAlign.Justify,
            modifier = Modifier.alpha(0.5f)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        RoundedTextField(
            value = state.clientUUID,
            onValueChange = { str ->
                viewModel.updateClientUuid(str)
            },
            label = stringResource(R.string.label_key_client),
            isEditable = clientIdEditable,
            trailingIcon = {
                Row() {
                    if (clientIdEditable) {
                        IconButton(
                            onClick = {
                                with(viewModel) {
                                    updateClientUuid("")
                                    applyClientUuid()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SettingsBackupRestore,
                                contentDescription = null
                            )
                        }

                    }
                    IconButton(
                        onClick = {
                            clientIdEditable = !clientIdEditable
                            if (!clientIdEditable) viewModel.applyClientUuid()
                        }
                    ) {
                        val icon = when (clientIdEditable) {
                            true -> Icons.Default.LockOpen
                            false -> Icons.Default.Lock
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }
                    IconButton(
                        onClick = { viewModel.onShareClientUuid() }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    }
                }
            }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        Text(
            text = stringResource(R.string.help_keys_property),
            autoSize = TextAutoSize.StepBased(maxFontSize = TextAutoSizeDefaults.MaxFontSize * 0.15),
            style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
            textAlign = TextAlign.Justify,
            fontWeight = FontWeight.Bold,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
        Text(
            text = stringResource(R.string.help_user_name_property),
            autoSize = TextAutoSize.StepBased(maxFontSize = TextAutoSizeDefaults.MaxFontSize * 0.1),
            style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
            textAlign = TextAlign.Justify,
            modifier = Modifier.alpha(0.5f)
        )
        RoundedTextField(
            value = state.userName,
            onValueChange = { str ->
                viewModel.updateUserName(str)
            },
            label = stringResource(R.string.label_user_name),
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
            ) {
                Checkbox(
                    checked = state.rainbow,
                    onCheckedChange = {
                        viewModel.updateBackground()
                    },
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    "фон списка динамический (выбрано) или статический (не выбрано).",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            itemsIndexed((1..4).toList()) { index, item ->
                if (index == 0) {
                    Text(
                        "тип создаваемого списка по умолчанию",
                        //modifier = Modifier,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable(
                            onClick = {
                                viewModel.updateTypeList(item)
                            }
                        )
                ) {
                    val label = when (item) {
                        1 -> R.string.label_full_icon_all
                        2 -> R.string.label_full_icon_add
                        3 -> R.string.label_full_icon_view
                        else -> R.string.label_full_icon_private
                    }
                    Image(
                        painter = GlassCircleImageHolder.getImage(item),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .size(16.dp),
                        contentScale = ContentScale.Inside,
                        colorFilter = when {
                            state.defaultTypeList == index + 1 -> null

                            else -> ColorFilter.tint(Color.Gray, blendMode = BlendMode.SrcIn)
                        }
                    )
                    Text(
                        text = stringResource(label), modifier = Modifier
                            .padding(start = 8.dp),
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 12.sp
                        )
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )

    }
}