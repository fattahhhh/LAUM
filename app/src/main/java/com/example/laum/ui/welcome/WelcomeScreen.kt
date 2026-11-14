package com.example.laum.ui.welcome

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laum.R
import com.example.laum.ui.login.LoginColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val IllustrationBg = Color(0xFFEAF5EE)

/** Animasi masuk: fade + slide naik + sedikit zoom. */
@Composable
private fun Reveal(
    state: MutableTransitionState<Boolean>,
    delayMillis: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = state,
        modifier = modifier,
        enter = fadeIn(tween(600, delayMillis, FastOutSlowInEasing)) +
                slideInVertically(tween(650, delayMillis, FastOutSlowInEasing)) { it / 4 } +
                scaleIn(tween(650, delayMillis, FastOutSlowInEasing), initialScale = 0.94f)
    ) {
        content()
    }
}

/** Layar pembuka: logo muncul memantul, ring berdenyut, lalu naik meninggalkan layar. */
@Composable
private fun SplashOverlay(modifier: Modifier = Modifier) {
    val logoScale = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-180f) }
    val ring = remember { Animatable(1f) }
    val textAlpha = remember { Animatable(0f) }
    val textShift = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                1f,
                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
            )
        }
        launch { logoRotation.animateTo(0f, tween(900, easing = FastOutSlowInEasing)) }
        launch {
            delay(350)
            ring.animateTo(2.6f, tween(1100, easing = FastOutSlowInEasing))
        }
        launch {
            delay(500)
            launch { textAlpha.animateTo(1f, tween(600)) }
            textShift.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LoginColors.GreenPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                // ring berdenyut di belakang logo
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .graphicsLayer {
                            scaleX = ring.value
                            scaleY = ring.value
                            alpha = (0.35f * (1f - (ring.value - 1f) / 1.6f)).coerceIn(0f, 1f)
                        }
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .graphicsLayer {
                            scaleX = logoScale.value
                            scaleY = logoScale.value
                        }
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = null,
                        tint = LoginColors.GreenPrimary,
                        modifier = Modifier
                            .size(46.dp)
                            .graphicsLayer { rotationZ = logoRotation.value }
                    )
                }
            }
            Text(
                text = "ReSort Campus",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .graphicsLayer {
                        alpha = textAlpha.value
                        translationY = textShift.value
                    }
            )
            Text(
                text = "Untuk kampus lebih hijau",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .graphicsLayer {
                        alpha = textAlpha.value
                        translationY = textShift.value
                    }
            )
        }
    }
}

