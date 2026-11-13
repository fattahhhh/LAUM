package id.ac.pnj.resortcampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ac.pnj.resortcampus.ui.AppNavHost
import id.ac.pnj.resortcampus.ui.StartViewModel
import id.ac.pnj.resortcampus.ui.theme.ReSortTheme

class MainActivity : ComponentActivity() {

    private val startViewModel: StartViewModel by viewModels {
        viewModelFactory {
            initializer { StartViewModel((application as ReSortApp).container.repository) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Splash tetap tampil sampai seed database & cek login selesai.
        splash.setKeepOnScreenCondition { startViewModel.loggedIn.value == null }
        enableEdgeToEdge()

        setContent {
            ReSortTheme {
                val loggedIn by startViewModel.loggedIn.collectAsStateWithLifecycle()
                loggedIn?.let { AppNavHost(startLoggedIn = it, activityOwner = this@MainActivity) }
            }
        }
    }
}
