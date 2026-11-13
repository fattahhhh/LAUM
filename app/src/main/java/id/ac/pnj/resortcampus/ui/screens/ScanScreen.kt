package id.ac.pnj.resortcampus.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.view.Surface
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.pnj.resortcampus.data.ai.MockAi
import id.ac.pnj.resortcampus.ui.ScanFlowViewModel
import id.ac.pnj.resortcampus.ui.components.SecondaryButton
import id.ac.pnj.resortcampus.ui.components.binLabel
import id.ac.pnj.resortcampus.ui.components.categoryIcon
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.Green950
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Mint50
import java.io.File
import java.io.FileOutputStream
import androidx.camera.core.ImageAnalysis
import id.ac.pnj.resortcampus.util.LiveDetection
import id.ac.pnj.resortcampus.util.LiveWasteAnalyzer
import java.util.concurrent.Executors

enum class ScanMode { WASTE, BIN }

@Composable
fun ScanScreen(
    initialMode: ScanMode,
    scanFlow: ScanFlowViewModel,
    onBack: () -> Unit,
    onWastePhoto: (Uri) -> Unit,
    onBinPhoto: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pending by scanFlow.pending.collectAsStateWithLifecycle()
    var mode by rememberSaveable { mutableStateOf(initialMode) }
    var selectedSampleIdx by rememberSaveable { mutableIntStateOf(0) }
    var showGallerySheet by rememberSaveable { mutableStateOf(false) }

    var hasCameraPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Cek ulang izin kamera saat kembali dari halaman Pengaturan HP
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCameraPerm = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var askedOnce by rememberSaveable { mutableStateOf(false) }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPerm = granted
        if (!granted && askedOnce) {
            runCatching {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null),
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
        askedOnce = true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPerm) {
            permLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val deliver: (Uri) -> Unit = { uri ->
        if (mode == ScanMode.WASTE) onWastePhoto(uri) else onBinPhoto(uri)
    }

    val systemGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            showGallerySheet = false
            deliver(uri)
        }
    }

    Box(Modifier.fillMaxSize().background(Green950)) {
        CameraXCaptureContent(
            mode = mode,
            hasCameraPerm = hasCameraPerm,
            canVerifyBin = pending != null,
            pendingItemName = pending?.itemName,
            pendingBinType = pending?.binType,
            onModeChange = { mode = it },
            onBack = onBack,
            onPhotoCaptured = deliver,
            onOpenGallery = { showGallerySheet = true },
            onRequestPerm = { permLauncher.launch(Manifest.permission.CAMERA) },
        )

        // Modal Galeri "Pilih foto..." sesuai Figma (UI • Galeri)
        if (showGallerySheet) {
            GalleryPickerSheet(
                mode = mode,
                selectedIdx = selectedSampleIdx,
                onSelectIdx = { selectedSampleIdx = it },
                onConfirmSample = {
                    showGallerySheet = false
                    val uri = if (mode == ScanMode.WASTE) {
                        Uri.parse("resort://sample/$selectedSampleIdx")
                    } else {
                        Uri.parse("resort://bin")
                    }
                    deliver(uri)
                },
                onOpenSystemGallery = {
                    systemGalleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onDismiss = { showGallerySheet = false },
            )
        }
    }
}

