package id.ac.pnj.resortcampus.ui.screens

import android.Manifest
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.pnj.resortcampus.R
import id.ac.pnj.resortcampus.ui.AuthViewModel
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.MessageBanner
import id.ac.pnj.resortcampus.ui.components.PrimaryButton
import id.ac.pnj.resortcampus.ui.components.shimmerSweep
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Mint50
import id.ac.pnj.resortcampus.ui.theme.Muted
import id.ac.pnj.resortcampus.util.AppLanguage
import id.ac.pnj.resortcampus.util.NotifType
import id.ac.pnj.resortcampus.util.NotificationHelper
import id.ac.pnj.resortcampus.util.SettingsStore
import id.ac.pnj.resortcampus.util.appStrings
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay

private class DesignScale(val k: Float, val v: Float) {
    fun x(d: Float): Dp = (d * k).dp
    fun y(d: Float): Dp = (d * v).dp
    fun t(d: Float): TextUnit = (d * k).sp
}

@Composable
private fun Modifier.appear(
    visible: Boolean,
    delayMs: Int,
    offsetY: Float = 24f,
    startScale: Float = 1f,
): Modifier {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = delayMs, easing = FastOutSlowInEasing),
        label = "appear",
    )
    return this.graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * offsetY * density
        val sc = startScale + (1f - startScale) * progress
        scaleX = sc
        scaleY = sc
    }
}

private val ParticleX = floatArrayOf(0.30f, 0.40f, 0.47f, 0.55f, 0.63f, 0.70f, 0.36f, 0.58f)
private val ParticleSpeed = intArrayOf(2, 3, 2, 4, 3, 2, 4, 3)
private val ParticleOffset = floatArrayOf(0.00f, 0.35f, 0.70f, 0.15f, 0.50f, 0.85f, 0.60f, 0.25f)
private val ParticleSize = floatArrayOf(4f, 6f, 3f, 5f, 7f, 4f, 3f, 5f)

