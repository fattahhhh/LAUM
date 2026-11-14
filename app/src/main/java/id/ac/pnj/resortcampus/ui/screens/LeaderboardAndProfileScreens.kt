package id.ac.pnj.resortcampus.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import id.ac.pnj.resortcampus.data.Badges
import id.ac.pnj.resortcampus.data.LeaderboardEntry
import id.ac.pnj.resortcampus.data.Period
import id.ac.pnj.resortcampus.data.Points
import id.ac.pnj.resortcampus.ui.LeaderboardUiState
import id.ac.pnj.resortcampus.ui.LeaderboardViewModel
import id.ac.pnj.resortcampus.ui.ProfileUiState
import id.ac.pnj.resortcampus.ui.ProfileViewModel
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.MainScaffold
import id.ac.pnj.resortcampus.ui.components.formatNum
import id.ac.pnj.resortcampus.ui.components.formatRelative
import id.ac.pnj.resortcampus.ui.components.shimmerSweep
import id.ac.pnj.resortcampus.ui.theme.BgCream
import id.ac.pnj.resortcampus.ui.theme.BorderSoft
import id.ac.pnj.resortcampus.ui.theme.Danger
import id.ac.pnj.resortcampus.ui.theme.DangerSoft
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green700
import id.ac.pnj.resortcampus.ui.theme.Green800
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.TextMuted
import id.ac.pnj.resortcampus.ui.theme.TextPrimary
import id.ac.pnj.resortcampus.ui.theme.TextSecondary
import id.ac.pnj.resortcampus.util.SettingsStore
import id.ac.pnj.resortcampus.util.tr

private fun shortMajorName(major: String): String {
    val clean = major.trim()
    if (clean.isEmpty()) return "PNJ"
    return when {
        clean.contains("Multimedia Digital", ignoreCase = true) -> "TMD"
        clean.contains("Multimedia dan Jaringan", ignoreCase = true) -> "TMJ"
        clean.contains("Teknik Informatika", ignoreCase = true) -> "Teknik Informatika"
        clean.contains("Teknik Komputer", ignoreCase = true) -> "Teknik Komputer"
        else -> clean
    }
}

private fun periodText(p: Period): String = when (p) {
    Period.WEEK -> tr("Mingguan", "Weekly")
    Period.MONTH -> tr("Bulanan", "Monthly")
    Period.SEMESTER -> tr("Satu Semester", "One Semester")
}

// ============================================================
// LeaderboardScreen (Per-User Ranking with Major, matching Mockup)
// ============================================================
@Composable
fun LeaderboardScreen(
    nav: NavHostController,
    vm: LeaderboardViewModel = appViewModel { LeaderboardViewModel(it) },
) {
    val state by vm.state.collectAsStateWithLifecycle()
    MainScaffold(
        nav = nav,
        current = Routes.LEADERBOARD,
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            LeaderboardScreenContent(
                state = state,
                onPeriodChange = { vm.setPeriod(it) },
            )
        }
    }
}

