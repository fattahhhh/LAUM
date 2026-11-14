package id.ac.pnj.resortcampus.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Design tokens dari Figma (BRIEF bagian 12)
val Green900 = Color(0xFF1C4734)
val Green950 = Color(0xFF173D2E)
val Green800 = Color(0xFF1E523B)
val Green700 = Color(0xFF28684B)
val Green500 = Color(0xFF4DB081)
val Mint50 = Color(0xFFF6FBF7)
val BgCream = Color(0xFFF6FBF7)
val Turquoise = Color(0xFF3EDBD4)

val GreenSoft = Color(0xFFE8F5EE)
val GreenBorder = Color(0xFFB7E4C7)
val BorderSoft = Color(0xFFDCEADE)
val GreenText = Color(0xFF1C6A38)
val Line = Color(0xFFDCEADE)
val Muted = Color(0xFF5A7A6E)
val TextPrimary = Color(0xFF1C4734)
val TextSecondary = Color(0xFF3A5A4E)
val TextMuted = Color(0xFF5A7A6E)
val Danger = Color(0xFFB3452E)
val DangerSoft = Color(0xFFFDECE7)
val Amber = Color(0xFFE8A33D)
val Amber500 = Color(0xFFE8A33D)
val Gold = Color(0xFFE0B100)
val Silver = Color(0xFF9EA7AD)
val Bronze = Color(0xFFCD7F32)

private val ColorScheme = lightColorScheme(
    primary = Green900,
    onPrimary = Color.White,
    secondary = Green500,
    onSecondary = Color.White,
    tertiary = Turquoise,
    background = Mint50,
    onBackground = Green900,
    surface = Color.White,
    onSurface = Green900,
    surfaceVariant = GreenSoft,
    onSurfaceVariant = Muted,
    outline = Line,
    error = Danger,
    errorContainer = DangerSoft,
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun ReSortTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
