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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import id.ac.pnj.resortcampus.data.Points
import id.ac.pnj.resortcampus.ui.HomeViewModel
import id.ac.pnj.resortcampus.ui.PendingScan
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.ScanFlowViewModel
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.AppCard
import id.ac.pnj.resortcampus.ui.components.MainScaffold
import id.ac.pnj.resortcampus.ui.components.PrimaryButton
import id.ac.pnj.resortcampus.ui.components.binLabel
import id.ac.pnj.resortcampus.ui.navigateTab
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.Green950
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Muted
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(nav: NavHostController, scanFlow: ScanFlowViewModel) {
    val vm = appViewModel { HomeViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    val pending by scanFlow.pending.collectAsStateWithLifecycle()
    val name = state.user?.user?.name ?: "Mahasiswa"
    val majorName = state.user?.major?.name ?: "Teknik Informatika"

    MainScaffold(nav = nav, current = Routes.HOME) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header atas sesuai Figma: "Hai, Nama user" + jurusan di kiri, Setting + Avatar di kanan
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Hai, $name",
                        style = MaterialTheme.typography.titleLarge,
                        color = Green900,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        majorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IconButton(
                        onClick = { nav.navigate(Routes.SETTINGS) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GreenSoft),
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Setting", tint = Green900, modifier = Modifier.size(18.dp))
                    }
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Green950)
                            .border(1.5.dp, Green500, CircleShape)
                            .clickable { nav.navigateTab(Routes.PROFILE) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            name.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Banner jika ada sampah yang belum diverifikasi ke tong
            pending?.let { p ->
                PendingBanner(
                    pending = p,
                    onVerify = { nav.navigate(Routes.scan(ScanMode.BIN)) },
                    onSeeBins = { nav.navigateTab(Routes.BINS) },
                )
            }

            // Hero Card Hijau Tua sesuai Figma ("Pilah sampahmu, Hijaukan kampus" + ilustrasi botol di kanan)
            // Hero card: teks + satu tombol di kiri, ilustrasi botol di kanan
            Surface(shape = MaterialTheme.shapes.large, color = Green900) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Ayo mulai pilah hari ini", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB7E4C7))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Pilah sampahmu, angkat jurusanmu.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Foto sampah, buang ke tong yang tepat, lalu kumpulkan poin untuk jurusanmu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA9C9BB),
                        )
                        Spacer(Modifier.height(14.dp))
                        PrimaryButton(
                            text = "Scan Sampah",
                            icon = Icons.Filled.PhotoCamera,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { nav.navigate(Routes.scan(ScanMode.WASTE)) }
                        )
                        Text(
                            "Lihat peta tong →",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFB7E4C7),
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .clickable { nav.navigateTab(Routes.BINS) },
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    RecycleBottleIllustration(Modifier.size(96.dp))
                }
            }

            // Kartu Peringkat Jurusan sesuai Figma (#rank + nama jurusan + tombol lihat leaderboard)
            val majorEntry = state.myMajor
            val majorPoints = majorEntry?.points ?: 0
            val progress = (majorPoints.toFloat() / Points.WEEKLY_MAJOR_TARGET).coerceIn(0f, 1f)
            val numFmt = NumberFormat.getIntegerInstance(Locale("id", "ID"))

            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { nav.navigateTab(Routes.LEADERBOARD) },
            ) {
                Text(
                    "Peringkat jurusanmu",
                    style = MaterialTheme.typography.labelMedium,
                    color = Muted,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            majorEntry?.let { "#${it.rank}" } ?: "#1",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Green900,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                majorEntry?.name ?: majorName,
                                style = MaterialTheme.typography.titleSmall,
                                color = Green900,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "${numFmt.format(majorPoints)} poin minggu ini",
                                style = MaterialTheme.typography.labelSmall,
                                color = Muted,
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Leaderboard", style = MaterialTheme.typography.labelMedium, color = GreenText, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = GreenText, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Bagian "Kontribusi hari ini" (2 kartu berdampingan persis Figma: Streak & Poin)
            Text(
                "Kontribusi hari ini",
                style = MaterialTheme.typography.titleSmall,
                color = Green900,
                fontWeight = FontWeight.Bold,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppCard(Modifier.weight(1f)) {
                    Text("Streak aktif", style = MaterialTheme.typography.labelSmall, color = Muted)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${state.streak} hari",
                        style = MaterialTheme.typography.titleLarge,
                        color = Green900,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${state.stats.items} sampah terverifikasi",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenText,
                    )
                }
                AppCard(Modifier.weight(1f)) {
                    Text("Total poin jurusan", style = MaterialTheme.typography.labelSmall, color = Muted)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        numFmt.format(majorPoints),
                        style = MaterialTheme.typography.titleLarge,
                        color = Green900,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "+${state.stats.points} poin dari kamu",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenText,
                    )
                }
            }

            // Kartu Progress Target Mingguan sesuai Figma
            AppCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Target mingguan jurusan",
                        style = MaterialTheme.typography.titleSmall,
                        color = Green900,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${numFmt.format(majorPoints)} / ${numFmt.format(Points.WEEKLY_MAJOR_TARGET)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreenText,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = Green500,
                    trackColor = Line,
                )
            }
        }
    }
}

@Composable
private fun PendingBanner(pending: PendingScan, onVerify: () -> Unit, onSeeBins: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = GreenSoft,
        border = BorderStroke(1.dp, GreenBorder),
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
                "Menunggu verifikasi tong: ${pending.itemName}",
                style = MaterialTheme.typography.titleSmall,
                color = GreenText,
            )
            Text(
                "Buang ke tong ${binLabel(pending.binType)}, lalu foto tongnya untuk klaim poin.",
                style = MaterialTheme.typography.bodySmall,
                color = GreenText.copy(alpha = 0.85f),
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton("Scan Tong Sekarang", onVerify, Modifier.weight(1f), icon = Icons.Filled.PhotoCamera)
                TextButton(onClick = onSeeBins) { Text("Lihat Peta", color = GreenText) }
            }
        }
    }
}
