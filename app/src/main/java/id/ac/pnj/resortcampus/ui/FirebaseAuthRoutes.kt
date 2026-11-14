package id.ac.pnj.resortcampus.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.laum.data.AuthRepository
import id.ac.pnj.resortcampus.ReSortApp
import id.ac.pnj.resortcampus.ui.screens.LoginScreen
import id.ac.pnj.resortcampus.ui.screens.RegisterScreen
import kotlinx.coroutines.launch

@Composable
fun FirebaseLoginRoute(
    onClose: () -> Unit,
    onRegisterClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember(context) {
        (context.applicationContext as ReSortApp).container.repository
    }
    LoginScreen(
        onClose = onClose,
        onRegisterClick = onRegisterClick,
        onLogin = { username, password, done ->
            AuthRepository.login(username, password) { error ->
                if (error != null) {
                    done(error)
                } else {
                    scope.launch {
                        val synced = runCatching {
                            repo.syncFirebaseUser(fallbackName = username)
                        }.getOrDefault(false)
                        if (synced) {
                            done(null)
                            onSuccess()
                        } else {
                            done("Gagal menyinkronkan profil akun")
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun FirebaseRegisterRoute(
    onClose: () -> Unit,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember(context) {
        (context.applicationContext as ReSortApp).container.repository
    }
    RegisterScreen(
        onClose = onClose,
        onRegister = { username, password, campus, major, email, done ->
            AuthRepository.register(username, password, campus, major, email) { error ->
                if (error != null) {
                    done(error)
                } else {
                    scope.launch {
                        val synced = runCatching {
                            repo.syncFirebaseUser(
                                fallbackName = username,
                                fallbackEmail = email,
                                fallbackMajorName = major,
                            )
                        }.getOrDefault(false)
                        if (synced) {
                            done(null)
                            onSuccess()
                        } else {
                            done("Gagal menyinkronkan profil akun")
                        }
                    }
                }
            }
        },
    )
}