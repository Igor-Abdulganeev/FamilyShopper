package ru.gorinih.familyshopper.ui.views

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.DialogProperties
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * диалоговые и прочие элементы взаимодействия
 */

@Composable
fun ProgressLoadingOverlay(
    color: Color = Color.Black
) {
    // Box на весь экран
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color.copy(alpha = 0.3f)) // Затенение на 40% черного
            .pointerInput(Unit) {}, // блок нажатий по элементам под ним
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun ErrorDialog(
    errorText: String,
    modifier: Modifier = Modifier,
    titleText: String = stringResource(R.string.error_title_text),
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = modifier.shadow(alphaShadowLight = 0.3f, alphaShadowDark = 0.2f),
        onDismissRequest = onDismiss,

        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        icon = {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(32.dp)
            )
        },
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 2.em
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.button_text_ok))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconContentColor = MaterialTheme.colorScheme.error

    )
}

@Composable
fun QueryDialog(
    text: String,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.warning_caption_text),
    positiveButtonText: String = stringResource(R.string.button_text_positive),
    negativeButtonText: String = stringResource(R.string.button_text_negative),
    icon: ImageVector = Icons.Default.WarningAmber,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        modifier = modifier.shadow(alphaShadowLight = 0.3f, alphaShadowDark = 0.2f),
        onDismissRequest = onCancel,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 2.em
            )
        },
        confirmButton = {
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(positiveButtonText)
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(negativeButtonText)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconContentColor = MaterialTheme.colorScheme.secondary
    )
}

@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PreviewDayQueryDialog() {
    FamilyShopperTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            QueryDialog(
                text = "Вы действительно ходите удалить список?",
                onDone = {},
                onCancel = {}
            )
        }
    }
}
@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNightQueryDialog(){
    FamilyShopperTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            QueryDialog(
                text = "Вы действительно ходите удалить список?",
                onDone = {},
                onCancel = {}
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PreviewDayErrorDialog() {
    FamilyShopperTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            ErrorDialog(
                errorText = "Ошибка обновления, проверьте сетевое соединение",
                onDismiss = {}
                )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNightErrorDialog() {
    FamilyShopperTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            ErrorDialog(
                errorText = "Ошибка обновления, проверьте сетевое соединение",
                onDismiss = {}
                )
        }
    }
}