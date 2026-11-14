package id.ac.pnj.resortcampus.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Looper
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import id.ac.pnj.resortcampus.data.local.BinLocationEntity
import id.ac.pnj.resortcampus.ui.BinsViewModel
import id.ac.pnj.resortcampus.ui.Routes
import id.ac.pnj.resortcampus.ui.ScanFlowViewModel
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.AppCard
import id.ac.pnj.resortcampus.ui.components.MainScaffold
import id.ac.pnj.resortcampus.ui.components.PrimaryButton
import id.ac.pnj.resortcampus.ui.components.ScreenHeader
import id.ac.pnj.resortcampus.ui.components.binColor
import id.ac.pnj.resortcampus.ui.components.binIcon
import id.ac.pnj.resortcampus.ui.components.binLabel
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Muted
import id.ac.pnj.resortcampus.util.tr
import java.io.File
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.roundToInt

private val PNJ_CENTER = GeoPoint(-6.371450, 106.824100)

private val PNJ_OSM_TILE_SOURCE = XYTileSource(
    "OSM-PNJ",
    0,
    19,
    256,
    ".png",
    arrayOf("https://tile.openstreetmap.org/"),
    "© OpenStreetMap contributors",
    TileSourcePolicy(
        2,
        TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE,
    ),
)

private val PNJ_BIN_COORDINATES: Map<String, GeoPoint> = mapOf(
    // Gedung Z, Teknik Grafika dan Percetakan (-6.3698868, 106.8229694)
    "tgp-1" to GeoPoint(-6.369865, 106.822940),
    "tgp-2" to GeoPoint(-6.369915, 106.823015),
    // Administrasi Bisnis Building / Administrasi Niaga (-6.3705120, 106.8230210)
    "an-1" to GeoPoint(-6.370490, 106.822990),
    "an-2" to GeoPoint(-6.370540, 106.823060),
    // Gedung F, Akuntansi (-6.3709841, 106.8235127)
    "akt-1" to GeoPoint(-6.370960, 106.823480),
    "akt-2" to GeoPoint(-6.371010, 106.823555),
    // Gedung Q, Direktorat (-6.3708696, 106.8246175)
    "dir-1" to GeoPoint(-6.370850, 106.824585),
    "dir-2" to GeoPoint(-6.370895, 106.824655),
    // Gedung D, Teknik Elektro (-6.3714001, 106.8229715)
    "elektro-1" to GeoPoint(-6.371380, 106.822945),
    "elektro-2" to GeoPoint(-6.371425, 106.823015),
    // Gedung A & Gedung M, Teknik Mesin
    "mesin-1" to GeoPoint(-6.371419, 106.823570),
    "mesin-2" to GeoPoint(-6.372484, 106.824917),
    // Gedung AA, Teknik Informatika dan Komputer (-6.3725201, 106.8239540)
    "tik-1" to GeoPoint(-6.372495, 106.823920),
    "tik-2" to GeoPoint(-6.372545, 106.823990),
    // Gedung T, Departemen Teknik Sipil (-6.3728433, 106.8255591)
    "sipil-1" to GeoPoint(-6.372820, 106.825525),
    "sipil-2" to GeoPoint(-6.372870, 106.825595),
    // Gedung Y, Perpustakaan (-6.3719709, 106.8260983)
    "perpus-1" to GeoPoint(-6.371971, 106.826098),
    // Kantin Spirit (-6.3720628, 106.8242151) & Kantin Bawah (-6.3708101, 106.8228436)
    "kantin-1" to GeoPoint(-6.372063, 106.824215),
    "kantin-2" to GeoPoint(-6.370810, 106.822844),
    // Gedung Serba Guna (-6.3705076, 106.8237027)
    "gsg-1" to GeoPoint(-6.370508, 106.823703),
    // Masjid Darul Ilmi (-6.3717179, 106.8229797)
    "masjid-1" to GeoPoint(-6.371718, 106.822980),
)

private fun BinLocationEntity.toGeoPoint(): GeoPoint =
    PNJ_BIN_COORDINATES[id] ?: GeoPoint(
        PNJ_CENTER.latitude + (0.5f - y) * 0.0018,
        PNJ_CENTER.longitude + (x - 0.5f) * 0.0018,
    )

private fun distanceMeters(from: GeoPoint, to: GeoPoint): Int {
    val result = FloatArray(1)
    Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, result)
    return result[0].roundToInt()
}

private fun createBinMarkerDrawable(
    context: Context,
    fillColor: Color,
    label: String,
    emphasized: Boolean,
): Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = ((if (emphasized) 38f else 30f) * density).roundToInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val radius = sizePx / 2f - 2f * density

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = fillColor.toArgb()
        style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = (if (emphasized) 3f else 2f) * density
    }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = (if (emphasized) 14f else 11.5f) * density
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    canvas.drawCircle(cx, cy, radius, fillPaint)
    canvas.drawCircle(cx, cy, radius, borderPaint)
    val textY = cy - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(label, cx, textY, textPaint)
    return BitmapDrawable(context.resources, bitmap)
}

