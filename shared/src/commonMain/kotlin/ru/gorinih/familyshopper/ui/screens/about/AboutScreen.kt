package ru.gorinih.familyshopper.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.about_code_description
import familyshopper.shared.generated.resources.about_code_name
import familyshopper.shared.generated.resources.about_code_url
import familyshopper.shared.generated.resources.about_copyright
import familyshopper.shared.generated.resources.about_description
import familyshopper.shared.generated.resources.about_license_description
import familyshopper.shared.generated.resources.about_license_name
import familyshopper.shared.generated.resources.about_license_url
import familyshopper.shared.generated.resources.about_version
import familyshopper.shared.generated.resources.app_name
import familyshopper.shared.generated.resources.about_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Created by Igor Abdulganeev on 20.04.2026
 */
@Composable
expect fun rememberAppVersion(): String

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val appVersion = rememberAppVersion()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Иконка приложения
        Image(
            painter = painterResource(resource = Res.drawable.about_icon),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Название приложения
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Версия приложения
        Text(
            text = stringResource(Res.string.about_version, appVersion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Описание приложения
        Text(
            text = stringResource(
                Res.string.about_description,
                stringResource(Res.string.app_name)
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Лицензия
        Text(
            text = buildAnnotatedString {
                append(stringResource(Res.string.about_license_description))
                withLink(
                    LinkAnnotation.Url(
                        url = stringResource(Res.string.about_license_url),
                        styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
                    )
                ) {
                    append(stringResource(Res.string.about_license_name))
                }
                append(".")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ссылка на GitHub
        Text(
            text = buildAnnotatedString {
                append(stringResource(Res.string.about_code_description))
                withLink(
                    LinkAnnotation.Url(
                        url = stringResource(Res.string.about_code_url),
                        styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
                    )
                ) {
                    append(stringResource(Res.string.about_code_name))
                }
                append(".")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        // информация об авторе или годе выпуска
        Text(
            text = stringResource(Res.string.about_copyright),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}