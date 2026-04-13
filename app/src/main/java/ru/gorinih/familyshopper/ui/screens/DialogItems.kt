package ru.gorinih.familyshopper.ui.screens

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
import androidx.compose.ui.unit.dp

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
        onDismissRequest = {  onDismiss()},
        title = { Text("Ошибка") },
        text = {Text(text = errorText)},
        confirmButton = {
            Button(onClick = { onDismiss() }) { Text("OK") }
        }
    )
}