@Composable
private fun CameraXCaptureContent(
    mode: ScanMode,
    hasCameraPerm: Boolean,
    canVerifyBin: Boolean,
    pendingItemName: String?,
    pendingBinType: String?,
    onModeChange: (ScanMode) -> Unit,
    onBack: () -> Unit,
    onPhotoCaptured: (Uri) -> Unit,
    onOpenGallery: () -> Unit,
    onRequestPerm: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var preferFront by rememberSaveable { mutableStateOf(false) }
    var torchOn by rememberSaveable { mutableStateOf(false) }
    var hasFlashUnit by remember { mutableStateOf(false) }
    var taking by remember { mutableStateOf(false) }
    var isStreaming by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    // PreviewView CameraX: gunakan COMPATIBLE (TextureView) agar stabil di dalam Compose Box & Emulator
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // UseCase ImageCapture CameraX untuk pengambilan gambar nyata (real photo capture)
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var imageCaptureBound by remember { mutableStateOf(false) }
    var liveHint by remember { mutableStateOf<LiveDetection?>(null) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember { LiveWasteAnalyzer { liveHint = it } }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { it.setAnalyzer(analysisExecutor, analyzer) }
    }

    // Analisis hanya aktif di mode Sampah
    LaunchedEffect(mode) {
        analyzer.enabled = mode == ScanMode.WASTE
        if (mode != ScanMode.WASTE) liveHint = null
    }
    DisposableEffect(Unit) {
        onDispose {
            imageAnalysis.clearAnalyzer()
            analyzer.close()
            analysisExecutor.shutdown()
        }
    }

    // Pantau status streaming nyata dari PreviewView CameraX
    DisposableEffect(lifecycleOwner, previewView) {
        val observer = Observer<PreviewView.StreamState> { state ->
            isStreaming = (state == PreviewView.StreamState.STREAMING)
        }
        previewView.previewStreamState.observe(lifecycleOwner, observer)
        onDispose {
            previewView.previewStreamState.removeObserver(observer)
        }
    }

    // Bind CameraX ProcessCameraProvider ke LifecycleOwner
    DisposableEffect(lifecycleOwner, hasCameraPerm, preferFront) {
        if (!hasCameraPerm) {
            camera = null
            imageCaptureBound = false
            return@DisposableEffect onDispose {}
        }

        cameraError = null
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)
        var boundProvider: ProcessCameraProvider? = null
        var disposed = false

        cameraProviderFuture.addListener({
            if (disposed) return@addListener
            runCatching {
                val cameraProvider = cameraProviderFuture.get()
                boundProvider = cameraProvider
                cameraProvider.unbindAll()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                // Urutan kandidat selector: lensa pilihan -> lensa sebaliknya -> kamera apa saja yang tersedia
                val primarySelector = if (preferFront) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                val secondarySelector = if (preferFront) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
                val anySelector = CameraSelector.Builder().build()

                val candidateSelectors = listOf(primarySelector, secondarySelector, anySelector)
                var boundCam: Camera? = null
                var boundWithImageCapture = false

                for (selector in candidateSelectors) {
                    // 0. Preview + ImageCapture + ImageAnalysis (scan realtime)
                    val attemptLive = runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture, imageAnalysis)
                    }.getOrNull()
                    if (attemptLive != null) {
                        boundCam = attemptLive
                        boundWithImageCapture = true
                        break
                    }
                    // 1. Coba bind Preview + ImageCapture bersamaan
                    val attemptFull = runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                    }.getOrNull()

                    if (attemptFull != null) {
                        boundCam = attemptFull
                        boundWithImageCapture = true
                        break
                    }

                    // 2. Fallback untuk kamera LEGACY / webcam tunggal: bind Preview saja
                    val attemptPreviewOnly = runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                    }.getOrNull()

                    if (attemptPreviewOnly != null) {
                        boundCam = attemptPreviewOnly
                        boundWithImageCapture = false
                        break
                    }
                }

                if (boundCam != null) {
                    camera = boundCam
                    imageCaptureBound = boundWithImageCapture
                    hasFlashUnit = boundCam.cameraInfo.hasFlashUnit()
                    torchOn = false
                    cameraError = null
                } else {
                    camera = null
                    imageCaptureBound = false
                    cameraError = "Kamera tidak ditemukan di perangkat/emulator ini."
                }
            }.onFailure { err ->
                camera = null
                imageCaptureBound = false
                cameraError = "Gagal membuka CameraX: ${err.localizedMessage ?: "cek pengaturan kamera AVD"}"
            }
        }, mainExecutor)

        onDispose {
            runCatching { boundProvider?.unbindAll() }
        }
    }

    // Fungsi capture gambar nyata dengan CameraX ImageCapture.takePicture
    val triggerRealCameraXCapture: () -> Unit = {
        if (!taking && hasCameraPerm) {
            taking = true
            cameraError = null
            captureRealPhotoWithCameraX(
                context = context,
                imageCapture = imageCapture,
                imageCaptureBound = imageCaptureBound,
                previewView = previewView,
                onSuccess = { realUri ->
                    taking = false
                    onPhotoCaptured(realUri)
                },
                onError = { msg ->
                    taking = false
                    cameraError = msg
                },
            )
        } else if (!hasCameraPerm) {
            onRequestPerm()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Green950)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Header atas sesuai Figma: Tombol Back, judul "AI Scan", dan switcher mode
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f)),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }

            Text(
                text = "AI Scan",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            // Switcher Sampah / Tong
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(3.dp),
            ) {
                ModeChip(
                    label = "Sampah",
                    active = mode == ScanMode.WASTE,
                    enabled = true,
                    onClick = { onModeChange(ScanMode.WASTE) },
                )
                ModeChip(
                    label = "Tong",
                    active = mode == ScanMode.BIN,
                    enabled = canVerifyBin,
                    onClick = { onModeChange(ScanMode.BIN) },
                )
            }
        }

        // Area Viewfinder Tengah (sesuai Figma UI • AI Scan)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF10281E)),
        ) {
            // PENTING: PreviewView selalu terpasang di dalam AndroidView selama izin kamera diberikan,
            // sehingga PreviewView memiliki Window & Display aktif saat CameraX meminta Surface!
            if (hasCameraPerm) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        view.display?.let { d -> imageCapture.targetRotation = d.rotation }
                    },
                )
            }

            // Status ketika izin belum diberikan atau kamera sedang inisialisasi / error
            if (!hasCameraPerm) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    RecycleBottleIllustration(modifier = Modifier.size(148.dp))
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Izin Kamera Diperlukan",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Berikan izin kamera agar CameraX dapat menampilkan preview langsung dan memotret gambar secara real.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        onClick = onRequestPerm,
                        shape = CircleShape,
                        color = Green500,
                    ) {
                        Text(
                            "Izinkan Kamera Sekarang",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        )
                    }
                }
            } else if (cameraError != null) {
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.72f))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        cameraError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        onClick = { preferFront = !preferFront },
                        shape = CircleShape,
                        color = Green500,
                    ) {
                        Text(
                            "Coba Ganti Lensa (Depan/Belakang)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
            } else if (!isStreaming) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = Green500, strokeWidth = 3.dp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Membuka CameraX...",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }
            }

            // Garis pemindai laser hijau + 4 Sudut Bingkai Pemandu (`[  ]` persis Figma)
            ScannerLaserAndBracketsOverlay(Modifier.fillMaxSize())

            // Indikator status CameraX di bagian atas viewfinder
            Surface(
                shape = CircleShape,
                color = Green900.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp, start = 16.dp, end = 16.dp),
            ) {
                val hint = when {
                    mode == ScanMode.WASTE && isStreaming ->
                        liveHint?.let { "Terdeteksi: ${it.name} (${(it.confidence * 100).toInt()}%) • tekan potret" }
                            ?: "Arahkan ke objek sampah lalu potret"
                    mode == ScanMode.BIN && isStreaming -> "CameraX Aktif • Potret tong ${binLabel(pendingBinType ?: "anorganik")}"
                    mode == ScanMode.BIN && pendingItemName != null -> "Buang $pendingItemName ke tong ${binLabel(pendingBinType ?: "anorganik")}"
                    else -> "Siapkan objek di dalam bingkai"
                }
                Text(
                    hint,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
            }

            // Tombol Pill "Ambil Foto Sampah" / "Foto Tong Sampah" di bagian bawah viewfinder sesuai Figma
            Surface(
                onClick = triggerRealCameraXCapture,
                shape = CircleShape,
                color = Green900,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
            ) {
                Text(
                    text = when {
                        taking -> "Mengambil Foto..."
                        mode == ScanMode.WASTE -> "Ambil Foto Sampah"
                        else -> "Foto Tong Sampah"
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                )
            }
        }

        // Kontrol kamera bawah sesuai Figma: Galeri (kiri), Shutter Bulat Putih (tengah), Flash/Putar (kanan)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomPillAction(
                label = "Galeri",
                icon = Icons.Filled.PhotoLibrary,
                active = false,
                onClick = onOpenGallery,
            )

            // Tombol Shutter Bulat Putih CameraX
            Box(
                Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(if (taking) Color.LightGray else Color.White)
                    .clickable(enabled = !taking, onClick = triggerRealCameraXCapture),
                contentAlignment = Alignment.Center,
            ) {
                if (taking) {
                    CircularProgressIndicator(Modifier.size(26.dp), color = Green900, strokeWidth = 3.dp)
                } else {
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = "Potret CameraX",
                        tint = Green900,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            if (hasFlashUnit) {
                BottomPillAction(
                    label = if (torchOn) "Flash On" else "Flash",
                    icon = if (torchOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    active = torchOn,
                    onClick = {
                        val next = !torchOn
                        torchOn = next
                        camera?.cameraControl?.enableTorch(next)
                    },
                )
            } else {
                BottomPillAction(
                    label = "Putar",
                    icon = Icons.Filled.Cameraswitch,
                    active = preferFront,
                    onClick = { preferFront = !preferFront },
                )
            }
        }
    }
}

