package ru.gorinih.familyshopper.ui.views

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.times
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * элементы ввода и редактирования текста
 */

@Composable
fun GlowRoundedTextField(
    isGlow: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    colorGlow: Color = MaterialTheme.colorScheme.primary,
    label: String = "",
    placeholder: String = "",
    isEditable: Boolean = true,
    isSingleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    action: (() -> Unit)? = null,
    onClearCurrentField: (() -> Unit)? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulsar")
    val waveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val waveRadius = lerp(start = 2.dp, stop = 6.dp, fraction = waveProgress)
    val glowVisibility by animateFloatAsState(
        targetValue = if (isGlow) 1f else 0f,
        animationSpec = tween(durationMillis = 100),
        label = "glowVisibility"
    )

    val targetGlow = waveRadius * glowVisibility
     Box(
        modifier = modifier.fillMaxWidth().glowWave(
            color = colorGlow,
            glowRadius = targetGlow,
            shapeRadius = 12.dp
        )
    )
    {
        RoundedTextField(
            value,
            onValueChange,
            modifier,
            label,
            placeholder,
            isEditable,
            isSingleLine,
            trailingIcon,
            leadingIcon,
            action,
            onClearCurrentField,
        )
    }
}

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    isEditable: Boolean = true,
    isSingleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    action: (() -> Unit)? = null,
    onClearCurrentField: (() -> Unit)? = null,
) {

    var internalValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }

    LaunchedEffect(value) {
        if (value != internalValue.text) {
            internalValue = internalValue.copy(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    OutlinedTextField(
        value = internalValue,
        onValueChange = {
            internalValue = it
            if (value != it.text) onValueChange(it.text)
            onClearCurrentField?.invoke()
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        label = if (label.isNotEmpty()) {
            {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else null,
        placeholder = if (placeholder.isNotEmpty()) {
            {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else null,
        singleLine = isSingleLine,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
            .onFocusChanged { focus ->
                if (focus.isFocused) onClearCurrentField?.invoke()
            },
        colors = OutlinedTextFieldDefaults.colors(

            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,

            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,

            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,

            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,

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
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
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
    backgroundColor: Color = Color.Transparent,
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
        modifier = modifier
            .padding(end = 8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(10)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = startBracket.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 2.dp)
        )
        TextField(
            value = comment,
            onValueChange = { str ->
                onChange(str)
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .width(
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
            placeholder = {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = backgroundColor,

                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,

                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

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
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 2.dp)
        )
    }
}

@Preview(showSystemUi = false)
@Composable
fun PreviewRoundedTextField() {
    FamilyShopperTheme {
        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            RoundedTextField(
                value = "",
                onValueChange = {},
                placeholder = "тестовый",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            RoundedTextField(
                value = "текст введен",
                onValueChange = {},
                placeholder = "",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            RoundedTextField(
                value = "текст введен с меткой",
                label = "Метка ввода",
                onValueChange = {},
                placeholder = "",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            RoundedTextField(
                value = "текст с иконками",
                label = "Метка ввода",
                onValueChange = {},
                placeholder = "",
                modifier = Modifier.padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.EditNote
                    )
                },
                trailingIcon = {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.EditNote
                    )
                }
            )
        }
    }
}

@Preview(showSystemUi = false)
@Composable
fun PreviewBracketTextField() {
    FamilyShopperTheme {
        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            BracketTextField(
                comment = "",
                emptyText = "...",
                startBracket = '<',
                endBracket = '>',
                modifier = Modifier,
                onChange = {}
            )
            BracketTextField(
                comment = "данные",
                emptyText = "...",
                startBracket = '<',
                endBracket = '>',
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.secondary,
                onChange = {}
            )
        }
    }
}

@Preview( showBackground = true)
@Composable
fun PreviewGlowRoundedTextField() {
    FamilyShopperTheme() {
        Column(Modifier.fillMaxSize().background(color= MaterialTheme.colorScheme.onBackground)) {
            GlowRoundedTextField(
                isGlow = false,
                value = "без свечения",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
           GlowRoundedTextField(
                isGlow = true,
               colorGlow = Color.Red,
                value = "со свечением",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        }
    }
}

