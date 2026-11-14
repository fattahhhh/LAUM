package id.ac.pnj.resortcampus.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import id.ac.pnj.resortcampus.R
import id.ac.pnj.resortcampus.data.Badges
import id.ac.pnj.resortcampus.ui.HomeUiState
import id.ac.pnj.resortcampus.ui.HomeViewModel
import id.ac.pnj.resortcampus.ui.PendingScan
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.ScanFlowViewModel
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.MainScaffold
import id.ac.pnj.resortcampus.ui.components.ProgressBar
import id.ac.pnj.resortcampus.ui.components.formatNum
import id.ac.pnj.resortcampus.ui.components.shimmerSweep
import id.ac.pnj.resortcampus.ui.navigateTab
import id.ac.pnj.resortcampus.ui.theme.BgCream
import id.ac.pnj.resortcampus.ui.theme.BorderSoft
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green800
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.TextMuted
import id.ac.pnj.resortcampus.ui.theme.TextSecondary
import id.ac.pnj.resortcampus.util.SettingsStore
import id.ac.pnj.resortcampus.util.isEnglish
import id.ac.pnj.resortcampus.util.tr

private data class FunFactSlide(
    val headlineId: String,
    val headlineEn: String,
    val bodyId: String,
    val bodyEn: String,
) {
    fun headline(en: Boolean) = if (en) headlineEn else headlineId
    fun body(en: Boolean) = if (en) bodyEn else bodyId
}

