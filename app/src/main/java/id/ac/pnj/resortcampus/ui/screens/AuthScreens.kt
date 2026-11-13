package id.ac.pnj.resortcampus.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.pnj.resortcampus.data.Seeder
import id.ac.pnj.resortcampus.ui.AuthViewModel
import id.ac.pnj.resortcampus.ui.appViewModel
import id.ac.pnj.resortcampus.ui.components.MessageBanner
import id.ac.pnj.resortcampus.ui.components.PrimaryButton
import id.ac.pnj.resortcampus.ui.components.SecondaryButton
import id.ac.pnj.resortcampus.ui.theme.Green500
import id.ac.pnj.resortcampus.ui.theme.Green900
import id.ac.pnj.resortcampus.ui.theme.Green950
import id.ac.pnj.resortcampus.ui.theme.GreenBorder
import id.ac.pnj.resortcampus.ui.theme.GreenSoft
import id.ac.pnj.resortcampus.ui.theme.GreenText
import id.ac.pnj.resortcampus.ui.theme.Line
import id.ac.pnj.resortcampus.ui.theme.Mint50
import id.ac.pnj.resortcampus.ui.theme.Muted

// ==========================================
// 1. SPLASH / ONBOARDING SCREEN (UI • Splash)
// ==========================================
@Composable
fun OnboardingScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Mint50)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header brand di kiri atas
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .fillMaxSize(0.36f)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, GreenBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.LocalDrink,
                    contentDescription = "Botol Daur Ulang",
                    tint = Green900,
                    modifier = Modifier.fillMaxSize(0.53f),
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "ReSort Campus",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Green900,
            )
        }

        Spacer(Modifier.weight(1f))

        // Kartu ilustrasi botol daur ulang melingkar sesuai Figma
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(GreenSoft)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            RecycleBottleIllustration(modifier = Modifier.size(160.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Headline teks sesuai Figma
        Text(
            text = "Pilah sampahmu,\nNaikkan peringkat\njurusanmu.",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
            ),
            color = Green900,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Foto sampahmu dengan AI, buang ke tong yang tepat di kampus PNJ, dan kumpulkan poin untuk jurusanmu.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))

        // Tombol Login & Register
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PrimaryButton(
                text = "Login",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            )
            SecondaryButton(
                text = "Register",
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            )
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
                contentDescription = "Botol Daur Ulang",
                tint = Green900,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

// ==========================================
// 2. LOGIN SCREEN MODAL (UI • Login)
// ==========================================