private fun createUserLocationDrawable(context: Context): Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (24f * density).roundToInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val cx = sizePx / 2f
    val cy = sizePx / 2f

    val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x331E88E5
        style = Paint.Style.FILL
    }
    val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF1E88E5.toInt()
        style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }

    canvas.drawCircle(cx, cy, sizePx / 2f, haloPaint)
    canvas.drawCircle(cx, cy, 7f * density, dotPaint)
    canvas.drawCircle(cx, cy, 7f * density, borderPaint)
    return BitmapDrawable(context.resources, bitmap)
}

@SuppressLint("MissingPermission")
@Composable
fun BinsScreen(nav: NavHostController, scanFlow: ScanFlowViewModel) {
    val context = LocalContext.current
    val vm = appViewModel { BinsViewModel(it) }
    val bins by vm.bins.collectAsStateWithLifecycle()
    val pending by scanFlow.pending.collectAsStateWithLifecycle()
    var selectedId by remember(bins, pending) {
        mutableStateOf(
            if (pending != null) bins.firstOrNull { it.binType == pending?.binType }?.id else null
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        hasLocationPermission = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            onDispose {}
        } else {
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) userLocation = GeoPoint(loc.latitude, loc.longitude)
            }
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
                .setMinUpdateIntervalMillis(2000L)
                .build()
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { loc ->
                        userLocation = GeoPoint(loc.latitude, loc.longitude)
                    }
                }
            }
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
            onDispose { client.removeLocationUpdates(callback) }
        }
    }

    val openScan: () -> Unit = {
        val mode = if (pending != null) ScanMode.BIN else ScanMode.WASTE
        nav.navigate(Routes.scan(mode))
    }

    MainScaffold(
        nav = nav,
        current = Routes.BINS,
        scanMode = if (pending != null) ScanMode.BIN else ScanMode.WASTE,
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenHeader(tr("Lokasi Tong PNJ", "PNJ Bin Locations"))
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                pending?.let { p ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = GreenSoft,
                        border = BorderStroke(1.dp, GreenBorder),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(
                                tr(
                                    "Sampahmu: ${p.itemName} -> Tong ${binLabel(p.binType)}",
                                    "Your waste: ${p.itemName} -> ${binLabel(p.binType)} Bin",
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                color = GreenText,
                            )
                            Text(
                                tr(
                                    "Datangi tong yang ditandai di peta, buang sampahnya, lalu tekan tombol di bawah untuk memotret tong.",
                                    "Go to the marked bin on the map, dispose of the waste, then tap the button below to snap the bin.",
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = GreenText.copy(alpha = 0.85f),
                            )
                            Spacer(Modifier.height(10.dp))
                            PrimaryButton(
                                text = tr("Foto Tong untuk Klaim Poin", "Photo Bin to Claim Points"),
                                onClick = openScan,
                                icon = Icons.Filled.PhotoCamera,
                                modifier = Modifier.fillMaxWidth(),
                                shimmer = true,
                            )
                        }
                    }
                }

                CampusMapCard(
                    bins = bins,
                    recommendedType = pending?.binType,
                    selectedId = selectedId,
                    userLocation = userLocation,
                    hasLocationPermission = hasLocationPermission,
                    onRequestLocation = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    },
                    onSelect = { selectedId = it.id },
                )

                // Kartu aksi bawah sesuai Figma (UI • Lokasi tong sampah: "Pilah di tempat yang tepat" -> "Lanjut verifikasi pembuangan")
                AppCard(Modifier.fillMaxWidth()) {
                    Text(
                        tr("Pilah di tempat yang tepat", "Sort in the right place"),
                        style = MaterialTheme.typography.titleMedium,
                        color = Green900,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (pending != null) {
                            tr(
                                "Buang ${pending!!.itemName} ke tong ${binLabel(pending!!.binType)} terdekat, lalu potret tongnya untuk klaim poin.",
                                "Dispose of ${pending!!.itemName} in the nearest ${binLabel(pending!!.binType)} bin, then snap the bin to claim points.",
                            )
                        } else {
                            tr(
                                "Pilih titik tong sampah terdekat di kampus PNJ, lalu pindai sampah atau verifikasi pembuanganmu.",
                                "Choose the nearest waste bin on PNJ campus, then scan waste or verify your disposal.",
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted,
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(
                        text = if (pending != null) {
                            tr("Lanjut verifikasi pembuangan", "Continue disposal verification")
                        } else {
                            tr("Mulai Scan Sampah", "Start Waste Scan")
                        },
                        onClick = openScan,
                        icon = Icons.Filled.PhotoCamera,
                        modifier = Modifier.fillMaxWidth(),
                        shimmer = true,
                    )
                }

                Text(tr("Daftar titik tong sampah", "Waste bin locations"), style = MaterialTheme.typography.titleSmall, color = Green900)
                bins.forEach { bin ->
                    val isRecommended = pending?.binType == bin.binType
                    val isSelected = selectedId == bin.id
                    val color = binColor(bin.binType)
                    val dist = userLocation?.let { distanceMeters(it, bin.toGeoPoint()) }
                    Surface(
                        onClick = { selectedId = bin.id },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) GreenSoft else Color.White,
                        border = BorderStroke(if (isSelected || isRecommended) 1.5.dp else 1.dp, if (isSelected) Green500 else Line),
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(binIcon(bin.binType), null, tint = color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(bin.name, style = MaterialTheme.typography.titleSmall, color = Green900)
                                Text(
                                    buildString {
                                        append("${bin.areaLabel} • ${binLabel(bin.binType)}")
                                        if (dist != null) append(" • ~$dist m")
                                        if (isRecommended) append(tr(" • Cocok untuk sampahmu", " • Matches your waste"))
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isRecommended) GreenText else Muted,
                                )
                            }
                            Icon(Icons.Filled.Place, null, tint = color)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun CampusMapCard(
    bins: List<BinLocationEntity>,
    recommendedType: String?,
    selectedId: String?,
    userLocation: GeoPoint?,
    hasLocationPermission: Boolean,
    onRequestLocation: () -> Unit,
    onSelect: (BinLocationEntity) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        val appContext = context.applicationContext
        val osmdroidPrefs = appContext.getSharedPreferences("osmdroid_pnj", Context.MODE_PRIVATE)
        val osmdroidDir = File(appContext.cacheDir, "osmdroid_pnj")
        val tileCacheDir = File(osmdroidDir, "tiles").apply { mkdirs() }
        Configuration.getInstance().apply {
            osmdroidBasePath = osmdroidDir
            osmdroidTileCache = tileCacheDir
            load(appContext, osmdroidPrefs)
            tileDownloadThreads = 2
            // Pakai User-Agent khusus aplikasi PNJ agar tidak ditolak kebijakan tile OSM (403 com.example.*).
            userAgentValue = "ReSortCampusPNJ/1.0 (id.ac.pnj.resortcampus)"
        }
        MapView(context).apply {
            setTileSource(PNJ_OSM_TILE_SOURCE)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            controller.setCenter(PNJ_CENTER)
            setOnTouchListener { v, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                        v.parent?.requestDisallowInterceptTouchEvent(false)
                }
                false
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    LaunchedEffect(selectedId, bins) {
        val target = bins.firstOrNull { it.id == selectedId }?.toGeoPoint() ?: return@LaunchedEffect
        mapView.controller.animateTo(target, 18.4, 450L)
    }

    AppCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.MyLocation, null, tint = Green500, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(tr("Peta Kampus PNJ (Real-Time)", "PNJ Campus Map (Real-Time)"), style = MaterialTheme.typography.titleSmall, color = Green900)
            }
            Text(tr("Ketuk pin", "Tap pin"), style = MaterialTheme.typography.labelSmall, color = Muted)
        }
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MapQuickChip(
                icon = Icons.Filled.School,
                label = tr("Kampus PNJ", "PNJ Campus"),
                onClick = {
                    mapView.controller.animateTo(PNJ_CENTER, 17.0, 450L)
                },
            )
            MapQuickChip(
                icon = Icons.Filled.LocationSearching,
                label = if (hasLocationPermission) tr("Lokasi Saya", "My Location") else tr("Aktifkan GPS", "Enable GPS"),
                onClick = {
                    if (!hasLocationPermission) {
                        onRequestLocation()
                    } else if (userLocation != null) {
                        mapView.controller.animateTo(userLocation, 18.2, 450L)
                    }
                },
            )
        }

        Spacer(Modifier.height(10.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFC4D6CD), RoundedCornerShape(14.dp)),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView },
                update = { view ->
                    view.overlays.clear()

                    bins.forEach { bin ->
                        val pos = bin.toGeoPoint()
                        val isRecommended = bin.binType == recommendedType
                        val isSelected = bin.id == selectedId
                        val dist = userLocation?.let { distanceMeters(it, pos) }
                        val marker = Marker(view).apply {
                            position = pos
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = bin.name
                            snippet = buildString {
                                append("${bin.areaLabel} • ${binLabel(bin.binType)}")
                                if (dist != null) append(" (~$dist m)")
                                if (isRecommended) append(tr(" ★ Rekomendasi", " ★ Recommended"))
                            }
                            icon = createBinMarkerDrawable(
                                context = context,
                                fillColor = binColor(bin.binType),
                                label = bin.binType.first().uppercase(),
                                emphasized = isSelected || isRecommended,
                            )
                            setOnMarkerClickListener { m, _ ->
                                onSelect(bin)
                                m.showInfoWindow()
                                true
                            }
                        }
                        view.overlays.add(marker)
                    }

                    if (userLocation != null) {
                        val userMarker = Marker(view).apply {
                            position = userLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = tr("Lokasi Kamu", "Your Location")
                            snippet = tr("Posisi GPS saat ini", "Current GPS position")
                            icon = createUserLocationDrawable(context)
                        }
                        view.overlays.add(userMarker)
                    }

                    view.invalidate()
                },
            )
        }
    }
}

@Composable
private fun MapQuickChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = GreenSoft,
        border = BorderStroke(1.dp, GreenBorder),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, tint = GreenText, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(5.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = GreenText, fontWeight = FontWeight.SemiBold)
        }
    }
}