@Composable
fun OnboardingScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onSettings: () -> Unit = {},
) {
    val s = appStrings()
    val strokeGreen = Color(0xFF9DB1A5)
    val bottleFill = Color(0xFFEEF6F1)

    var started by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        started = true
        delay(1000)
        showButton = true
    }

    val infinite = rememberInfiniteTransition(label = "onboarding")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "phase",
    )
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.045f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    val dividerFraction by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(900, delayMillis = 350, easing = FastOutSlowInEasing),
        label = "divider",
    )

    val buttonInteraction = remember { MutableInteractionSource() }
    val buttonPressed by buttonInteraction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        label = "press",
    )
    val buttonIn by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "buttonIn",
    )

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Mint50),
    ) {
        val d = DesignScale(maxWidth.value / 390f, maxHeight.value / 844f)

        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val angle = phase * 2f * PI.toFloat()

            val glowCenter = Offset(w * 0.5f, h * (0.25f + 0.01f * sin(angle * 2f)))
            val glowRadius = w * (0.60f + 0.03f * sin(angle * 3f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFB9E6C9).copy(alpha = 0.55f), Color.Transparent),
                    center = glowCenter,
                    radius = glowRadius,
                ),
                radius = glowRadius,
                center = glowCenter,
            )

            val blob1 = Offset(w * (0.92f + 0.04f * cos(angle)), h * (0.55f + 0.03f * sin(angle)))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFCDEFD9).copy(alpha = 0.55f), Color.Transparent),
                    center = blob1,
                    radius = w * 0.45f,
                ),
                radius = w * 0.45f,
                center = blob1,
            )

            val blob2 = Offset(w * (0.04f + 0.04f * sin(angle)), h * (0.47f + 0.03f * cos(angle)))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFCDEFD9).copy(alpha = 0.5f), Color.Transparent),
                    center = blob2,
                    radius = w * 0.38f,
                ),
                radius = w * 0.38f,
                center = blob2,
            )

            for (i in ParticleX.indices) {
                val speed = ParticleSpeed[i].toFloat()
                val p = (phase * speed + ParticleOffset[i]) % 1f
                val px = w * (ParticleX[i] + 0.025f * sin(angle * speed + i))
                val py = h * (0.90f - 0.40f * p)
                val a = (1f - p) * minOf(p * 8f, 1f) * 0.55f
                drawCircle(
                    color = Green500.copy(alpha = a),
                    radius = ParticleSize[i] * density,
                    center = Offset(px, py),
                )
            }
        }

        Canvas(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(d.y(130f))
                .appear(started, 200, offsetY = 70f),
        ) {
            val w = size.width
            val h = size.height
            val neckL = w * 0.282f
            val neckR = w * 0.710f
            val baseL = w * 0.145f
            val baseR = w * 0.855f
            val neckEnd = h * 0.45f

            val fillPath = Path()
            fillPath.moveTo(baseL, h)
            fillPath.cubicTo(w * 0.19f, h * 0.80f, neckL, h * 0.70f, neckL, neckEnd)
            fillPath.lineTo(neckL, 0f)
            fillPath.lineTo(neckR, 0f)
            fillPath.lineTo(neckR, neckEnd)
            fillPath.cubicTo(neckR, h * 0.70f, w * 0.81f, h * 0.80f, baseR, h)
            fillPath.close()
            drawPath(path = fillPath, color = bottleFill)

            val linePath = Path()
            linePath.moveTo(baseL, h)
            linePath.cubicTo(w * 0.19f, h * 0.80f, neckL, h * 0.70f, neckL, neckEnd)
            linePath.lineTo(neckL, 0f)
            linePath.moveTo(neckR, 0f)
            linePath.lineTo(neckR, neckEnd)
            linePath.cubicTo(neckR, h * 0.70f, w * 0.81f, h * 0.80f, baseR, h)
            drawPath(
                path = linePath,
                color = strokeGreen,
                style = Stroke(width = 3.5f * d.k * density, cap = StrokeCap.Round),
            )
        }

        val moreShape = RoundedCornerShape(d.x(12f))
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = d.y(57f), end = d.x(24f))
                .size(d.x(40f))
                .appear(started, 100, offsetY = 0f, startScale = 0.4f)
                .shadow(6.dp, moreShape)
                .clip(moreShape)
                .background(Color(0xFFB9E6C9))
                .clickable { onSettings() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.MoreHoriz,
                contentDescription = s.settingsCd,
                tint = Color.White,
                modifier = Modifier.size(d.x(22f)),
            )
        }

        val logoBitmap = ImageBitmap.imageResource(id = R.drawable.conregis)
        Canvas(
            Modifier
                .offset(x = d.x(64.5f), y = d.y(112f))
                .width(d.x(261f))
                .aspectRatio(logoBitmap.width.toFloat() / logoBitmap.height.toFloat()),
        ) {
            drawImage(
                image = logoBitmap,
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                blendMode = BlendMode.Multiply,
                filterQuality = FilterQuality.High,
            )
        }

        Box(
            Modifier
                .offset(x = d.x(45f), y = d.y(391f))
                .width(d.x(298f) * dividerFraction)
                .height(1.dp)
                .background(Green900),
        )

        Column(
            modifier = Modifier
                .offset(x = d.x(45f), y = d.y(406f))
                .width(d.x(300f)),
        ) {
            listOf(s.headline1, s.headline2, s.headline3).forEachIndexed { i, line ->
                Text(
                    text = line,
                    fontSize = d.t(30f),
                    lineHeight = d.t(41f),
                    fontWeight = FontWeight.ExtraBold,
                    color = Green900,
                    maxLines = 1,
                    modifier = Modifier.appear(started, 450 + i * 150, offsetY = 22f),
                )
            }
        }

        Text(
            text = s.onboardingDesc,
            fontSize = d.t(13f),
            lineHeight = d.t(19f),
            color = Muted,
            modifier = Modifier
                .offset(x = d.x(45f), y = d.y(535f))
                .width(d.x(300f))
                .appear(started, 900, offsetY = 16f),
        )

        Column(
            modifier = Modifier
                .offset(x = d.x(80f), y = d.y(626f))
                .width(d.x(228f)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onRegister,
                interactionSource = buttonInteraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(d.x(88f))
                    .graphicsLayer {
                        val sc = (0.7f + 0.3f * buttonIn) * pulse * pressScale
                        scaleX = sc
                        scaleY = sc
                        alpha = buttonIn.coerceIn(0f, 1f)
                    }
                    .clip(RoundedCornerShape(d.x(26f)))
                    .shimmerSweep(alpha = 0.25f, durationMillis = 2400, pauseMillis = 700),
                shape = RoundedCornerShape(d.x(26f)),
                colors = ButtonDefaults.buttonColors(containerColor = Green900),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            ) {
                Text(
                    text = s.getStarted,
                    fontSize = d.t(22f),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }

            Box(
                modifier = Modifier
                    .width(d.x(185f))
                    .height(d.x(30f))
                    .appear(showButton, 200, offsetY = 10f)
                    .background(Color(0xFFF1F8F4))
                    .border(2.dp, strokeGreen, RoundedCornerShape(2.dp))
                    .clickable { onLogin() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = s.haveAccount,
                    color = Green900,
                    fontSize = d.t(13f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun RecycleBottleIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.7f))
            .border(1.5.dp, GreenBorder, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            val strokeWidth = 5.dp.toPx()
            val diameter = size.minDimension
            val radius = diameter / 2f
            val arcSize = Size(diameter, diameter)
            val center = Offset(size.width / 2f, size.height / 2f)

            for (start in listOf(0f, 120f, 240f)) {
                drawArc(
                    color = Green500,
                    startAngle = start,
                    sweepAngle = 82f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
        }
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, GreenBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.LocalDrink,
                contentDescription = "Recycle Bottle",
                tint = Green900,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

private val FieldFill = Color(0xFFF1F8F4)
private val CloseFill = Color(0xFFB9E6C9)
private val CampusOptions = listOf("Politeknik Negeri Jakarta")

private fun tr(id: String, en: String): String =
    if (SettingsStore.language == AppLanguage.EN) en else id

@Composable
private fun AuthShell(onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Green900)
            .statusBarsPadding()
            .imePadding(),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "LAUM",
                modifier = Modifier.height(150.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Mint50,
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(top = 36.dp, bottom = 24.dp),
                    content = content,
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 20.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CloseFill),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = tr("Tutup", "Close"),
                        tint = Green900,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FieldFill,
    unfocusedContainerColor = FieldFill,
    focusedBorderColor = Green500,
    unfocusedBorderColor = GreenBorder,
)

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Green900,
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun LaunchField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    secret: Boolean = false,
) {
    FieldLabel(label)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Muted.copy(alpha = 0.7f), fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Green900),
        colors = fieldColors(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(14.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchDropdown(
    label: String,
    value: String?,
    placeholder: String,
    options: List<String>,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    FieldLabel(label)
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value.orEmpty(),
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = Muted.copy(alpha = 0.7f), fontSize = 14.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(14.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Green900),
            colors = fieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    },
                )
            }
        }
    }
    Spacer(Modifier.height(14.dp))
}

