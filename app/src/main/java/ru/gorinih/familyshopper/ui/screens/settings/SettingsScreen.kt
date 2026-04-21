package ru.gorinih.familyshopper.ui.screens.settings

import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.navigation.NavigationActions
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.screens.about.AboutScreen
import ru.gorinih.familyshopper.ui.views.DividerTransparent
import ru.gorinih.familyshopper.ui.views.RoundedTextField
import ru.gorinih.familyshopper.ui.views.Users

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
    val scrollPageState = rememberScrollState()
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

    val pagesTitle = listOf(
        stringResource(R.string.label_settings_tab_keys),
        stringResource(R.string.label_settings_tab_users),
        stringResource(R.string.label_settings_tab_views),
        stringResource(R.string.label_settings_tab_about)
    )
    var expandedGroupKey by rememberSaveable { mutableStateOf(false) }
    var expandedUserKey by rememberSaveable { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 0) { pagesTitle.size }
    val coroutine = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp)
    ) {

        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex = pagerState.currentPage,
                    )
                )
            }
        ) {
            pagesTitle.forEachIndexed { index, header ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = {},
                    text = {
                        SuggestionChip(
                            onClick = {
                                coroutine.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            label = {
                                Text(text = header, overflow = TextOverflow.Ellipsis)
                            }
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = true,
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .verticalScroll(state = scrollPageState),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        RoundedTextField(
                            modifier = Modifier.padding(top = 16.dp),
                            value = state.groupUUID,
                            onValueChange = { str ->
                                viewModel.updateGroupUuid(str)
                            },
                            label = stringResource(R.string.label_key_group),
                            isEditable = groupIdEditable,
                            /*
                                                        leadingIcon = {
                                                            Icon(Icons.Default.VpnKey, contentDescription = null)
                                                        },
                            */
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
                                                imageVector = Icons.Default.Restore,
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
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                        Column(
                            modifier = Modifier
                                .animateContentSize()
                        ) {
                            when (expandedGroupKey) {
                                true -> Text(
                                    text = stringResource(R.string.help_group_key_property),
                                    fontSize = 12.sp,
                                    style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .alpha(0.8f)
                                        .clickable(
                                            onClick = { expandedGroupKey = !expandedGroupKey }
                                        ),
                                )

                                false -> Text(
                                    text = stringResource(R.string.help_group_key_property),
                                    fontSize = 12.sp,
                                    style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .alpha(0.8f)
                                        .clickable(
                                            onClick = { expandedGroupKey = !expandedGroupKey }
                                        ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        RoundedTextField(
                            modifier = Modifier.padding(top = 16.dp),
                            value = state.clientUUID,
                            onValueChange = { str ->
                                viewModel.updateClientUuid(str)
                            },
                            label = stringResource(R.string.label_key_client),
                            isEditable = clientIdEditable,
                            /*
                                                        leadingIcon = {
                                                                      Icon(Icons.Default.AccountCircle, contentDescription = null)
                                                               },
                            */
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
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                        Column(
                            modifier = Modifier
                                .animateContentSize()
                        ) {
                            when (expandedUserKey) {
                                true -> Text(
                                    text = stringResource(R.string.help_client_key_property),
                                    fontSize = 12.sp,
                                    style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .alpha(0.8f)
                                        .clickable(
                                            onClick = { expandedUserKey = !expandedUserKey }
                                        ),
                                )

                                false -> Text(
                                    text = stringResource(R.string.help_client_key_property),
                                    fontSize = 12.sp,
                                    style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .alpha(0.8f)
                                        .clickable(
                                            onClick = { expandedUserKey = !expandedUserKey }
                                        ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        DividerTransparent(Modifier.padding(top = 16.dp))

                        Text(
                            text = stringResource(R.string.help_keys_property),
                            autoSize = TextAutoSize.StepBased(maxFontSize = TextAutoSizeDefaults.MaxFontSize * 0.15),
                            style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
                            textAlign = TextAlign.Justify,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        RoundedTextField(
                            modifier = Modifier.padding(top = 16.dp),
                            value = state.userName,
                            onValueChange = { str ->
                                viewModel.updateUserName(str)
                            },
                            label = stringResource(R.string.label_user_name),
                        )
                        Text(
                            text = stringResource(R.string.help_user_name_property),
                            fontSize = 12.sp,
                            style = LocalTextStyle.current.copy(lineHeight = TextUnit.Unspecified),
                            textAlign = TextAlign.Justify,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .alpha(0.8f)
                        )


                        DividerTransparent(modifier = Modifier.padding(vertical = 16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = stringResource(R.string.header_other_users))
                            AnimatedVisibility(visible = state.listUsers.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.clearUsers() },
                                ) {
                                    Icon(Icons.Default.DeleteForever, null)
                                }
                            }
                            IconButton(
                                onClick = { viewModel.updateUsers(true) },
                            ) {
                                Icon(Icons.Default.Repeat, null)
                            }

                        }

                        AnimatedVisibility(visible = state.listUsers.isNotEmpty()) {
                            Users(
                                list = state.listUsers,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onEdit = {
                                    viewModel.updateUser(it, isDelete = false)
                                },
                                onDelete = {
                                    viewModel.updateUser(it, isDelete = true)
                                }
                            )
                        }
                    }
                }

                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = state.rainbow,
                                    onCheckedChange = {
                                        viewModel.updateBackground()
                                    },
                                )
                                Text(
                                    stringResource(R.string.label_settings_background),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                            )
                        }

                        DividerTransparent()

                        LazyColumn(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            itemsIndexed((1..4).toList()) { index, item ->
                                if (index == 0) {
                                    Text(
                                        stringResource(R.string.label_full_icon_header),
                                        modifier = Modifier.padding(
                                            start = 8.dp,
                                            end = 8.dp,
                                            bottom = 8.dp
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp))
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
                                            //.padding(start = 8.dp)
                                            .clip(CircleShape)
                                            .size(24.dp),
                                        contentScale = ContentScale.Inside,
                                        colorFilter = when {
                                            state.defaultTypeList == index + 1 -> null

                                            else -> ColorFilter.tint(
                                                Color.Gray,
                                                blendMode = BlendMode.SrcIn
                                            )
                                        }
                                    )
                                    Text(
                                        text = stringResource(label), modifier = Modifier
                                            .padding(start = 8.dp),
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            //   lineHeight = 14.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AboutScreen()
                     }
                }
            }
        }
    }
}