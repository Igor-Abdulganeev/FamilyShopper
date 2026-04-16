package ru.gorinih.familyshopper.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.gorinih.familyshopper.R

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
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
        title = { Text("Ошибка") },
        text = { Text(text = errorText) },
        confirmButton = {
            Button(onClick = { onDismiss() }) { Text("OK") }
        }
    )
}

@Composable
fun QueryDialog(
    question: String,
    modifier: Modifier = Modifier,
    titleId: Int = R.string.warning_caption_text,
    positiveButtonTextId: Int = R.string.button_text_positive,
    negativeButtonTextId: Int = R.string.button_text_negative,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onCancel() },
        title = { Text(stringResource(titleId)) },
        text = { Text(text = question) },
        confirmButton = {
            Button(onClick = { onDone() }) { Text(stringResource(positiveButtonTextId)) }
        },
        dismissButton = {
            Button(onClick = { onCancel() }) { Text(stringResource(negativeButtonTextId)) }
        }
    )
}
