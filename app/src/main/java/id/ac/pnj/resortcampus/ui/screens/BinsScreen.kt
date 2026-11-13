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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.roundToInt

private val PNJ_CENTER = GeoPoint(-6.371551, 106.824218)

private val PNJ_BIN_COORDINATES: Map<String, GeoPoint> = mapOf(
    "bin-1" to GeoPoint(-6.370832, 106.824621), // Tong Depan Gedung Direktorat
    "bin-2" to GeoPoint(-6.371780, 106.824520), // Tong Kantin Pusat
    "bin-3" to GeoPoint(-6.371120, 106.824950), // Tong Perpustakaan
    "bin-4" to GeoPoint(-6.372150, 106.823680), // Tong Parkiran Motor
    "bin-5" to GeoPoint(-6.372280, 106.824650), // Tong Lapangan Olahraga
    "bin-6" to GeoPoint(-6.371450, 106.823850), // Drop Box B3 Gedung TIK
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
            bins.firstOrNull { it.binType == pending?.binType }?.id ?: bins.firstOrNull()?.id
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

    MainScaffold(nav = nav, current = Routes.BINS) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenHeader("Lokasi Tong PNJ")
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
                                "Sampahmu: ${p.itemName} -> Tong ${binLabel(p.binType)}",
                                style = MaterialTheme.typography.titleSmall,
                                color = GreenText,
                            )
                            Text(
                                "Datangi tong yang ditandai di peta, buang sampahnya, lalu tekan tombol di bawah untuk memotret tong.",
                                style = MaterialTheme.typography.bodySmall,
                                color = GreenText.copy(alpha = 0.85f),
                            )
                            Spacer(Modifier.height(10.dp))
                            PrimaryButton(
                                text = "Foto Tong untuk Klaim Poin",
                                onClick = openScan,
                                icon = Icons.Filled.PhotoCamera,
                                modifier = Modifier.fillMaxWidth(),
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
                        "Pilah di tempat yang tepat",
                        style = MaterialTheme.typography.titleMedium,
                        color = Green900,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (pending != null) {
                            "Buang ${pending!!.itemName} ke tong ${binLabel(pending!!.binType)} terdekat, lalu potret tongnya untuk klaim poin."
                        } else {
                            "Pilih titik tong sampah terdekat di kampus PNJ, lalu pindai sampah atau verifikasi pembuanganmu."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted,
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(
                        text = if (pending != null) "Lanjut verifikasi pembuangan" else "Mulai Scan Sampah",
                        onClick = openScan,
                        icon = Icons.Filled.PhotoCamera,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Text("Daftar titik tong sampah", style = MaterialTheme.typography.titleSmall, color = Green900)
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
                                        if (isRecommended) append(" • Cocok untuk sampahmu")
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
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
        }
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.5)
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
        mapView.controller.animateTo(target, 18.2, 450L)
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
                Text("Peta Kampus PNJ (Real-Time)", style = MaterialTheme.typography.titleSmall, color = Green900)
            }
            Text("Ketuk pin", style = MaterialTheme.typography.labelSmall, color = Muted)
        }
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MapQuickChip(
                icon = Icons.Filled.School,
                label = "Kampus PNJ",
                onClick = {
                    mapView.controller.animateTo(PNJ_CENTER, 17.5, 450L)
                },
            )
            MapQuickChip(
                icon = Icons.Filled.LocationSearching,
                label = if (hasLocationPermission) "Lokasi Saya" else "Aktifkan GPS",
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
                                if (isRecommended) append(" ★ Rekomendasi")
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
                            title = "Lokasi Kamu"
                            snippet = "Posisi GPS saat ini"
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