private val DID_YOU_KNOW_FACTS = listOf(
    FunFactSlide(
        headlineId = "Sampah plastik ternyata lama sekali untuk terurai secara alami di lingkungan.",
        headlineEn = "Plastic waste takes a remarkably long time to decompose naturally in the environment.",
        bodyId = "Botol minuman plastik berbahan PET termasuk sampah anorganik karena terbuat dari polimer sintetis minyak bumi yang tidak dapat diurai oleh mikroorganisme tanah. Satu botol plastik membutuhkan waktu sekitar 450 tahun untuk hancur di Tempat Pemrosesan Akhir (TPA).\n\nDi Indonesia, sampah plastik menjadi penyumbang sampah terbesar kedua setelah sisa makanan, yaitu sekitar 18 hingga 19 persen dari total timbulan sampah nasional. Di lingkungan kampus, sumber utamanya berasal dari botol minuman sekali pakai, gelas kopi plastik, dan kemasan makanan.\n\nKosongkan sisa cairan dan remukkan botol sebelum membuangnya ke tong sampah anorganik agar volumenya lebih ringkas dan siap didaur ulang menjadi serat tekstil atau bijih plastik baru.",
        bodyEn = "PET plastic drink bottles are inorganic waste made from synthetic petroleum polymers that soil microorganisms cannot digest. A single plastic bottle takes around 450 years to break down in a landfill.\n\nIn Indonesia, plastic is the second largest contributor to national waste at roughly 18 to 19 percent of the total volume. On campus, the primary sources are single-use drink bottles, plastic coffee cups, and food wrappers.\n\nEmpty any remaining liquid and crush the bottle before placing it in the inorganic bin so it takes up less space and can be recycled into textile fibers or new plastic pellets.",
    ),
    FunFactSlide(
        headlineId = "Sisa makanan dan sampah organik adalah penyumbang sampah terbanyak setiap hari.",
        headlineEn = "Food scraps and organic waste make up the largest share of daily waste.",
        bodyId = "Sampah organik adalah limbah yang berasal dari bahan hayati atau makhluk hidup, seperti sisa nasi, sayuran, kulit buah, dan daun kering. Karena sifatnya alami, sampah organik dapat terurai oleh bakteri dan jamur hanya dalam waktu 2 minggu hingga 3 bulan.\n\nBerdasarkan data Sistem Informasi Pengelolaan Sampah Nasional (SIPSN), sisa makanan merupakan penyumbang sampah nomor satu di Indonesia dengan porsi sekitar 40 hingga 41 persen. Penyumbang terbesarnya adalah aktivitas rumah tangga (sekitar 40 persen), disusul pasar tradisional, kantin kampus, dan rumah makan.\n\nJika sampah organik tertimbun bersama plastik di TPA tanpa oksigen, pembusukannya menghasilkan gas metana yang mempercepat pemanasan global. Memilahnya ke tong organik memungkinkan sampah ini diolah menjadi kompos penyubur taman kampus.",
        bodyEn = "Organic waste comes from biological or living matter such as leftover rice, vegetables, fruit peels, and dry leaves. Because it is natural, bacteria and fungi can break it down in just 2 weeks to 3 months.\n\nAccording to Indonesia's National Waste Management Information System (SIPSN), food waste is the number one waste contributor in the country at 40 to 41 percent. Households account for the largest share (around 40 percent), followed by traditional markets, campus canteens, and restaurants.\n\nWhen organic waste is buried with plastic in oxygen-free landfills, it releases methane gas that accelerates global warming. Sorting it into the organic bin allows it to be turned into compost for campus gardens.",
    ),
    FunFactSlide(
        headlineId = "Kantong kresek dan kemasan tipis hanya dipakai sebentar namun bertahan puluhan tahun.",
        headlineEn = "Plastic grocery bags and thin wrappers are used briefly but persist for decades.",
        bodyId = "Kantong kresek berbahan polietilena (LDPE) dan bungkus jajanan berlapis termasuk sampah anorganik fleksibel. Rata-rata kantong plastik hanya digunakan selama 25 menit sebelum dibuang, tetapi membutuhkan waktu 20 hingga 100 tahun untuk terurai di alam.\n\nKemasan sachet berlapis aluminium foil bahkan membutuhkan 50 hingga 80 tahun untuk hancur. Penyumbang utama sampah jenis ini adalah kegiatan belanja harian, bungkusan makanan pesan-antar, serta konsumsi jajanan kemasan sekali pakai di area kantin.\n\nKarena bobotnya ringan, plastik tipis mudah terbawa angin dan menyumbat saluran drainase. Membawa tas lipat dan wadah makan sendiri saat ke kampus dapat memangkas sampah plastik tipis secara nyata.",
        bodyEn = "Polyethylene grocery bags (LDPE) and multilayer snack wrappers are flexible inorganic waste. On average, a plastic bag is used for just 25 minutes before being discarded, yet it takes 20 to 100 years to decompose.\n\nAluminum-lined sachet wrappers take 50 to 80 years to break down. The main contributors are daily convenience shopping, take-away food orders, and single-use packaged snacks around canteens.\n\nBecause thin plastic is lightweight, wind easily carries it into storm drains. Bringing a reusable tote bag and lunch container to campus cuts down thin plastic waste significantly.",
    ),
    FunFactSlide(
        headlineId = "Wadah makanan styrofoam hampir tidak dapat terurai secara biologis di tanah.",
        headlineEn = "Styrofoam food containers can barely biodegrade in soil.",
        bodyId = "Styrofoam atau polistirena ekspansi adalah sampah anorganik yang tersusun dari 95 persen udara dan 5 persen plastik berbasis minyak bumi. Mikroorganisme tanah tidak mengenali struktur kimianya sebagai makanan, sehingga styrofoam membutuhkan waktu lebih dari 500 tahun dan hanya pecah menjadi butiran mikroplastik.\n\nPenyumbang terbesar sampah styrofoam berasal dari bungkus makanan cepat saji, katering kegiatan atau seminar mahasiswa, wadah mie instan cup, serta pelindung barang elektronik.\n\nKarena memakan banyak ruang namun bobotnya sangat ringan, pengepul jarang menerima styrofoam untuk didaur ulang. Mengurangi pemakaian wadah styrofoam di acara kampus adalah langkah pencegahan paling efektif.",
        bodyEn = "Styrofoam, or expanded polystyrene, is inorganic waste composed of 95 percent air and 5 percent petroleum-based plastic. Soil microorganisms cannot break down its chemical structure, so styrofoam lasts over 500 years and merely fragments into microplastics.\n\nThe biggest contributors are fast-food take-out containers, student event and seminar catering, instant noodle cups, and electronics packaging.\n\nBecause it takes up large volume with very little weight, recyclers rarely accept styrofoam. Avoiding styrofoam containers during campus events is the most effective way to prevent this waste.",
    ),
    FunFactSlide(
        headlineId = "Kertas dan kardus mudah terurai, tetapi nilainya hilang jika tercampur sampah basah.",
        headlineEn = "Paper and cardboard decompose easily, but lose their recycling value when wet.",
        bodyId = "Kertas HVS, koran, dan kardus dibuat dari serat selulosa kayu alami sehingga dapat terurai di alam dalam waktu 2 hingga 6 bulan. Meski berasal dari bahan organik pohon, kertas dikelompokkan dalam tong tersendiri agar seratnya dapat dilebur kembali menjadi kertas daur ulang.\n\nSampah kertas dan karton menyumbang sekitar 11 persen dari total sampah nasional. Di area kampus dan perkantoran, penyumbang utamanya adalah pencetakan laporan akademik, fotokopi dokumen, kemasan makanan kertas, dan kardus paket belanja daring.\n\nApabila kertas terkena kuah makanan, minyak, atau sisa minuman, hingga 70 persen nilai daur ulangnya akan rusak. Pastikan kertas dan kardus selalu dibuang dalam keadaan kering ke tong sampah kertas.",
        bodyEn = "Office paper, newspapers, and cardboard are made from natural wood cellulose fibers and decompose within 2 to 6 months. Although derived from trees, paper is placed in its own dedicated bin so the fibers can be pulped into recycled paper.\n\nPaper and cardboard account for roughly 11 percent of national waste. On campus and in offices, the main contributors are printed academic reports, photocopies, paper food boxes, and online shopping parcels.\n\nWhen paper is soaked with food broth, oil, or leftover drinks, up to 70 percent of its recycling value is lost. Always keep paper and cardboard dry when placing them in the paper bin.",
    ),
    FunFactSlide(
        headlineId = "Kaleng aluminium dan botol kaca bertahan ratusan tahun namun bisa didaur ulang seutuhnya.",
        headlineEn = "Aluminum cans and glass bottles last for centuries but are completely recyclable.",
        bodyId = "Kaleng minuman dan botol kaca merupakan sampah anorganik dengan ketahanan fisik yang sangat tinggi. Di alam terbuka, kaleng aluminium membutuhkan waktu 80 hingga 200 tahun untuk terurai, sedangkan botol kaca membutuhkan waktu hingga 1 juta tahun untuk kembali menjadi butiran pasir silika.\n\nPenyumbang utama sampah logam dan kaca di perkotaan adalah konsumsi minuman kaleng di kantin dan minimarket, botol saus atau kecap rumah tangga, serta botol kaca dari fasilitas laboratorium.\n\nKedua material ini dapat dilebur dan didaur ulang berulang kali tanpa menurunkan kualitas aslinya. Mendaur ulang satu kaleng aluminium menghemat hingga 95 persen energi dibanding memproduksi aluminium baru dari tambang bauksit.",
        bodyEn = "Drink cans and glass bottles are durable inorganic waste. Left in nature, an aluminum can takes 80 to 200 years to corrode, while a glass bottle can take up to 1 million years to weather back into silica sand.\n\nThe primary contributors in urban areas are canned drinks from canteens and convenience stores, household sauce bottles, and glassware from laboratory facilities.\n\nBoth materials can be melted and recycled repeatedly without losing their original quality. Recycling a single aluminum can saves up to 95 percent of the energy required to refine new aluminum from bauxite ore.",
    ),
)

