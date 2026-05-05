package ru.gorinih.familyshopper.ui.views

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme
import ru.gorinih.familyshopper.ui.theme.ThemeType
import ru.gorinih.familyshopper.ui.theme.models.PaletteScheme
import ru.gorinih.familyshopper.ui.theme.models.Palettes

/**
 * Created by Igor Abdulganeev on 05.05.2026
 */

@Composable
fun ColorSchemeItems(
    currentPalette: PaletteScheme,
    modifier: Modifier = Modifier,
    paletteList: List<PaletteScheme> = Palettes.palettes,
    onThemeSelected: (PaletteScheme) -> Unit
) {
    val listOfColors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) paletteList
    else paletteList.filterNot { it.themeType == ThemeType.SYSTEM }

    for (scheme in listOfColors) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .selectable(
                    selected = (scheme.themeType == currentPalette.themeType),
                    onClick = { onThemeSelected(scheme) },
                    role = Role.RadioButton
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                 RadioButton(
                    selected = (scheme.themeType == currentPalette.themeType),
                    onClick = null
                )

                Spacer(modifier = Modifier.width(4.dp))

            if (scheme.themeType == ThemeType.SYSTEM) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.label_settings_dynamic_color_text),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.label_settings_dynamic_color_description),
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = TextUnit.Unspecified),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }
                }
            } else ColorsBarItem(scheme)
        }
    }
}

@Composable
fun ColorsBarItem(
    palette: PaletteScheme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .weight(0.2f)
                .height(24.dp)
                .background(color = palette.lightPrimary)
        )
        Box(
            modifier = Modifier
                .weight(0.2f)
                .height(24.dp)
                .background(color = palette.darkPrimary)
        )
        Box(
            modifier = Modifier
                .weight(0.2f)
                .height(24.dp)
                .background(color = palette.secondary)
        )
        Box(
            modifier = Modifier
                .weight(0.2f)
                .height(24.dp)
                .background(color = palette.tertiary)
        )
    }
}

@Preview
@Composable
fun PreviewColorsBarItem() {
    FamilyShopperTheme() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Palettes.palettes.forEach {
                ColorsBarItem(
                    it, modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }

        }
    }
}