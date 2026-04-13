package ru.gorinih.familyshopper.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.views.RoundedTextField

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
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
                        onClick = {viewModel.onShareGroupUuid()}
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
                        onClick = {viewModel.onShareClientUuid()}
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

    }
}