private fun shortMajorCode(major: String): String {
    val clean = major.trim()
    if (clean.isEmpty()) return "PNJ"
    return when {
        clean.contains("Multimedia Digital", ignoreCase = true) -> "TMD"
        clean.contains("Informatika", ignoreCase = true) && clean.contains("Teknik", ignoreCase = true) -> "TI"
        clean.contains("Multimedia dan Jaringan", ignoreCase = true) -> "TMJ"
        clean.contains("Komputer", ignoreCase = true) -> "TKJ"
        else -> clean
    }
}

@Composable
fun HomeScreen(
    nav: NavHostController,
    scanFlow: ScanFlowViewModel,
    vm: HomeViewModel = appViewModel { HomeViewModel(it) },
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val pending by scanFlow.pending.collectAsStateWithLifecycle()
    var showDidYouKnowPage by rememberSaveable { mutableStateOf(false) }

    MainScaffold(
        nav = nav,
        current = Routes.HOME,
        scanMode = if (pending != null) ScanMode.BIN else ScanMode.WASTE,
        scanLabel = "Scan",
        onHomeClick = if (showDidYouKnowPage) ({ showDidYouKnowPage = false }) else null,
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            if (showDidYouKnowPage) {
                val userName = state.user?.user?.name ?: tr("Nama user", "User")
                DidYouKnowScreenContent(
                    userName = userName,
                    onBack = { showDidYouKnowPage = false },
                    onOpenBins = {
                        showDidYouKnowPage = false
                        nav.navigateTab(Routes.BINS)
                    },
                    onOpenLeaderboard = {
                        showDidYouKnowPage = false
                        nav.navigateTab(Routes.LEADERBOARD)
                    },
                )
            } else {
                HomeScreenContent(
                    state = state,
                    pending = pending,
                    onStartScan = { nav.navigate(Routes.scan(ScanMode.WASTE)) },
                    onOpenBins = { nav.navigateTab(Routes.BINS) },
                    onOpenLeaderboard = { nav.navigateTab(Routes.LEADERBOARD) },
                    onOpenProfile = { nav.navigateTab(Routes.PROFILE) },
                    onOpenDidYouKnow = { showDidYouKnowPage = true },
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    pending: PendingScan? = null,
    onStartScan: () -> Unit,
    onOpenBins: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenProfile: () -> Unit = {},
    onOpenDidYouKnow: () -> Unit = {},
) {
    val cu = state.user
    val stats = state.stats
    val en = isEnglish()
    val facts = DID_YOU_KNOW_FACTS
    val currentFactIndex = SettingsStore.funFactIndex.mod(facts.size)
    val currentFact = facts[currentFactIndex]
    var showMoreMenu by remember { mutableStateOf(false) }

    val nextBadge = Badges.nextFor(stats.items) ?: Badges.ALL.last()
    val targetItems = nextBadge.threshold.coerceAtLeast(10)
    val itemProgress = (stats.items.toFloat() / targetItems.toFloat()).coerceIn(0f, 1f)

    val myEntry = state.myMajor
    val rank = myEntry?.rank ?: 1
    val nextRankNum = (rank - 1).coerceAtLeast(1)
    val aboveEntry = if (rank > 1) state.board.firstOrNull { it.rank == rank - 1 } else null
    val pointsToNextRank = if (aboveEntry != null && myEntry != null) {
        (aboveEntry.points - myEntry.points).coerceAtLeast(10)
    } else 0
    val rankProgress = if (rank == 1) 1f else (1f - (pointsToNextRank.coerceAtMost(500) / 600f)).coerceIn(0.18f, 0.92f)
    val majorLabel = shortMajorCode(cu?.major?.name ?: "TMD")
    val userName = cu?.user?.name ?: tr("Nama user", "User")

    Column(
        Modifier
            .fillMaxSize()
            .background(BgCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Header: Hai, Nama user + Streak pill + More button
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = tr("Hai, $userName", "Hi, $userName"),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                    ),
                    color = Green800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$majorLabel • Politeknik Negeri Jakarta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                // Streak count badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE7F2EC))
                        .clickable { onOpenProfile() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${state.streak}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Green900,
                    )
                }

                // More options button (gives access to Peta Tong & Leaderboard)
                Box {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE7F2EC))
                            .clickable { showMoreMenu = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.MoreHoriz,
                            contentDescription = tr("Menu lainnya", "More menu"),
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFC5DDD0))
                                .padding(2.dp),
                        )
                    }

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(tr("Peta Lokasi Tong PNJ", "PNJ Bin Locations Map")) },
                            onClick = {
                                showMoreMenu = false
                                onOpenBins()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Leaderboard") },
                            onClick = {
                                showMoreMenu = false
                                onOpenLeaderboard()
                            },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Pending waste banner (if user already scanned waste and needs to verify bin)
        pending?.let { p ->
            Surface(
                onClick = onOpenBins,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE4F4EA),
                border = androidx.compose.foundation.BorderStroke(1.dp, Green500),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            tr("Menunggu verifikasi tong: ${p.itemName}", "Awaiting bin verification: ${p.itemName}"),
                            style = MaterialTheme.typography.labelLarge,
                            color = Green900,
                        )
                        Text(
                            tr("Ketuk untuk buka Peta Tong & klaim poin", "Tap to open Bin Map & claim points"),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Green800)
                }
            }
        }

        // Hero Banner: Dark green card matching mockup
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1D4433))
                .shimmerSweep(alpha = 0.11f, durationMillis = 3200, pauseMillis = 900)
                .padding(22.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = tr("AKSI KECIL, DAMPAK BESAR", "SMALL ACTION, BIG IMPACT"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                        color = Color(0xFF9AC5AF),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = tr("Pilah sampahmu,\nHijaukan kampus.", "Sort your waste,\nGreen the campus."),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 23.sp,
                            lineHeight = 29.sp,
                        ),
                        color = Color.White,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = tr(
                            "Satu scan, satu langkah\nuntuk bumi yang lebih baik.",
                            "One scan, one step\nfor a greener earth.",
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            fontSize = 12.sp,
                        ),
                        color = Color(0xFFCBE2D6),
                    )
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        onClick = onStartScan,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerSweep(alpha = 0.34f, durationMillis = 2100, pauseMillis = 800),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFB5DFC7),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = tr("Scan sekarang", "Scan now"),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                ),
                                color = Green900,
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Green900,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                HomeHeroBottleIllustration(
                    modifier = Modifier.size(116.dp),
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Rank Card (mint green background with #Rank badge & Leaderboard button)
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFB6E2C8))
                .border(1.dp, Color(0xFF66B889), RoundedCornerShape(22.dp))
                .clickable { onOpenLeaderboard() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = tr("PERINGKAT PENGGUNA", "USER RANKING"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp,
                        ),
                        color = Green900,
                    )

                    Surface(
                        onClick = onOpenLeaderboard,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerSweep(alpha = 0.24f, durationMillis = 2200, pauseMillis = 850),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1D4433),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Leaderboard",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                ),
                                color = Color.White,
                            )
                            Spacer(Modifier.width(5.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Dark green rounded square with #Rank
                    Box(
                        modifier = Modifier
                            .size(width = 66.dp, height = 56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1D4433))
                            .shimmerSweep(alpha = 0.22f, durationMillis = 2400, pauseMillis = 900),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "#$rank",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                            ),
                            color = Color.White,
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                            ),
                            color = Green900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (rank == 1) {
                                tr(
                                    "Peringkat #1 di kampus ($userName)",
                                    "Rank #1 on campus ($userName)",
                                )
                            } else {
                                tr(
                                    "${formatNum(pointsToNextRank)} poin menuju peringkat #$nextRankNum",
                                    "${formatNum(pointsToNextRank)} points to rank #$nextRankNum",
                                )
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Green800,
                        )
                        Spacer(Modifier.height(8.dp))
                        ProgressBar(
                            value = rankProgress,
                            height = 6.dp,
                            trackColor = Color.White.copy(alpha = 0.75f),
                            fillColor = Color(0xFF1D4433),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Section Heading: Kontribusi hari ini
        Text(
            text = tr("Kontribusi hari ini", "Today's contribution"),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
            ),
            color = Green800,
        )

        Spacer(Modifier.height(10.dp))

        // Two side-by-side white cards: Streak Aktif & Poin Anda
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Left card: Streak Aktif
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = tr("Streak Aktif", "Active Streak"),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextMuted,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = tr("${state.streak} hari", "${state.streak} days"),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                        ),
                        color = Green900,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = tr("Pertahankan aksi hijaumu", "Keep up your green action"),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextMuted,
                    )
                }
            }

            // Right card: Poin
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                    .clickable { onOpenLeaderboard() }
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = tr("Poin anda", "Your points"),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextMuted,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = formatNum(myEntry?.points ?: stats.points),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                        ),
                        color = Green900,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = tr("+15 poin per botol plastik", "+15 pts per plastic bottle"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = Green800,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Target Progress Card: Sedikit lagi menuju target!
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFE6F2EC))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column {
                Text(
                    text = tr("Sedikit lagi menuju target!", "Almost at your target!"),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                    ),
                    color = Green900,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tr(
                        "${stats.items} dari $targetItems foto sampah berhasil dipilah (menuju ${nextBadge.displayName()})",
                        "${stats.items} of $targetItems waste photos sorted (towards ${nextBadge.displayName()})",
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                ProgressBar(
                    value = itemProgress,
                    height = 6.dp,
                    trackColor = Color(0xFFCDE2D6),
                    fillColor = Color(0xFF2E8B57),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Did You Know Card: Tahukah kamu?
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFE6F2EC))
                .clickable { onOpenDidYouKnow() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = tr("Tahukah kamu?", "Did you know?"),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                        ),
                        color = Green900,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = tr("Baca selengkapnya", "Read more"),
                        tint = Green900,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = currentFact.headline(en),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DidYouKnowScreenContent(
    userName: String,
    onBack: () -> Unit,
    onOpenBins: () -> Unit,
    onOpenLeaderboard: () -> Unit,
) {
    BackHandler { onBack() }

    val en = isEnglish()
    val facts = DID_YOU_KNOW_FACTS
    val initialIndex = remember { SettingsStore.funFactIndex.mod(facts.size) }
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { facts.size },
    )
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        SettingsStore.setFunFact(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(top = 16.dp, bottom = 14.dp),
    ) {
        // Header sesuai mockup: "Hai, [user]" + "Tahukah kamu?" di kiri, tombol "..." di kanan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tr("Hai, $userName", "Hi, $userName"),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tr("Tahukah kamu?", "Did you know?"),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                    ),
                    color = Green800,
                )
            }

            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFA7D7BC))
                        .clickable { showMenu = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = tr("Menu lainnya", "More menu"),
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(tr("Kembali ke Beranda", "Back to Home")) },
                        onClick = {
                            showMenu = false
                            onBack()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(tr("Peta Lokasi Tong PNJ", "PNJ Bin Locations Map")) },
                        onClick = {
                            showMenu = false
                            onOpenBins()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Leaderboard") },
                        onClick = {
                            showMenu = false
                            onOpenLeaderboard()
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Kartu hijau sage yang bisa digeser ke samping (HorizontalPager)
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            val fact = facts[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF537B67))
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Logo LAUM di bagian atas tengah
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "LAUM",
                        modifier = Modifier.height(110.dp),
                        contentScale = ContentScale.Fit,
                    )

                    Spacer(Modifier.height(20.dp))

                    // Judul fakta + penjelasan lengkap tanpa emoji
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Text(
                            text = fact.headline(en),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                lineHeight = 25.sp,
                            ),
                            color = Color.White,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = fact.body(en),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                            ),
                            color = Color.White.copy(alpha = 0.90f),
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Indikator titik halaman di bagian bawah kartu
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(facts.size) { idx ->
                            val active = idx == pagerState.currentPage
                            Box(
                                modifier = Modifier
                                    .size(if (active) 8.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (active) Color.White else Color.White.copy(alpha = 0.42f)
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroBottleIllustration(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroBottleAnim")
    val bottleFloatY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bottleFloatY",
    )
    val bottleTilt by infiniteTransition.animateFloat(
        initialValue = 21f,
        targetValue = 34f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bottleTilt",
    )
    val arcSway by infiniteTransition.animateFloat(
        initialValue = -9f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "arcSway",
    )
    val leafBob by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "leafBob",
    )
    val leafPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "leafPulse",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.52f)
        val bottleCenter = Offset(center.x, center.y + bottleFloatY)

        // Circular green recycling arc around bottle (gently swaying)
        val arcRadius = w * 0.36f
        rotate(degrees = arcSway, pivot = center) {
            drawArc(
                color = Color(0xFF45A16D),
                startAngle = -110f,
                sweepAngle = 215f,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 11f, cap = StrokeCap.Round),
            )

            // Arrow head on right side of arc
            val arrowTip = Offset(center.x + arcRadius * 0.85f, center.y + arcRadius * 0.45f)
            val arrowPath = Path().apply {
                moveTo(arrowTip.x, arrowTip.y)
                lineTo(arrowTip.x - 20f, arrowTip.y - 16f)
                lineTo(arrowTip.x + 6f, arrowTip.y - 22f)
                close()
            }
            drawPath(arrowPath, color = Color(0xFF45A16D))
        }

        // Small decorative leaves floating gently around the bottle
        drawCircle(
            color = Color(0xFF45A16D),
            radius = 6f * leafPulse,
            center = Offset(w * 0.14f, h * 0.30f - leafBob),
        )
        drawCircle(
            color = Color(0xFF45A16D),
            radius = 5f * (2.05f - leafPulse),
            center = Offset(w * 0.84f, h * 0.80f + leafBob * 0.8f),
        )
        drawCircle(
            color = Color(0xFF45A16D),
            radius = 5f * leafPulse,
            center = Offset(w * 0.50f + leafBob * 0.5f, h * 0.90f - leafBob * 0.6f),
        )

        // Tilted white plastic bottle in center (gently bobbing & rocking)
        rotate(degrees = bottleTilt, pivot = bottleCenter) {
            val bw = w * 0.28f
            val bh = h * 0.48f
            val left = bottleCenter.x - bw / 2f
            val top = bottleCenter.y - bh / 2f

            // Bottle cap
            drawRoundRect(
                color = Color(0xFF45A16D),
                topLeft = Offset(bottleCenter.x - bw * 0.26f, top - bh * 0.16f),
                size = Size(bw * 0.52f, bh * 0.13f),
                cornerRadius = CornerRadius(6f, 6f),
            )
            // Bottle neck
            drawRoundRect(
                color = Color(0xFFD9EFE2),
                topLeft = Offset(bottleCenter.x - bw * 0.22f, top - bh * 0.05f),
                size = Size(bw * 0.44f, bh * 0.10f),
                cornerRadius = CornerRadius(4f, 4f),
            )
            // Bottle body
            drawRoundRect(
                color = Color(0xFFEAF7F0),
                topLeft = Offset(left, top),
                size = Size(bw, bh),
                cornerRadius = CornerRadius(16f, 16f),
            )
            // Bottle ridges / label highlight
            drawRoundRect(
                color = Color(0xFFC4E4D2),
                topLeft = Offset(left + bw * 0.12f, top + bh * 0.22f),
                size = Size(bw * 0.76f, bh * 0.42f),
                cornerRadius = CornerRadius(8f, 8f),
            )
        }
    }
}
