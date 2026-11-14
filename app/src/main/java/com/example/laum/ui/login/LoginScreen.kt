package com.example.laum.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.laum.ui.login.components.LoginCardTop
import com.example.laum.ui.login.components.LoginTextField

@Composable
fun LoginScreen(
    onClose: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLogin: (String, String, (String?) -> Unit) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginColors.BgDark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "ReSort Campus",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 56.dp)
        )
        Text(
            text = "Aksi kecil, dampak besar.",
            color = LoginColors.GreenSoft,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 24.dp, top = 4.dp)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 56.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            LoginCardTop(onClose = onClose)

            Text(
                text = "LOGIN",
                color = LoginColors.GreenPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Selamat datang kembali di ReSort Campus.",
                color = LoginColors.TextSub,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Masukan username",
                color = LoginColors.GreenPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            Spacer(Modifier.height(8.dp))
            LoginTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = null
                },
                placeholder = "Username Anda",
                error = usernameError
            )

            Text(
                text = "Masukan Password",
                color = LoginColors.GreenPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            LoginTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                placeholder = "Password Anda",
                error = passwordError,
                isPassword = true
            )

            Text(
                text = "Lupa Password?",
                color = LoginColors.GreenPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 12.dp)
                    .clickable {
                        Toast
                            .makeText(context, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .padding(4.dp)
            )

            Button(
                onClick = {
                    when {
                        username.trim().isEmpty() -> usernameError = "Username wajib diisi"
                        password.isEmpty() -> passwordError = "Password wajib diisi"
                        else -> {
                            loading = true
                            onLogin(username.trim(), password) { error ->
                                loading = false
                                if (error != null) passwordError = error
                            }
                        }
                    }
                },
                enabled = !loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginColors.GreenPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (loading) "Memproses..." else "Masuk",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Data aman. Kontribusi tetap bermakna.",
                color = LoginColors.TextSub,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Belum punya akun? Daftar",
                color = LoginColors.GreenPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
                    .clickable { onRegisterClick() }
                    .padding(4.dp)
            )
        }
    }
}