/** Tombol dengan efek menekan (spring) dan opsional kilau yang menyapu. */
@Composable
private fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    shimmer: Boolean = false
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "press"
    )

    val shimmerX = if (shimmer) {
        val t = rememberInfiniteTransition(label = "shimmer")
        t.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
            label = "shimmerX"
        ).value
    } else 0f

    val shape = RoundedCornerShape(14.dp)
    Button(
        onClick = onClick,
        interactionSource = interaction,
        shape = shape,
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(shape)
            .then(
                if (shimmer) Modifier.drawWithContent {
                    drawContent()
                    val w = size.width
                    val x = -w * 0.4f + w * 1.8f * shimmerX
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.28f),
                                Color.Transparent
                            ),
                            start = Offset(x, 0f),
                            end = Offset(x + w * 0.35f, size.height * 0.6f)
                        )
                    )
                } else Modifier
            )
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density

    var showSplash by remember { mutableStateOf(true) }
    var imageIn by remember { mutableStateOf(false) }
    val revealState = remember { MutableTransitionState(false) }

    // Urutan: splash -> splash keluar -> konten masuk berurutan
    LaunchedEffect(Unit) {
        delay(1900)
        showSplash = false
        delay(350)
        revealState.targetState = true
        delay(150)
        imageIn = true
    }

    val imageEntrance by animateFloatAsState(
        targetValue = if (imageIn) 1f else 0.4f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "imageEntrance"
    )

    val infinite = rememberInfiniteTransition(label = "float")
    val floatY by infinite.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val tilt by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val badgePulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgePulse"
    )

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginColors.WelcomeBg)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // ---------- Header ----------
            Reveal(revealState, 0, Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LoginColors.GreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(22.dp)
                                .graphicsLayer { rotationZ = tilt * 6f }
                        )
                    }
                    Text(
                        text = "ReSort Campus",
                        color = LoginColors.GreenPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .clickable {
                                Toast.makeText(context, "Menu belum tersedia", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val dots = rememberInfiniteTransition(label = "dots")
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(3) { index ->
                                val hop by dots.animateFloat(
                                    initialValue = 0f,
                                    targetValue = -4f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(450, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse,
                                        initialStartOffset = StartOffset(index * 160)
                                    ),
                                    label = "dot$index"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .graphicsLayer { translationY = hop * density }
                                        .clip(CircleShape)
                                        .background(LoginColors.GreenPrimary)
                                )
                            }
                        }
                    }
                }
            }

            // ---------- Ilustrasi ----------
            Reveal(revealState, 120, Modifier.padding(top = 24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(440f / 355f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(IllustrationBg)
                ) {
                    // lingkaran dekoratif yang melayang
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 24.dp, top = 28.dp)
                            .size(36.dp)
                            .graphicsLayer {
                                translationY = -floatY * density * 1.4f
                                alpha = 0.55f
                            }
                            .clip(CircleShape)
                            .background(LoginColors.GreenPrimary.copy(alpha = 0.25f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 20.dp, bottom = 60.dp)
                            .size(22.dp)
                            .graphicsLayer {
                                translationY = floatY * density * 1.8f
                                alpha = 0.7f
                            }
                            .clip(CircleShape)
                            .background(LoginColors.GreenPrimary.copy(alpha = 0.3f))
                    )
                    // lingkaran berdenyut di belakang gambar
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 36.dp)
                            .size(150.dp)
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                                alpha = 0.9f - (pulse - 0.85f) * 1.6f
                            }
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Image(
                        painter = painterResource(R.drawable.img_welcome),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 36.dp)
                            .fillMaxWidth(0.62f)
                            .graphicsLayer(
                                scaleX = 2.3f * imageEntrance,
                                scaleY = 2.3f * imageEntrance,
                                translationY = floatY * density,
                                rotationZ = tilt,
                                alpha = ((imageEntrance - 0.4f) / 0.6f).coerceIn(0f, 1f)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp)
                            .graphicsLayer {
                                scaleX = badgePulse
                                scaleY = badgePulse
                            }
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .padding(horizontal = 18.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = "UNTUK KAMPUS LEBIH HIJAU",
                            color = LoginColors.GreenPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ---------- Headline (muncul baris per baris) ----------
            Reveal(revealState, 260, Modifier.padding(top = 32.dp)) {
                Text(
                    text = "Pilah sampahmu.",
                    color = LoginColors.GreenPrimary,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Reveal(revealState, 340) {
                Text(
                    text = "Naikkan peringkat",
                    color = LoginColors.GreenPrimary,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Reveal(revealState, 420) {
                Text(
                    text = "jurusanmu.",
                    color = LoginColors.GreenPrimary,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Reveal(revealState, 520, Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Scan sampah dengan AI, pilah dengan tepat,\ndan ubah aksi kecilmu menjadi poin jurusan.",
                    color = LoginColors.TextSub,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            // ---------- Tombol ----------
            Reveal(revealState, 620) {
                AnimatedButton(
                    text = "Login",
                    onClick = onLoginClick,
                    containerColor = LoginColors.GreenPrimary,
                    contentColor = Color.White,
                    shimmer = true
                )
            }

            Reveal(revealState, 700, Modifier.padding(top = 12.dp)) {
                AnimatedButton(
                    text = "Register",
                    onClick = onRegisterClick,
                    containerColor = Color.White,
                    contentColor = LoginColors.GreenPrimary,
                    border = BorderStroke(1.dp, LoginColors.InputBorder)
                )
            }

            Reveal(revealState, 780, Modifier.padding(top = 24.dp, bottom = 16.dp)) {
                Text(
                    text = "Butuh bantuan?  Hubungi kami",
                    color = LoginColors.TextSub,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "Kontak belum tersedia", Toast.LENGTH_SHORT).show()
                        }
                )
            }
        }

        // ---------- Animasi pembuka (di atas semuanya) ----------
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(tween(0)),
            exit = fadeOut(tween(500, easing = FastOutSlowInEasing)) +
                    slideOutVertically(tween(650, easing = FastOutSlowInEasing)) { -it / 3 }
        ) {
            SplashOverlay()
        }
    }
}