package id.ac.pnj.resortcampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.ac.pnj.resortcampus.ui.theme.Amber
import id.ac.pnj.resortcampus.ui.theme.Danger
import id.ac.pnj.resortcampus.ui.theme.DangerSoft
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Muted
import java.util.Locale

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, Line),
    ) {
        Column(Modifier.padding(padding), content = content)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.heightIn(min = 52.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Green900, contentColor = Color.White),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            Spacer(Modifier.width(10.dp))
        } else if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 52.dp),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, Green500),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Green900),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun Chip(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, danger: Boolean = false) {
    val bg = if (danger) DangerSoft else GreenSoft
    val fg = if (danger) Danger else GreenText
    Row(
        modifier
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, if (danger) Danger.copy(alpha = 0.3f) else GreenBorder, CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, color = fg, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun ScreenHeader(title: String, onBack: (() -> Unit)? = null, actions: @Composable () -> Unit = {}) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Green900)
            }
        } else {
            Spacer(Modifier.width(16.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = Green900,
            modifier = Modifier.weight(1f),
        )
        actions()
    }
}

@Composable
fun StatTile(value: String, label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(GreenSoft)
            .border(1.dp, GreenBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = GreenText, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, color = GreenText, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = GreenText.copy(alpha = 0.8f))
    }
}

@Composable
fun LoadingState(message: String, sub: String? = null, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = Green500, strokeWidth = 4.dp, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(20.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = Green900, textAlign = TextAlign.Center)
        if (sub != null) {
            Spacer(Modifier.height(4.dp))
            Text(sub, style = MaterialTheme.typography.bodySmall, color = Muted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ErrorState(message: String, actionLabel: String, onAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(DangerSoft),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Filled.Warning, null, tint = Danger, modifier = Modifier.size(32.dp)) }
        Spacer(Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = Green900, textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        PrimaryButton(actionLabel, onAction)
    }
}

@Composable
fun MessageBanner(text: String, isError: Boolean, modifier: Modifier = Modifier) {
    val bg = if (isError) DangerSoft else GreenSoft
    val fg = if (isError) Danger else GreenText
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(if (isError) Icons.Filled.Warning else Icons.Filled.Eco, null, tint = fg, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = fg, style = MaterialTheme.typography.bodySmall)
    }
}

// ---------- Helper kategori & tong ----------

fun categoryIcon(category: String): ImageVector = when (category) {
    "plastik" -> Icons.Filled.LocalDrink
    "kertas" -> Icons.Filled.Description
    "organik" -> Icons.Filled.Eco
    "kaca" -> Icons.Filled.WineBar
    "logam" -> Icons.Filled.Recycling
    "elektronik" -> Icons.Filled.Memory
    else -> Icons.Filled.Delete
}

fun binColor(binType: String): Color = when (binType) {
    "anorganik" -> Color(0xFF2F80ED)
    "organik" -> Green500
    "kertas" -> Amber
    "b3" -> Danger
    else -> Muted
}

fun binLabel(binType: String): String = when (binType) {
    "anorganik" -> "Anorganik"
    "organik" -> "Organik"
    "kertas" -> "Kertas"
    "b3" -> "B3 / Elektronik"
    else -> binType.replaceFirstChar { it.titlecase(Locale.getDefault()) }
}

fun binIcon(binType: String): ImageVector = when (binType) {
    "b3" -> Icons.Filled.Bolt
    "organik" -> Icons.Filled.Eco
    "kertas" -> Icons.Filled.Description
    else -> Icons.Filled.Recycling
}

fun String.capitalized(): String = replaceFirstChar { it.titlecase(Locale.getDefault()) }

fun formatKg(kg: Double): String = String.format(Locale("id", "ID"), "%.2f kg", kg)

fun formatRelative(time: Long, now: Long = System.currentTimeMillis()): String {
    val diff = (now - time).coerceAtLeast(0)
    val min = diff / 60_000
    return when {
        min < 1 -> "Baru saja"
        min < 60 -> "$min menit lalu"
        min < 60 * 24 -> "${min / 60} jam lalu"
        min < 60 * 24 * 7 -> "${min / (60 * 24)} hari lalu"
        else -> java.text.SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(java.util.Date(time))
    }
}