/**
 * Mengambil foto nyata menggunakan CameraX `ImageCapture.takePicture(OutputFileOptions, ...)`.
 * Jika hardware/emulator gagal pada still capture pipeline, otomatis mengambil frame nyata
 * yang sedang tampil di `PreviewView` CameraX (`previewView.bitmap`).
 */
private fun captureRealPhotoWithCameraX(
    context: Context,
    imageCapture: ImageCapture,
    imageCaptureBound: Boolean,
    previewView: PreviewView,
    onSuccess: (Uri) -> Unit,
    onError: (String) -> Unit,
) {
    val photoFile = runCatching {
        File.createTempFile("camerax_capture_", ".jpg", context.cacheDir)
    }.getOrNull()

    if (photoFile == null) {
        onError("Gagal membuat file foto sementara di penyimpanan.")
        return
    }

    // Fungsi cadangan untuk menyimpan frame nyata dari PreviewView CameraX jika ImageCapture HAL error di emulator
    fun saveFromLivePreviewViewOrFail(cause: String) {
        val liveFrame: Bitmap? = runCatching { previewView.bitmap }.getOrNull()
        if (liveFrame != null) {
            val saved = runCatching {
                FileOutputStream(photoFile).use { out ->
                    liveFrame.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }
                true
            }.getOrDefault(false)
            if (saved && photoFile.length() > 0L) {
                onSuccess(Uri.fromFile(photoFile))
                return
            }
        }
        runCatching { photoFile.delete() }
        onError(cause)
    }

    if (!imageCaptureBound) {
        saveFromLivePreviewViewOrFail("Kamera belum siap memotret. Tunggu preview kamera tampil.")
        return
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(Uri.fromFile(photoFile))
            }

            override fun onError(exception: ImageCaptureException) {
                saveFromLivePreviewViewOrFail(
                    "Gagal mengambil foto (${exception.message ?: "error kamera"})."
                )
            }
        },
    )
}

