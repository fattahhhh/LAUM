package id.ac.pnj.resortcampus.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import id.ac.pnj.resortcampus.data.Badges
import id.ac.pnj.resortcampus.data.LeaderboardEntry
import id.ac.pnj.resortcampus.data.Period
import id.ac.pnj.resortcampus.ui.LeaderboardViewModel
import id.ac.pnj.resortcampus.ui.ProfileViewModel
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.AppCard
import id.ac.pnj.resortcampus.ui.components.Chip
import id.ac.pnj.resortcampus.ui.components.MainScaffold
import id.ac.pnj.resortcampus.ui.components.ScreenHeader
import id.ac.pnj.resortcampus.ui.components.StatTile
import id.ac.pnj.resortcampus.ui.components.categoryIcon
import id.ac.pnj.resortcampus.ui.components.formatKg
import id.ac.pnj.resortcampus.ui.components.formatRelative
import id.ac.pnj.resortcampus.ui.theme.Bronze
import id.ac.pnj.resortcampus.ui.theme.Danger
import id.ac.pnj.resortcampus.ui.theme.DangerSoft
import id.ac.pnj.resortcampus.ui.theme.Gold
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Mint50
import id.ac.pnj.resortcampus.ui.theme.Muted
import id.ac.pnj.resortcampus.ui.theme.Silver