@Composable
private fun AuthBrandHeader() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            "ReSort Campus",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            "Politeknik Negeri Jakarta",
            style = MaterialTheme.typography.labelSmall,
            color = GreenBorder,
        )
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = Muted,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder != null) ({ Text(placeholder, color = Muted.copy(alpha = 0.6f)) }) else null,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Mint50,
                unfocusedContainerColor = Mint50,
                focusedBorderColor = Green500,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit, onSuccess: () -> Unit, onGoRegister: () -> Unit) {
    val vm = appViewModel { AuthViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.success) { if (state.success) onSuccess() }

    Column(
        Modifier
            .fillMaxSize()
            .background(Green950)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        AuthBrandHeader()

        Box(
            Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.padding(24.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Green900),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Eco, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }

                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Mint50),
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Green900, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "LOGIN",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                        ),
                        color = Green900,
                    )
                    Text(
                        text = "Masuk untuk mencatat sampah & poin jurusan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 18.dp),
                    )

                    state.error?.let {
                        MessageBanner(it, isError = true)
                        Spacer(Modifier.height(12.dp))
                    }

                    AuthField(
                        label = "Email atau username",
                        value = email,
                        onValueChange = { email = it; vm.clearError() },
                        placeholder = "nama@pnj.ac.id",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    )

                    Spacer(Modifier.height(12.dp))

                    AuthField(
                        label = "Kata sandi",
                        value = password,
                        onValueChange = { password = it; vm.clearError() },
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    "Tampilkan kata sandi",
                                    tint = Muted,
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    )

                    Spacer(Modifier.height(20.dp))

                    PrimaryButton(
                        text = "Masuk",
                        onClick = { vm.login(email, password) },
                        loading = state.loading,
                        enabled = email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                    )

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        onClick = {
                            email = Seeder.DEMO_EMAIL
                            password = Seeder.DEMO_PASSWORD
                            vm.login(Seeder.DEMO_EMAIL, Seeder.DEMO_PASSWORD)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = GreenSoft,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenBorder),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("Pakai Akun Demo", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GreenText)
                                Text("demo@pnj.ac.id", style = MaterialTheme.typography.labelSmall, color = Muted)
                            }
                            Text("Masuk Cepat →", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GreenText)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Belum punya akun? ", style = MaterialTheme.typography.bodySmall, color = Muted)
                        Text(
                            "Daftar",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Green900,
                            modifier = Modifier.clickable { onGoRegister() },
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. REGISTER SCREEN MODAL (UI • Register)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onSuccess: () -> Unit, onGoLogin: () -> Unit) {
    val vm = appViewModel { AuthViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var majorId by rememberSaveable { mutableStateOf<String?>(null) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showPass by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.majors) {
        if (majorId == null && state.majors.isNotEmpty()) majorId = state.majors.first().id
    }
    LaunchedEffect(state.success) { if (state.success) onSuccess() }

    val selectedMajor = state.majors.firstOrNull { it.id == majorId }

    Box(
        Modifier
            .fillMaxSize()
            .background(Green950)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp,
        ) {
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GreenSoft)
                            .border(1.dp, GreenBorder, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Eco, null, tint = Green500, modifier = Modifier.size(22.dp))
                    }
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Mint50),
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Green900, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Register",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Green900,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Buat akun mahasiswa untuk mendukung jurusanmu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 18.dp),
                )

                state.error?.let {
                    MessageBanner(it, isError = true)
                    Spacer(Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; vm.clearError() },
                    label = { Text("Nama lengkap") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Green500, unfocusedBorderColor = Line),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; vm.clearError() },
                    label = { Text("Email atau username") },
                    placeholder = { Text("nama@pnj.ac.id") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Green500, unfocusedBorderColor = Line),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(10.dp))

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedMajor?.name ?: "Pilih jurusan",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih jurusan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Green500, unfocusedBorderColor = Line),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        state.majors.forEach { m ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(m.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("Bidang ${m.faculty}", style = MaterialTheme.typography.bodySmall, color = Muted)
                                    }
                                },
                                onClick = {
                                    majorId = m.id
                                    expanded = false
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; vm.clearError() },
                    label = { Text("Kata sandi (min. 6 karakter)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Green500, unfocusedBorderColor = Line),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "Tampilkan")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(20.dp))

                PrimaryButton(
                    text = "Daftar Akun",
                    onClick = { vm.register(name, email, password, majorId) },
                    loading = state.loading,
                    enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6 && majorId != null,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                )

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sudah punya akun? ", style = MaterialTheme.typography.bodySmall, color = Muted)
                    Text(
                        "Masuk",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Green900,
                        modifier = Modifier.clickable { onGoLogin() },
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. SETTING SCREEN MODAL (UI • Setting)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(onBack: () -> Unit) {
    var selectedLanguage by rememberSaveable { mutableStateOf("Bahasa Indonesia") }
    var languageExpanded by rememberSaveable { mutableStateOf(false) }
    var notificationEnabled by rememberSaveable { mutableStateOf(true) }
    var darkModeEnabled by rememberSaveable { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp,
        ) {
            Column(Modifier.padding(24.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Setting",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Green900,
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Mint50),
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Green900, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Pilihan bahasa", style = MaterialTheme.typography.labelMedium, color = Muted)
                Spacer(Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Filled.Public, null, tint = Green500) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(languageExpanded) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Green500, unfocusedBorderColor = Line),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false },
                    ) {
                        listOf("Bahasa Indonesia", "English (EN)").forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    selectedLanguage = lang
                                    languageExpanded = false
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = Line)
                Spacer(Modifier.height(14.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(GreenSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Notifications, null, tint = GreenText, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Notifikasi", style = MaterialTheme.typography.titleSmall, color = Green900)
                            Text("Pengingat streak & update jurusan", style = MaterialTheme.typography.labelSmall, color = Muted)
                        }
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { notificationEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Green500),
                    )
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Line)
                Spacer(Modifier.height(14.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(GreenSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Palette, null, tint = GreenText, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Mode Gelap", style = MaterialTheme.typography.titleSmall, color = Green900)
                            Text("Tampilan gelap hemat baterai", style = MaterialTheme.typography.labelSmall, color = Muted)
                        }
                    }
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Green500),
                    )
                }

                Spacer(Modifier.height(24.dp))

                PrimaryButton(
                    text = "Simpan Pengaturan",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                )
            }
        }
    }
}
