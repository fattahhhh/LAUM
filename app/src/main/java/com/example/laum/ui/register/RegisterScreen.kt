package com.example.laum.ui.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laum.ui.login.LoginColors
import com.example.laum.ui.login.components.LoginCardTop
import com.example.laum.ui.login.components.LoginDropdownField
import com.example.laum.ui.login.components.LoginTextField

private val campusOptions = listOf(
    "Politeknik Negeri Jakarta",
    "Universitas Indonesia",
    "Universitas Gunadarma",
    "Lainnya"
)

private val majorOptions = listOf(
    "Teknik Informatika dan Komputer",
    "Teknik Sipil",
    "Teknik Mesin",
    "Teknik Elektro",
    "Akuntansi",
    "Administrasi Niaga"
)

@Composable
private fun FieldLabel(text: String, topPadding: Int = 16) {
    Text(
        text = text,
        color = LoginColors.GreenPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = topPadding.dp, bottom = 8.dp)
    )
}

@Composable
fun RegisterScreen(
    onClose: () -> Unit = {},
    onRegister: (String, String, String, String, String, (String?) -> Unit) -> Unit = { _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var campus by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var campusError by remember { mutableStateOf<String?>(null) }
    var majorError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun submit() {
        usernameError = if (username.trim().isEmpty()) "Username wajib diisi" else null
        passwordError = if (password.length < 8) "Minimal 8 karakter" else null
        confirmError = if (confirm != password) "Password tidak sama" else null
        campusError = if (campus.isEmpty()) "Pilih kampus" else null
        majorError = if (major.isEmpty()) "Pilih jurusan" else null
        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Email tidak valid" else null

        val valid = listOf(usernameError, passwordError, confirmError, campusError, majorError, emailError)
            .all { it == null }
        if (valid) {
            loading = true
            onRegister(username.trim(), password, campus, major, email.trim()) { error ->
                loading = false
                if (error != null) Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginColors.BgDark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "ReSort Campus",
            color = LoginColors.GreenSoft,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 56.dp)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            LoginCardTop(onClose = onClose)

            Text(
                text = "Register",
                color = LoginColors.GreenPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Mulai kontribusi hijaumu dari sini.",
                color = LoginColors.TextSub,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            FieldLabel("Buat username", 24)
            LoginTextField(
                value = username,
                onValueChange = { username = it; usernameError = null },
                placeholder = "Username Anda",
                error = usernameError
            )

            FieldLabel("Buat Password")
            LoginTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                placeholder = "Minimal 8 karakter",
                error = passwordError,
                isPassword = true
            )

            FieldLabel("Konfirmasi Password anda")
            LoginTextField(
                value = confirm,
                onValueChange = { confirm = it; confirmError = null },
                placeholder = "Ulangi password",
                error = confirmError,
                isPassword = true
            )

            FieldLabel("Nama Kampus")
            LoginDropdownField(
                value = campus,
                onValueChange = { campus = it; campusError = null },
                placeholder = "Pilih kampus",
                options = campusOptions,
                error = campusError
            )

            FieldLabel("Jurusan")
            LoginDropdownField(
                value = major,
                onValueChange = { major = it; majorError = null },
                placeholder = "Pilih jurusan",
                options = majorOptions,
                error = majorError
            )

            FieldLabel("Masukan email aktif Anda")
            LoginTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                placeholder = "nama@email.com",
                error = emailError,
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { submit() },
                enabled = !loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginColors.GreenPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (loading) "Memproses..." else "Buat Akun",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Dengan mendaftar, Anda menyetujui ketentuan layanan.",
                color = LoginColors.TextSub,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Bantuan?",
                color = LoginColors.GreenPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )
        }
    }
}