@Composable
fun LeaderboardScreen(nav: NavHostController) {
    val vm = appViewModel { LeaderboardViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()

    MainScaffold(nav = nav, current = Routes.LEADERBOARD) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenHeader("Leaderboard Jurusan")
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Segmented periode: Minggu / Bulanan / Semester
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Line, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                ) {
                    Period.entries.forEach { p ->
                        val active = p == state.period
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (active) Green900 else Color.Transparent)
                                .clickable { vm.setPeriod(p) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                p.label,
                                color = if (active) Color.White else Muted,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                // Podium 3 teratas sesuai Figma (#1 bar tinggi hijau tua, #2 & #3 hijau muda)
                if (state.entries.size >= 3) {
                    AppCard(Modifier.fillMaxWidth()) {
                        Text(
                            "Podium 3 Besar Jurusan",
                            style = MaterialTheme.typography.labelMedium,
                            color = Muted,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            PodiumColumn(state.entries[1], isFirst = false, barHeight = 112.dp, Modifier.weight(1f))
                            PodiumColumn(state.entries[0], isFirst = true, barHeight = 148.dp, Modifier.weight(1f))
                            PodiumColumn(state.entries[2], isFirst = false, barHeight = 96.dp, Modifier.weight(1f))
                        }
                    }
                }

                // Daftar seluruh jurusan
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.entries.forEach { entry ->
                        val mine = entry.majorId == state.myMajorId
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (mine) GreenSoft else Color.White,
                            border = BorderStroke(if (mine) 1.5.dp else 1.dp, if (mine) Green500 else Line),
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (mine) Green900 else GreenSoft),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "#${entry.rank}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (mine) Color.White else GreenText,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        entry.name + if (mine) " (Jurusanmu)" else "",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Green900,
                                    )
                                    Text("Bidang ${entry.faculty}", style = MaterialTheme.typography.bodySmall, color = Muted)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${entry.points}", style = MaterialTheme.typography.titleMedium, color = Green900, fontWeight = FontWeight.Bold)
                                    Text("poin", style = MaterialTheme.typography.labelSmall, color = Muted)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PodiumColumn(entry: LeaderboardEntry, isFirst: Boolean, barHeight: Dp, modifier: Modifier) {
    val bg = if (isFirst) Green900 else GreenSoft
    val fg = if (isFirst) Color.White else Green900
    val sub = if (isFirst) Color(0xFFB7E4C7) else Muted
    Column(
        modifier
            .height(barHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, if (isFirst) Green900 else GreenBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "#${entry.rank}",
            color = fg,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            entry.name,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Text("${entry.points} poin", style = MaterialTheme.typography.labelSmall, color = sub)
    }
}

// ---------------- Profil ----------------

@Composable
fun ProfileScreen(nav: NavHostController, onLoggedOut: () -> Unit) {
    val vm = appViewModel { ProfileViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    val user = state.user?.user
    val major = state.user?.major
    val level = (state.stats.items / 5) + 1

    MainScaffold(nav = nav, current = Routes.PROFILE) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenHeader(
                title = "Profil",
                actions = {
                    Surface(
                        onClick = { nav.navigate(Routes.SETTINGS) },
                        shape = CircleShape,
                        color = GreenSoft,
                        border = BorderStroke(1.dp, GreenBorder),
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Text(
                            "Setting",
                            style = MaterialTheme.typography.labelMedium,
                            color = GreenText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        )
                    }
                },
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Kartu identitas
                AppCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(GreenSoft)
                                .border(1.5.dp, GreenBorder, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                user?.name?.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.headlineSmall,
                                color = GreenText,
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user?.name ?: "-", style = MaterialTheme.typography.titleLarge, color = Green900)
                            Text(user?.email ?: "-", style = MaterialTheme.typography.bodySmall, color = Muted)
                            major?.let {
                                Spacer(Modifier.height(4.dp))
                                Chip(it.name)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile("${state.stats.items}", "Sampah", Icons.Filled.Recycling, Modifier.weight(1f))
                        StatTile(formatKg(state.stats.co2eKg), "Est. CO2e", Icons.Filled.CloudOff, Modifier.weight(1f))
                        StatTile("Lv. $level", "Level", Icons.Filled.EmojiEvents, Modifier.weight(1f))
                    }
                }

                // Koleksi badge
                AppCard(Modifier.fillMaxWidth()) {
                    Text("Koleksi Badge", style = MaterialTheme.typography.titleSmall, color = Green900)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Badges.ALL.forEach { badge ->
                            val earned = state.stats.items >= badge.threshold
                            Column(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (earned) GreenSoft else Mint50)
                                    .border(1.dp, if (earned) GreenBorder else Line, RoundedCornerShape(12.dp))
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(
                                    if (earned) Icons.Filled.Verified else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (earned) GreenText else Muted,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    badge.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (earned) Green900 else Muted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                )
                                Text(
                                    "${badge.threshold}x scan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Muted,
                                )
                            }
                        }
                    }
                }

                // Riwayat kontribusi
                AppCard(Modifier.fillMaxWidth()) {
                    Text("Riwayat Kontribusi", style = MaterialTheme.typography.titleSmall, color = Green900)
                    Spacer(Modifier.height(10.dp))
                    if (state.history.isEmpty()) {
                        Text(
                            "Belum ada sampah terverifikasi. Ketuk tombol kamera di bawah untuk mulai memilah!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.history.take(10).forEach { c ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Mint50)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        Modifier.size(34.dp).clip(CircleShape).background(GreenSoft),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(categoryIcon(c.category), null, tint = GreenText, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(c.itemName, style = MaterialTheme.typography.titleSmall, color = Green900)
                                        Text(formatRelative(c.createdAt), style = MaterialTheme.typography.labelSmall, color = Muted)
                                    }
                                    Text("+${c.points} poin", style = MaterialTheme.typography.labelLarge, color = GreenText)
                                }
                            }
                        }
                    }
                }

                // Info Green IT & privasi (BRIEF bagian 11)
                AppCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, null, tint = Green500, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Data & Green IT", style = MaterialTheme.typography.titleSmall, color = Green900)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "• Foto dikompres di HP sebelum dikirim dan langsung dihapus setelah dianalisis (hanya hash & hasil teks yang disimpan).\n" +
                            "• Status AI: " + if (state.aiConfigured) "Gemini API aktif." else "Mode demo offline (isi GEMINI_API_KEY di local.properties untuk AI nyata).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted,
                    )
                }

                OutlinedButton(
                    onClick = { vm.logout(onLoggedOut) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Danger.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DangerSoft, contentColor = Danger),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Keluar dari Akun", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