// ==========================================
// SHEET GALERI "Pilih foto..." (UI • Galeri di Figma)
// ==========================================
@Composable
private fun GalleryPickerSheet(
    mode: ScanMode,
    selectedIdx: Int,
    onSelectIdx: (Int) -> Unit,
    onConfirmSample: () -> Unit,
    onOpenSystemGallery: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Green900)
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Pilih foto...",
                            style = MaterialTheme.typography.titleMedium,
                            color = Green900,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Surface(
                        onClick = onConfirmSample,
                        shape = CircleShape,
                        color = Green900,
                    ) {
                        Text(
                            "Lanjut",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                val items = MockAi.SAMPLES
                for (row in items.chunked(3)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        row.forEach { sample ->
                            val idx = items.indexOf(sample)
                            val isSelected = idx == selectedIdx
                            Column(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(0.95f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) GreenSoft else Mint50)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Green500 else Line,
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                    .clickable { onSelectIdx(idx) }
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Box(
                                    Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, GreenBorder, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        if (mode == ScanMode.BIN) Icons.Filled.Delete else categoryIcon(sample.category),
                                        contentDescription = null,
                                        tint = GreenText,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    if (mode == ScanMode.BIN) "Tong ${binLabel(sample.binType)}" else sample.itemName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Green900,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                )
                                if (isSelected) {
                                    Spacer(Modifier.height(2.dp))
                                    Icon(Icons.Filled.CheckCircle, null, tint = Green500, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(6.dp))
                SecondaryButton(
                    text = "Buka Galeri File HP",
                    onClick = onOpenSystemGallery,
                    icon = Icons.Filled.FolderOpen,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ScannerLaserAndBracketsOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scannerLaser")
    val laserProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "laserY",
    )

    Canvas(modifier) {
        val boxSide = size.minDimension * 0.68f
        val left = (size.width - boxSide) / 2f
        val top = (size.height - boxSide) / 2f - 10.dp.toPx()
        val right = left + boxSide
        val bottom = top + boxSide
        val len = 28.dp.toPx()
        val stroke = 3.dp.toPx()
        val c = Color.White.copy(alpha = 0.85f)

        // Kiri atas
        drawLine(c, Offset(left, top), Offset(left + len, top), stroke, StrokeCap.Round)
        drawLine(c, Offset(left, top), Offset(left, top + len), stroke, StrokeCap.Round)
        // Kanan atas
        drawLine(c, Offset(right, top), Offset(right - len, top), stroke, StrokeCap.Round)
        drawLine(c, Offset(right, top), Offset(right, top + len), stroke, StrokeCap.Round)
        // Kiri bawah
        drawLine(c, Offset(left, bottom), Offset(left + len, bottom), stroke, StrokeCap.Round)
        drawLine(c, Offset(left, bottom), Offset(left, bottom - len), stroke, StrokeCap.Round)
        // Kanan bawah
        drawLine(c, Offset(right, bottom), Offset(right - len, bottom), stroke, StrokeCap.Round)
        drawLine(c, Offset(right, bottom), Offset(right, bottom - len), stroke, StrokeCap.Round)

        // Garis laser pemindai bergerak
        val laserY = top + (bottom - top) * laserProgress
        drawLine(
            color = Green500.copy(alpha = 0.85f),
            start = Offset(left + 10.dp.toPx(), laserY),
            end = Offset(right - 10.dp.toPx(), laserY),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun ModeChip(label: String, active: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(if (active) Green500 else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            color = if (!enabled) Color.White.copy(alpha = 0.4f) else Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BottomPillAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (active) Green500 else Color.White.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