@Composable
fun LoginScreen(
    onClose: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogin: (username: String, password: String, done: (String?) -> Unit) -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AuthShell(onClose = onClose) {
        Text(
            text = tr("Selamat datang kembali,", "Welcome back,"),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Green900,
            modifier = Modifier.padding(end = 48.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tr("Masuk untuk lanjut mengumpulkan poin jurusanmu.", "Sign in to keep earning points for your major."),
            fontSize = 13.sp,
            color = Muted,
        )
        Spacer(Modifier.height(20.dp))

        error?.let {
            MessageBanner(it, isError = true)
            Spacer(Modifier.height(12.dp))
        }

        LaunchField(
            label = "Username",
            value = username,
            onValueChange = { username = it; error = null },
            placeholder = tr("Username Anda", "Your username"),
        )
        LaunchField(
            label = "Password",
            value = password,
            onValueChange = { password = it; error = null },
            placeholder = tr("Masukkan password", "Enter password"),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            secret = true,
        )

        Spacer(Modifier.height(10.dp))

        PrimaryButton(
            text = tr("Masuk", "Sign In"),
            onClick = {
                loading = true
                error = null
                onLogin(username.trim(), password) { err ->
                    loading = false
                    error = err
                }
            },
            loading = loading,
            enabled = username.isNotBlank() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        )

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(tr("Belum punya akun? ", "Don't have an account? "), fontSize = 12.sp, color = Muted)
            Text(
                text = tr("Daftar", "Sign Up"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Green900,
                modifier = Modifier.clickable { onRegisterClick() },
            )
        }
    }
}

@Composable
fun RegisterScreen(
    onClose: () -> Unit,
    onRegister: (
        username: String,
        password: String,
        campus: String,
        major: String,
        email: String,
        done: (String?) -> Unit,
    ) -> Unit,
) {
    val vm = appViewModel { AuthViewModel(it) }
    val vmState by vm.state.collectAsStateWithLifecycle()
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var campus by rememberSaveable { mutableStateOf<String?>(null) }
    var major by rememberSaveable { mutableStateOf<String?>(null) }
    var email by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val majorNames = vmState.majors.map { it.name }
    val ready = username.isNotBlank() && password.isNotEmpty() && confirm.isNotEmpty() &&
            campus != null && major != null && email.isNotBlank()

    AuthShell(onClose = onClose) {
        Text(
            text = tr("Selamat datang,", "Welcome,"),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Green900,
            modifier = Modifier.padding(end = 48.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tr("Mulai kontribusi hijaumu dari sini.", "Start your green contribution here."),
            fontSize = 13.sp,
            color = Muted,
        )
        Spacer(Modifier.height(20.dp))

        error?.let {
            MessageBanner(it, isError = true)
            Spacer(Modifier.height(12.dp))
        }

        LaunchField(
            label = tr("Buat username", "Create username"),
            value = username,
            onValueChange = { username = it; error = null },
            placeholder = tr("Username Anda", "Your username"),
        )
        LaunchField(
            label = tr("Buat Password", "Create password"),
            value = password,
            onValueChange = { password = it; error = null },
            placeholder = tr("Minimal 8 karakter", "At least 8 characters"),
            keyboardType = KeyboardType.Password,
            secret = true,
        )
        LaunchField(
            label = tr("Konfirmasi Password anda", "Confirm your password"),
            value = confirm,
            onValueChange = { confirm = it; error = null },
            placeholder = tr("Ulangi password", "Repeat password"),
            keyboardType = KeyboardType.Password,
            secret = true,
        )
        LaunchDropdown(
            label = tr("Nama Kampus", "Campus name"),
            value = campus,
            placeholder = tr("Pilih kampus", "Select campus"),
            options = CampusOptions,
            onSelect = { campus = CampusOptions[it]; error = null },
        )
        LaunchDropdown(
            label = tr("Jurusan", "Major"),
            value = major,
            placeholder = tr("Pilih jurusan", "Select major"),
            options = majorNames,
            onSelect = { major = majorNames[it]; error = null },
        )
        LaunchField(
            label = tr("Masukan email aktif Anda", "Enter your active email"),
            value = email,
            onValueChange = { email = it; error = null },
            placeholder = "nama@email.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
        )

        Spacer(Modifier.height(10.dp))

        PrimaryButton(
            text = tr("Buat Akun", "Create Account"),
            onClick = {
                val message = when {
                    password.length < 8 -> tr("Password minimal 8 karakter.", "Password must be at least 8 characters.")
                    password != confirm -> tr("Konfirmasi password tidak sama.", "Passwords do not match.")
                    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> tr("Format email tidak valid.", "Invalid email format.")
                    else -> null
                }
                error = message
                if (message == null) {
                    loading = true
                    onRegister(username.trim(), password, campus.orEmpty(), major.orEmpty(), email.trim()) { err ->
                        loading = false
                        error = err
                    }
                }
            },
            loading = loading,
            enabled = ready,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = tr(
                "Dengan mendaftar, Anda menyetujui ketentuan layanan.",
                "By registering, you agree to the terms of service.",
            ),
            fontSize = 11.sp,
            color = Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = tr("Bantuan?", "Need help?"),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Green900,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(onBack: () -> Unit) {
    val s = appStrings()
    val context = LocalContext.current
    var languageExpanded by remember { mutableStateOf(false) }
    var pendingType by remember { mutableStateOf<NotifType?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val type = pendingType
        pendingType = null
        if (type != null) {
            if (granted) {
                NotificationHelper.enable(context, type)
            } else {
                Toast.makeText(context, s.permissionDenied, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun toggle(type: NotifType, on: Boolean) {
        if (!on) {
            NotificationHelper.disable(context, type)
        } else if (NotificationHelper.hasPermission(context)) {
            NotificationHelper.enable(context, type)
        } else {
            pendingType = type
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Mint50)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f).padding(top = 28.dp)) {
                Text(
                    text = s.settingTitle,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Green900,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = s.settingSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                )
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFB9E6C9)),
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = s.close,
                    tint = Green900,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(s.languageLabel, style = MaterialTheme.typography.bodyMedium, color = Muted)
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = languageExpanded,
            onExpandedChange = { languageExpanded = it },
        ) {
            OutlinedTextField(
                value = SettingsStore.language.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(languageExpanded) },
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Green900,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Line,
                    unfocusedBorderColor = Line,
                ),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = languageExpanded,
                onDismissRequest = { languageExpanded = false },
            ) {
                AppLanguage.entries.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang.label, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            SettingsStore.changeLanguage(lang)
                            languageExpanded = false
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Line)
        Spacer(Modifier.height(20.dp))

        Text(
            text = s.notifications,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Green900,
        )
        Spacer(Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Line),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                NotificationRow(
                    title = s.reminderTitle,
                    desc = s.reminderDesc,
                    checked = SettingsStore.reminder,
                    onCheckedChange = { toggle(NotifType.REMINDER, it) },
                )
                NotificationRow(
                    title = s.competitionTitle,
                    desc = s.competitionDesc,
                    checked = SettingsStore.competition,
                    onCheckedChange = { toggle(NotifType.COMPETITION, it) },
                )
                NotificationRow(
                    title = s.achievementTitle,
                    desc = s.achievementDesc,
                    checked = SettingsStore.achievement,
                    onCheckedChange = { toggle(NotifType.ACHIEVEMENT, it) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Line)
    }
}

@Composable
private fun NotificationRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Green900,
            )
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Green500,
                checkedBorderColor = Color(0xFF3D3D3D),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF9DB1A5),
                uncheckedBorderColor = Color(0xFF3D3D3D),
            ),
        )
    }
}
