package id.ac.pnj.resortcampus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.navigateTab
import id.ac.pnj.resortcampus.ui.screens.ScanMode
import id.ac.pnj.resortcampus.ui.theme.BgCream
import id.ac.pnj.resortcampus.ui.theme.Green700
import id.ac.pnj.resortcampus.ui.theme.Green800
import id.ac.pnj.resortcampus.util.tr

enum class BottomTab { HOME, BINS, LEADERBOARD, PROFILE }

@Composable
fun MainScaffold(
    nav: NavHostController,
    current: String = Routes.HOME,
    scanMode: ScanMode = ScanMode.WASTE,
    scanLabel: String = "Scan",
    onHomeClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val currentTab = when (current) {
        Routes.HOME -> BottomTab.HOME
        Routes.BINS -> BottomTab.BINS
        Routes.LEADERBOARD -> BottomTab.LEADERBOARD
        Routes.PROFILE -> BottomTab.PROFILE
        else -> BottomTab.HOME
    }

    Scaffold(
        containerColor = BgCream,
        bottomBar = {
            BottomBar(
                current = currentTab,
                onSelect = { tab ->
                    if (tab == BottomTab.HOME && onHomeClick != null) {
                        onHomeClick()
                        return@BottomBar
                    }
                    val target = when (tab) {
                        BottomTab.HOME -> Routes.HOME
                        BottomTab.BINS -> Routes.BINS
                        BottomTab.LEADERBOARD -> Routes.LEADERBOARD
                        BottomTab.PROFILE -> Routes.PROFILE
                    }
                    if (current != target) {
                        nav.navigateTab(target)
                    }
                },
                onScan = { nav.navigate(Routes.scan(scanMode)) },
                scanLabel = scanLabel,
            )
        },
        content = content,
    )
}

@Composable
fun BottomBar(
    current: BottomTab?,
    onSelect: (BottomTab) -> Unit,
    onScan: () -> Unit,
    scanLabel: String = "Scan",
) {
    val homeSelected = current == BottomTab.HOME || current == BottomTab.BINS || current == BottomTab.LEADERBOARD
    val profileSelected = current == BottomTab.PROFILE
    val resolvedScanLabel = when (scanLabel) {
        "Scan Tong" -> tr("Scan Tong", "Scan Bin")
        else -> scanLabel
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // Dark green bottom bar background matching mockup
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
                .background(Color(0xFF1D4433)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                NavPillButton(
                    label = "Home",
                    selected = homeSelected,
                    onClick = { onSelect(BottomTab.HOME) },
                )

                // Space reserved for center floating Scan card
                Spacer(Modifier.width(140.dp))

                NavPillButton(
                    label = tr("Profil", "Profile"),
                    selected = profileSelected,
                    onClick = { onSelect(BottomTab.PROFILE) },
                )
            }
        }

        // Tombol Scan sesuai referensi: alas mint lebar di belakang + kartu hijau tua berbingkai gelap + kilau cahaya lewat
        val scanInteraction = remember { MutableInteractionSource() }
        val scanPressed by scanInteraction.collectIsPressedAsState()
        val scanScale by animateFloatAsState(
            targetValue = if (scanPressed) 0.94f else 1f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
            label = "scanPress",
        )

        Box(
            modifier = Modifier
                .offset(y = (-10).dp)
                .width(150.dp)
                .height(104.dp)
                .graphicsLayer {
                    scaleX = scanScale
                    scaleY = scanScale
                }
                .clickable(
                    interactionSource = scanInteraction,
                    indication = null,
                    onClick = onScan,
                ),
        ) {
            // Alas mint (lebih lebar & lebih pendek dari kartu)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(66.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFD6E6DC)),
            )

            // Kartu Scan
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .width(120.dp)
                    .height(90.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF333333))
                    .padding(4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C4733))
                    .shimmerSweep(alpha = 0.34f, durationMillis = 2000, pauseMillis = 700),
                contentAlignment = Alignment.Center,
            ) {
                // Dua garis diagonal lebar (efek kilap)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val stripe = Color(0xFF5E7A6E)
                    drawPath(
                        Path().apply {
                            moveTo(w * 0.47f, 0f)
                            lineTo(w * 0.63f, 0f)
                            lineTo(0f, h * 0.90f)
                            lineTo(0f, h * 0.68f)
                            close()
                        },
                        color = stripe,
                    )
                    drawPath(
                        Path().apply {
                            moveTo(w * 0.72f, 0f)
                            lineTo(w, 0f)
                            lineTo(w, h * 0.43f)
                            lineTo(w * 0.61f, h)
                            lineTo(w * 0.12f, h)
                            close()
                        },
                        color = stripe,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = resolvedScanLabel,
                        color = Color.White,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = if (resolvedScanLabel.length > 5) 19.sp else 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.35f),
                                offset = Offset(0f, 4f),
                                blurRadius = 8f,
                            ),
                        ),
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE)),
                    )
                }
            }
        }
    }
}

@Composable
private fun NavPillButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (selected) Color(0xFFD6E6DC) else Color(0xFFC7DBD0)
    val textColor = if (selected) Green800 else Green700

    Box(
        modifier = Modifier
            .width(92.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
    }
}
