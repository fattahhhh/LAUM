package id.ac.pnj.resortcampus.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.pnj.resortcampus.data.Points
import id.ac.pnj.resortcampus.data.RewardInfo
import id.ac.pnj.resortcampus.data.ScanAnalysis
import id.ac.pnj.resortcampus.util.ImageUtils
import id.ac.pnj.resortcampus.util.tr
import id.ac.pnj.resortcampus.ui.BinState
import id.ac.pnj.resortcampus.ui.ScanFlowViewModel
import id.ac.pnj.resortcampus.ui.WasteState
import id.ac.pnj.resortcampus.ui.components.Chip
import id.ac.pnj.resortcampus.ui.components.ErrorState
import id.ac.pnj.resortcampus.ui.components.LoadingState
import id.ac.pnj.resortcampus.ui.components.PrimaryButton
import id.ac.pnj.resortcampus.ui.components.SecondaryButton
import id.ac.pnj.resortcampus.ui.components.binColor
import id.ac.pnj.resortcampus.ui.components.binIcon
import id.ac.pnj.resortcampus.ui.components.binLabel
import id.ac.pnj.resortcampus.ui.components.capitalized
import id.ac.pnj.resortcampus.ui.components.formatKg
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.Green950
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Mint50
import id.ac.pnj.resortcampus.ui.theme.Muted
import kotlin.math.roundToInt

// ==========================================
// 1. LAYAR HASIL DETEKSI AI (UI • Hasil di Figma)
// ==========================================
@Composable
fun ResultScreen(
    scanFlow: ScanFlowViewModel,
    onRetake: () -> Unit,
    onGoToBin: () -> Unit,
    onSkip: () -> Unit,
) {
    val state by scanFlow.waste.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(Green950)) {
        // Header gelap "AI Scan" sesuai Figma
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = onRetake,
                modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.14f)),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Kembali", "Back"), tint = Color.White)
            }
            Text("AI Scan", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(38.dp))
        }

        // Lembar putih melengkung di bawah (bottom sheet style sesuai Figma)
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
        ) {
            when (val s = state) {
                WasteState.Idle, WasteState.Analyzing -> {
                    LoadingState(
                        tr("AI sedang menganalisis foto...", "AI is analyzing your photo..."),
                        tr("Mengenali jenis sampah & cara buangnya", "Identifying waste type & disposal method"),
                    )
                }
                is WasteState.Failed -> {
                    ErrorState(s.message, tr("Foto Ulang", "Retake Photo"), onRetake)
                }
                is WasteState.Done -> {
                    ResultBody(s.analysis, onRetake = onRetake, onGoToBin = onGoToBin, onSkip = onSkip)
                }
            }
        }
    }
}