@Composable
fun LeaderboardScreenContent(
    state: LeaderboardUiState,
    onPeriodChange: (Period) -> Unit,
) {
    val rows = state.entries
    val top1 = rows.getOrNull(0)
    val top2 = rows.getOrNull(1)
    val top3 = rows.getOrNull(2)
    val remaining = if (rows.size > 3) rows.drop(3) else emptyList()

    Column(
        Modifier
            .fillMaxSize()
            .background(BgCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Leaderboard",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
            ),
            color = Green800,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = tr(
                "Kompetisi sehat untuk kampus lebih hijau.",
                "Healthy competition for a greener campus.",
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
        )

        Spacer(Modifier.height(14.dp))

        // Period selector pill tabs: Mingguan | Bulanan | Satu Semester
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE6F2EC))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Period.entries.forEach { p ->
                val active = state.period == p
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) Color(0xFF1D4433) else Color.Transparent)
                        .clickable { onPeriodChange(p) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = periodText(p),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        ),
                        color = if (active) Color.White else Green800,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = tr("Mahasiswa paling berdampak", "Most impactful students"),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            ),
            color = Green800,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(14.dp))

        // Top 3 Podium Cards: #2 (left), #1 (center tall dark green), #3 (right)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            PodiumCard(
                rank = 2,
                row = top2,
                isCenter = false,
                height = 148.dp,
                isCurrentUser = top2 != null && top2.userId.isNotEmpty() && top2.userId == state.myUserId,
                modifier = Modifier.weight(1f),
            )
            PodiumCard(
                rank = 1,
                row = top1,
                isCenter = true,
                height = 182.dp,
                isCurrentUser = top1 != null && top1.userId.isNotEmpty() && top1.userId == state.myUserId,
                modifier = Modifier.weight(1.08f),
            )
            PodiumCard(
                rank = 3,
                row = top3,
                isCenter = false,
                height = 136.dp,
                isCurrentUser = top3 != null && top3.userId.isNotEmpty() && top3.userId == state.myUserId,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(20.dp))

        // Subheader: Peringkat mahasiswa & POIN
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tr("Peringkat mahasiswa", "Student rankings"),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                ),
                color = Green800,
            )
            Text(
                text = tr("POIN", "POINTS"),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                ),
                color = TextMuted,
            )
        }

        Spacer(Modifier.height(10.dp))

        // Ranked user rows (#4, #5, #6, ...)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            remaining.forEachIndexed { idx, row ->
                val rankNum = idx + 4
                val isMe = row.userId.isNotEmpty() && row.userId == state.myUserId
                LeaderboardUserItem(
                    rank = rankNum,
                    row = row,
                    isMe = isMe,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PodiumCard(
    rank: Int,
    row: LeaderboardEntry?,
    isCenter: Boolean,
    height: Dp,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isCenter) Color(0xFF1D4433) else Color(0xFFE3F0E9)
    val rankColor = if (isCenter) Color.White else Green900
    val nameColor = if (isCenter) Color.White else Green900
    val majorColor = if (isCenter) Color(0xFFB7DEC8) else TextSecondary
    val pointsColor = if (isCenter) Color(0xFFD6EFE1) else Green700

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .shimmerSweep(enabled = isCenter, alpha = 0.16f, durationMillis = 2800, pauseMillis = 900)
            .then(
                if (isCurrentUser && !isCenter) {
                    Modifier.border(1.5.dp, Green500, RoundedCornerShape(20.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (isCenter) 30.sp else 26.sp,
                ),
                color = rankColor,
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val youSuffix = tr(" (Kamu)", " (You)")
                Text(
                    text = if (row != null) {
                        if (isCurrentUser) "${row.name}$youSuffix" else row.name
                    } else "-",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    ),
                    color = nameColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (row != null) shortMajorName(row.majorName.ifEmpty { row.faculty }) else "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = majorColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = tr(
                    "${formatNum(row?.points ?: 0)} poin",
                    "${formatNum(row?.points ?: 0)} pts",
                ),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                ),
                color = pointsColor,
            )
        }
    }
}

@Composable
private fun LeaderboardUserItem(
    rank: Int,
    row: LeaderboardEntry,
    isMe: Boolean,
) {
    val bgColor = if (isMe) Color(0xFFDFF1E6) else Color.White
    val borderColor = if (isMe) Color(0xFF7FC49B) else BorderSoft
    val youTag = tr(" (Kamu)", " (You)")

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            ),
            color = if (isMe) Green800 else TextMuted,
            modifier = Modifier.width(36.dp),
        )

        Column(Modifier.weight(1f)) {
            Text(
                text = if (isMe) "${row.name}$youTag" else row.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                ),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = row.majorName.ifEmpty { row.faculty },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = formatNum(row.points),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
            ),
            color = Green900,
        )
    }
}

