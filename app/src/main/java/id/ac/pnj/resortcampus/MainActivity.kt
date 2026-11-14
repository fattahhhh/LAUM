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
import id.ac.pnj.resortcampus.util.NotificationHelper
import id.ac.pnj.resortcampus.util.NotifType
import id.ac.pnj.resortcampus.util.ReminderScheduler
import id.ac.pnj.resortcampus.util.SettingsStore

class MainActivity : ComponentActivity() {

    private val startViewModel: StartViewModel by viewModels {
        viewModelFactory {
            initializer { StartViewModel((application as ReSortApp).container.repository) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        SettingsStore.init(this)
        if (SettingsStore.enabled(NotifType.REMINDER) && NotificationHelper.hasPermission(this)) {
            ReminderScheduler.schedule(this)
        }
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