@Composable
private fun ResultBody(
    analysis: ScanAnalysis,
    onRetake: () -> Unit,
    onGoToBin: () -> Unit,
    onSkip: () -> Unit,
) {
    val r = analysis.result
    val valid = analysis.scanSessionId != null
    val rule = Points.ruleFor(r.category)
    val pct = (r.confidence * 100).roundToInt()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Chip status di atas
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(
                if (valid) {
                    tr("Sampah Terdeteksi • $pct% akurat", "Waste Detected • $pct% accurate")
                } else {
                    tr("Perlu Foto Ulang ($pct%)", "Retake Needed ($pct%)")
                },
                danger = !valid,
            )
            if (analysis.usedMock) Chip(tr("Mode Demo", "Demo Mode"))
        }

        // Tampilkan foto asli hasil CameraX ImageCapture jika ada, atau ilustrasi lingkaran daur ulang sesuai Figma
        val context = LocalContext.current
        val capturedBitmap = remember(analysis.photoUri) {
            analysis.photoUri?.let { uriStr ->
                ImageUtils.decodeOrientedBitmap(context, Uri.parse(uriStr))
            }
        }
        if (capturedBitmap != null) {
            Box(
                Modifier
                    .size(176.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.5.dp, Green500, RoundedCornerShape(24.dp)),
            ) {
                Image(
                    bitmap = capturedBitmap.asImageBitmap(),
                    contentDescription = "Foto Hasil CameraX",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            RecycleBottleIllustration(modifier = Modifier.size(132.dp))
        }

        // Nama sampah & kategori
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                r.itemName,
                style = MaterialTheme.typography.headlineSmall,
                color = Green900,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                tr(
                    "Kategori: ${r.category.capitalized()} • Material: ${r.material} • +${rule.points} poin",
                    "Category: ${r.category.capitalized()} • Material: ${r.material} • +${rule.points} pts",
                ),
                style = MaterialTheme.typography.bodySmall,
                color = Muted,
                textAlign = TextAlign.Center,
            )
        }

        // Tombol utama sesuai posisi di Figma
        if (valid) {
            PrimaryButton(
                text = tr("Pilih lokasi tong sampah", "Choose waste bin location"),
                onClick = onGoToBin,
                icon = Icons.Filled.Place,
                modifier = Modifier.fillMaxWidth(),
                shimmer = true,
            )
            TextButton(onClick = onSkip) {
                Text(
                    tr("Saya hanya memeriksa saja (tanpa poin)", "Just checking (no points)"),
                    color = Muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        } else {
            PrimaryButton(
                text = tr("Foto Ulang Sampah", "Retake Waste Photo"),
                onClick = onRetake,
                icon = Icons.Filled.PhotoCamera,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        HorizontalDivider(color = Line)

        // Instruksi cara buang & tips
        val bColor = binColor(r.binType)
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Mint50)
                .border(1.dp, Line, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                Modifier.size(38.dp).clip(CircleShape).background(bColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(binIcon(r.binType), null, tint = bColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    tr("Instruksi Pembuangan (Tong ${binLabel(r.binType)})", "Disposal Instructions (${binLabel(r.binType)} Bin)"),
                    style = MaterialTheme.typography.titleSmall,
                    color = Green900,
                )
                Spacer(Modifier.height(2.dp))
                Text(r.disposalInstruction, style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        }

        if (r.tips.isNotBlank()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(GreenSoft)
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Lightbulb, null, tint = GreenText, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(tr("Tips: ${r.tips}", "Tip: ${r.tips}"), style = MaterialTheme.typography.bodySmall, color = GreenText)
            }
        }

        if (valid) {
            SecondaryButton(
                text = tr("Foto Ulang", "Retake Photo"),
                onClick = onRetake,
                icon = Icons.Filled.Refresh,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, null, tint = Muted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                tr("Dianalisis AI, bisa keliru.", "Analyzed by AI, may make mistakes.") +
                    if (analysis.usedMock) tr(" (mode demo offline)", " (offline demo mode)") else "",
                style = MaterialTheme.typography.labelSmall,
                color = Muted,
            )
        }
    }
}

// ==========================================
// 2. LAYAR REWARD MODAL (UI • Reward di Figma)
// ==========================================
@Composable
fun RewardScreen(
    scanFlow: ScanFlowViewModel,
    onDone: () -> Unit,
    onScanAgain: () -> Unit,
    onRetryBin: () -> Unit,
) {
    val state by scanFlow.bin.collectAsStateWithLifecycle()

    // Background hijau tua dengan modal putih melayang di tengah sesuai Figma
    Box(
        Modifier
            .fillMaxSize()
            .background(Green950)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp,
        ) {
            when (val s = state) {
                BinState.Idle, BinState.Verifying -> {
                    Box(Modifier.height(340.dp)) {
                        LoadingState(
                            tr("Memverifikasi tong sampah...", "Verifying waste bin..."),
                            tr("Mencatat poin untuk jurusanmu", "Recording points for your major"),
                        )
                    }
                }
                is BinState.Failed -> {
                    Box(Modifier.height(340.dp)) {
                        ErrorState(s.message, tr("Foto Ulang Tong", "Retake Bin Photo"), onRetryBin)
                    }
                }
                is BinState.Rewarded -> {
                    RewardBody(s.reward, onDone = onDone, onScanAgain = onScanAgain)
                }
            }
        }
    }
}

@Composable
private fun RewardBody(reward: RewardInfo, onDone: () -> Unit, onScanAgain: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Chip(tr("Verifikasi Berhasil", "Verification Succeeded"))

        val context = LocalContext.current
        val capturedBinBitmap = remember(reward.photoUri) {
            reward.photoUri?.let { uriStr ->
                ImageUtils.decodeOrientedBitmap(context, Uri.parse(uriStr))
            }
        }
        if (capturedBinBitmap != null) {
            Box(
                Modifier
                    .size(136.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(2.5.dp, Green500, RoundedCornerShape(22.dp)),
            ) {
                Image(
                    bitmap = capturedBinBitmap.asImageBitmap(),
                    contentDescription = "Foto Tong Hasil CameraX",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            RecycleBottleIllustration(modifier = Modifier.size(116.dp))
        }

        Text(
            tr("Terimakasih, pahlawan kampus!", "Thank you, campus hero!"),
            style = MaterialTheme.typography.titleLarge,
            color = Green900,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Text(
            tr(
                "${reward.itemName} (${reward.category.capitalized()}) berhasil diverifikasi dan poin sudah ditambahkan ke jurusanmu.",
                "${reward.itemName} (${reward.category.capitalized()}) was verified and points have been added to your major.",
            ),
            style = MaterialTheme.typography.bodySmall,
            color = Muted,
            textAlign = TextAlign.Center,
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RewardMetric(
                value = tr("+${reward.points} poin", "+${reward.points} pts"),
                label = tr("Poin jurusan", "Major points"),
                modifier = Modifier.weight(1f),
            )
            RewardMetric(
                value = formatKg(reward.co2eKg),
                label = tr("CO2e (estimasi)", "CO2e (est.)"),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Filled.Eco, null, tint = Muted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(tr("Estimasi penghematan emisi karbon", "Estimated carbon emission savings"), style = MaterialTheme.typography.labelSmall, color = Muted)
        }

        Spacer(Modifier.height(4.dp))

        PrimaryButton(
            text = tr("Koleksi poin!", "Collect points!"),
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            shimmer = true,
        )
        TextButton(onClick = onScanAgain) {
            Text(tr("Scan sampah lainnya", "Scan another item"), color = GreenText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RewardMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GreenSoft)
            .border(1.dp, GreenBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = GreenText, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = GreenText, textAlign = TextAlign.Center)
    }
}
