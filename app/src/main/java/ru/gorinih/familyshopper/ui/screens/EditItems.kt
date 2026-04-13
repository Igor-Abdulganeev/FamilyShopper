package ru.gorinih.familyshopper.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * элементы ввода и редактирования текста
 */

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    isEditable: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    action: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = if (label.isNotEmpty()) {
            {
                Text(
                    text = label,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
                )
            }
        } else null,
        placeholder = if (placeholder.isNotEmpty()) {
            {
                Text(
                    text = placeholder,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
                )
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = OutlinedTextFieldDefaults.colors(

            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,

            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,

            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,

            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        ),
        readOnly = !isEditable,
        enabled = isEditable,
        trailingIcon = trailingIcon,
        /*
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
        */
        keyboardActions = when (action) {
            null -> KeyboardActions.Default
            else -> KeyboardActions(
                onDone = {
                    action()
                }
            )
        }
    )
}

@Composable
fun BracketTextField(
    modifier: Modifier = Modifier,
    comment: String = "",
    emptyText: String = "...",
    startBracket: Char = '<',
    endBracket: Char = '>',
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val width = when (comment.isBlank()) {
        true -> 50.dp
        false -> 200.dp
    }
    val progress by animateFloatAsState(
        targetValue = if (width == 50.dp) 0f else 1f,
        animationSpec = tween(durationMillis = 250)
    )
    Row(
        modifier = modifier.
            padding(end = 8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(10)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = startBracket.toString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 2.dp)
        )
        TextField(
            value = comment,
            onValueChange = { str ->
                onChange(str)
            },
            modifier = Modifier.width(
                when (width) {
                    50.dp -> when {
                        progress * 200.dp > 50.dp -> progress * 200.dp
                        else -> 50.dp
                    }

                    else -> when {
                        progress * width < 50.dp -> 50.dp
                        else -> progress * width
                    }
                }
            ),
            placeholder = { Text(text = emptyText) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = backgroundColor,

                focusedIndicatorColor = backgroundColor,
                unfocusedIndicatorColor = backgroundColor,

                ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        )
        Text(
            text = endBracket.toString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 2.dp)
        )
    }
}

@Preview(showSystemUi = false)
@Composable
fun PreviewRoundedTextField() {
    FamilyShopperTheme {
        Column(Modifier.padding(8.dp)) {
            RoundedTextField(
                value = "",
                onValueChange = {},
                placeholder = "тестовый",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BracketTextField(
                comment = "",
                emptyText = "...",
                startBracket = '<',
                endBracket = '>',
                modifier = Modifier,
                onChange = {}
            )
        }
    }
}