// ============================================================
// ProfileScreen (Matching Mockup + Photo Picker + Emphasized Current Badge)
// ============================================================
@Composable
fun ProfileScreen(
    nav: NavHostController,
    onLoggedOut: () -> Unit = {},
    vm: ProfileViewModel = appViewModel { ProfileViewModel(it) },
) {
    val state by vm.state.collectAsStateWithLifecycle()
    MainScaffold(
        nav = nav,
        current = Routes.PROFILE,
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            ProfileScreenContent(
                state = state,
                onUpdateProfile = { name, majorId -> vm.updateProfile(name, majorId) },
                onOpenSettings = { nav.navigate(Routes.SETTINGS) },
                onLogout = { vm.logout(onLoggedOut) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    state: ProfileUiState,
    onUpdateProfile: (String, String) -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    val cu = state.user
    val user = cu?.user
    val majorObj = cu?.major
    val level = Points.levelFor(state.stats.points)
    val earnedBadges = Badges.earnedFor(state.stats.items)
    val currentBadgeIdx = Badges.ALL.indexOfLast { state.stats.items >= it.threshold }.coerceAtLeast(0)
    val currentBadgeNumber = currentBadgeIdx + 1
    val currentBadge = Badges.ALL[currentBadgeIdx]
    val nextBadge = Badges.ALL.getOrNull(currentBadgeIdx + 1)

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Reactive profile photo bitmap loaded from internal storage
    val photoVersion = SettingsStore.profilePhotoVersion
    val avatarBitmap = remember(user?.id, photoVersion) {
        SettingsStore.getProfilePhotoPath(user?.id)?.let { path ->
            runCatching { BitmapFactory.decodeFile(path) }.getOrNull()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null && user != null) {
            SettingsStore.saveProfilePhoto(context, user.id, uri)
        }
    }

    val launchPhotoPicker = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val initials = remember(user?.name) {
        val parts = (user?.name ?: "NU").trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
            parts.size == 1 -> parts[0].uppercase()
            else -> "NU"
        }
    }
    val shortMajor = shortMajorName(majorObj?.name ?: "TMD")

    // Auto-scroll badge row to keep current active badge visible
    val badgeScrollState = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(currentBadgeIdx) {
        if (currentBadgeIdx > 0) {
            val targetPx = with(density) { ((currentBadgeIdx - 1).coerceAtLeast(0) * 116).dp.roundToPx() }
            badgeScrollState.animateScrollTo(targetPx)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Header row: Profil + Setting button on top-right
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = tr("Profil", "Profile"),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                    ),
                    color = Green800,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tr(
                        "Setiap kontribusi punya cerita.",
                        "Every contribution has a story.",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
            }

            Surface(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE6F2EC),
            ) {
                Text(
                    text = tr("Setting", "Settings"),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    ),
                    color = Green900,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Top Identity Section: Left (Avatar Box + Edit Profil pill) & Right (User Card + overlapping Status pill)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Left column: Avatar box + Edit Profil pill
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(102.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE6F2EC))
                        .border(1.dp, Color(0xFFD0E4D9), RoundedCornerShape(20.dp))
                        .clickable { launchPhotoPicker() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarBitmap != null) {
                        Image(
                            bitmap = avatarBitmap.asImageBitmap(),
                            contentDescription = tr("Foto Profil", "Profile Photo"),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                            ),
                            color = Green900,
                        )
                    }

                    // Small camera badge in bottom-right of avatar box
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(5.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D4433))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = tr("Ubah foto profil", "Change profile photo"),
                            tint = Color.White,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = tr("Edit Profil", "Edit Profile"),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                        ),
                        color = Green900,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
            }

            // Right column: White card with Name + Major, plus overlapping Status pill at bottom
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(126.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(1.dp, BorderSoft, RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Column {
                        Text(
                            text = user?.name ?: tr("Nama user", "User Name"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                            ),
                            color = Green800,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = shortMajor,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            ),
                            color = Green900,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }
                }

                // Overlapping Status pill at bottom of the card (shows current Badge #N)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.92f)
                        .offset(y = (-2).dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE6F2EC))
                        .border(1.dp, Color(0xFFCCE2D6), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Status : Badge #$currentBadgeNumber\n${currentBadge.displayName()} [level $level]",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                        ),
                        color = Green900,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Stats Row Container (Outer rounded card with two inner white cards)
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(10.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier
                        .weight(1.2f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, BorderSoft, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tr(
                            "${state.stats.items} total sampah berhasil di pilah",
                            "${state.stats.items} total waste items sorted",
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                        ),
                        color = Green900,
                        textAlign = TextAlign.Center,
                    )
                }

                Box(
                    Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, BorderSoft, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tr(
                            "${state.streak} hari current streak",
                            "${state.streak} days current streak",
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                        ),
                        color = Green900,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Koleksi Badge Card with strong emphasis on the user's current badge number
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
                .padding(16.dp),
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = tr(
                            "Koleksi Badge (${earnedBadges.size}/${Badges.ALL.size})",
                            "Badge Collection (${earnedBadges.size}/${Badges.ALL.size})",
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                        ),
                        color = Green800,
                    )

                    // Pill highlighting current badge number
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1D4433))
                            .shimmerSweep(alpha = 0.22f, durationMillis = 2500, pauseMillis = 900)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = tr("Di Badge #$currentBadgeNumber", "At Badge #$currentBadgeNumber"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Active Badge status banner emphasizing which badge user is currently on
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE4F4EA))
                        .border(1.dp, Green500, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Green800,
                            modifier = Modifier.size(18.dp),
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = tr(
                                    "Posisi Saat Ini: Badge $currentBadgeNumber — ${currentBadge.displayName()}",
                                    "Current Position: Badge $currentBadgeNumber — ${currentBadge.displayName()}",
                                ),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                ),
                                color = Green900,
                            )
                            val nextHint = if (nextBadge != null) {
                                val rem = (nextBadge.threshold - state.stats.items).coerceAtLeast(1)
                                val stepNeeded = nextBadge.threshold - currentBadge.threshold
                                tr(
                                    "Scan $rem foto lagi untuk naik ke Badge ${currentBadgeNumber + 1} (${nextBadge.displayName()}) • Syarat: ${nextBadge.threshold} foto (+$stepNeeded foto)",
                                    "Scan $rem more photos to reach Badge ${currentBadgeNumber + 1} (${nextBadge.displayName()}) • Target: ${nextBadge.threshold} photos (+$stepNeeded)",
                                )
                            } else {
                                tr(
                                    "Badge tertinggi sudah tercapai!",
                                    "Highest badge achieved!",
                                )
                            }
                            Text(
                                text = nextHint,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextSecondary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFDCEBE2), RoundedCornerShape(16.dp))
                        .padding(10.dp),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(badgeScrollState),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Badges.ALL.forEachIndexed { idx, badge ->
                            val isCurrent = idx == currentBadgeIdx
                            val isEarned = state.stats.items >= badge.threshold
                            val cardBg = when {
                                isCurrent -> Color(0xFF1D4433)
                                isEarned -> Color.White
                                else -> Color(0xFFEAF3EE)
                            }
                            val borderColor = when {
                                isCurrent -> Green500
                                isEarned -> Color(0xFFB7D8C6)
                                else -> Color(0xFFD5E6DC)
                            }

                            Box(
                                Modifier
                                    .width(110.dp)
                                    .height(114.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(cardBg)
                                    .shimmerSweep(enabled = isCurrent, alpha = 0.26f, durationMillis = 2400, pauseMillis = 800)
                                    .border(
                                        width = if (isCurrent) 2.5.dp else 2.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    // Status tag at top of each badge card
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(
                                                when {
                                                    isCurrent -> Color(0xFFB5DFC7)
                                                    isEarned -> Color(0xFFE4F4EA)
                                                    else -> Color(0xFFD8E6DE)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = when {
                                                isCurrent -> tr("SAAT INI", "CURRENT")
                                                isEarned -> tr("✓ Lulus", "✓ Earned")
                                                else -> tr("${badge.threshold} foto", "${badge.threshold} photos")
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.sp,
                                            ),
                                            color = if (isCurrent) Green900 else Green800,
                                        )
                                    }

                                    Text(
                                        text = "Badge ${idx + 1}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                        ),
                                        color = when {
                                            isCurrent -> Color.White
                                            isEarned -> Green800
                                            else -> Green800.copy(alpha = 0.55f)
                                        },
                                        textAlign = TextAlign.Center,
                                    )

                                    Text(
                                        text = badge.displayName(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                        ),
                                        color = when {
                                            isCurrent -> Color(0xFFD6EFE1)
                                            isEarned -> Green900
                                            else -> TextMuted
                                        },
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                // Subtle bottom scrollbar indicator matching mockup
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE6EFEA)),
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Riwayat Kontribusi terakhir Card
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
                .padding(16.dp),
        ) {
            Column {
                Text(
                    text = tr("Riwayat Kontribusi terakhir", "Recent Contribution History"),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                    ),
                    color = Green900,
                )

                Spacer(Modifier.height(10.dp))

                if (state.history.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE6F2EC))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = tr(
                                "Belum ada riwayat kontribusi. Yuk mulai scan sampah pertamamu!",
                                "No contribution history yet. Let's scan your first waste item!",
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.history.take(8).forEach { h ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE6F2EC))
                                    .border(1.dp, Color(0xFFD0E4D9), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                            ) {
                                Column {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = h.itemName,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 12.sp,
                                            ),
                                            color = Green900,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = formatRelative(h.createdAt),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                            ),
                                            color = Green800,
                                        )
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = tr(
                                            "(+${h.points} Poin untuk $shortMajor • 99% Akurat)",
                                            "(+${h.points} Points for $shortMajor • 99% Accurate)",
                                        ),
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Log Out button so signed-in users can switch accounts or sign out cleanly
        Surface(
            onClick = { showLogoutDialog = true },
            shape = RoundedCornerShape(18.dp),
            color = DangerSoft,
            border = BorderStroke(1.dp, Danger.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Danger,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = tr("Keluar Akun (Log Out)", "Log Out"),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Danger,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = tr("Keluar dari akun?", "Log out of your account?"),
                    fontWeight = FontWeight.Bold,
                    color = Green900,
                )
            },
            text = {
                Text(
                    text = tr(
                        "Kamu akan keluar dari sesi saat ini dan kembali ke halaman masuk.",
                        "You will be signed out of your current session and returned to the login screen.",
                    ),
                    color = TextSecondary,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(tr("Keluar", "Log Out"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(tr("Batal", "Cancel"), color = TextSecondary)
                }
            },
        )
    }

    if (showEditDialog && user != null) {
        var nameInput by remember(user.name) { mutableStateOf(user.name) }
        var selectedMajorId by remember(user.majorId) { mutableStateOf(user.majorId ?: state.majors.firstOrNull()?.id.orEmpty()) }
        val selectedMajorName = state.majors.firstOrNull { it.id == selectedMajorId }?.name
            ?: majorObj?.name
            ?: selectedMajorId
        var majorMenuOpen by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    tr("Edit Profil", "Edit Profile"),
                    style = MaterialTheme.typography.titleLarge,
                    color = Green900,
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Profile Photo preview + upload button inside Edit Profile dialog
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFE6F2EC))
                            .border(1.5.dp, Green500, RoundedCornerShape(18.dp))
                            .clickable { launchPhotoPicker() },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (avatarBitmap != null) {
                            Image(
                                bitmap = avatarBitmap.asImageBitmap(),
                                contentDescription = tr("Foto Profil", "Profile Photo"),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = Green900,
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { launchPhotoPicker() },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = Green800,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (avatarBitmap == null) {
                                tr("Tambah Foto Profil", "Add Profile Photo")
                            } else {
                                tr("Ganti Foto Profil", "Change Profile Photo")
                            },
                            color = Green800,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(tr("Nama lengkap", "Full name")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    ExposedDropdownMenuBox(
                        expanded = majorMenuOpen,
                        onExpandedChange = { majorMenuOpen = !majorMenuOpen },
                    ) {
                        OutlinedTextField(
                            value = selectedMajorName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(tr("Jurusan / Prodi", "Major / Study Program")) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = majorMenuOpen) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth(),
                        )
                        ExposedDropdownMenu(
                            expanded = majorMenuOpen,
                            onDismissRequest = { majorMenuOpen = false },
                        ) {
                            state.majors.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.name) },
                                    onClick = {
                                        selectedMajorId = m.id
                                        majorMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(nameInput, selectedMajorId)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green800),
                ) {
                    Text(tr("Simpan", "Save"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(tr("Batal", "Cancel"))
                }
            },
        )
    }
}
