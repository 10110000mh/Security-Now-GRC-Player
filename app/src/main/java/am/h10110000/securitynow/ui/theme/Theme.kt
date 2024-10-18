package am.h10110000.securitynow.ui.theme
import androidx.compose.material3.lightColorScheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

enum class AppTheme {
    PROGRAMMER_DARK,
    LIGHT_BLUE,
    MONO
}

@Composable
fun PodcastPlayerTheme(
    theme: AppTheme = AppTheme.PROGRAMMER_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.PROGRAMMER_DARK -> darkColorScheme(
            primary = ProgrammerPrimary,
            background = ProgrammerBackground,
            surface = ProgrammerSurface
        )
        AppTheme.LIGHT_BLUE -> lightColorScheme(
            primary = LightBluePrimary,
            background = LightBlueBackground,
            surface = LightBlueSurface
        )
        AppTheme.MONO -> darkColorScheme(
            primary = MonoPrimary,
            background = MonoBackground,
            surface = MonoSurface
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}