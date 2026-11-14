package id.ac.pnj.resortcampus.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.ac.pnj.resortcampus.ui.screens.BinsScreen
import id.ac.pnj.resortcampus.ui.screens.HomeScreen
import id.ac.pnj.resortcampus.ui.screens.LeaderboardScreen
import id.ac.pnj.resortcampus.ui.screens.OnboardingScreen
import id.ac.pnj.resortcampus.ui.screens.ProfileScreen
import id.ac.pnj.resortcampus.ui.screens.ResultScreen
import id.ac.pnj.resortcampus.ui.screens.RewardScreen
import id.ac.pnj.resortcampus.ui.screens.ScanMode
import id.ac.pnj.resortcampus.ui.screens.ScanScreen
import id.ac.pnj.resortcampus.ui.screens.SettingScreen
import id.ac.pnj.resortcampus.util.SettingsStore

object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val SETTINGS = "settings"
    const val HOME = "home"
    const val LEADERBOARD = "leaderboard"
    const val BINS = "bins"
    const val PROFILE = "profile"
    const val SCAN = "scan/{mode}"
    const val RESULT = "result"
    const val REWARD = "reward"

    fun scan(mode: ScanMode) = "scan/${mode.name}"
}

/** Navigasi antar tab utama tanpa menumpuk back stack. */
fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavHost(startLoggedIn: Boolean, activityOwner: ViewModelStoreOwner) {
    val nav = rememberNavController()
    // ScanFlowViewModel dibagikan antar layar Scan -> Hasil -> Tong -> Reward (scope Activity).
    val scanFlow = appViewModel(owner = activityOwner) { ScanFlowViewModel(it) }

    val goHomeClearingAuth: () -> Unit = remember(nav) {
        {
            SettingsStore.advanceFunFact()
            nav.navigate(Routes.HOME) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = nav,
        startDestination = if (startLoggedIn) Routes.HOME else Routes.ONBOARDING,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        // Layar awal: sekarang memakai OnboardingScreen (AuthScreens.kt), bukan WelcomeScreen lama.
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onLogin = { nav.navigate(Routes.LOGIN) },
                onRegister = { nav.navigate(Routes.REGISTER) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.LOGIN) {
            FirebaseLoginRoute(
                onClose = {
                    if (!nav.popBackStack()) {
                        nav.navigate(Routes.ONBOARDING) { launchSingleTop = true }
                    }
                },
                onRegisterClick = {
                    nav.navigate(Routes.REGISTER) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onSuccess = goHomeClearingAuth,
            )
        }
        composable(Routes.REGISTER) {
            FirebaseRegisterRoute(
                onClose = {
                    if (!nav.popBackStack()) {
                        nav.navigate(Routes.ONBOARDING) { launchSingleTop = true }
                    }
                },
                onSuccess = goHomeClearingAuth,
            )
        }
        composable(Routes.SETTINGS) {
            SettingScreen(onBack = { nav.popBackStack() })
        }

        composable(Routes.HOME) {
            HomeScreen(nav = nav, scanFlow = scanFlow)
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(nav = nav)
        }
        composable(Routes.BINS) {
            BinsScreen(nav = nav, scanFlow = scanFlow)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                nav = nav,
                onLoggedOut = {
                    nav.navigate(Routes.ONBOARDING) { popUpTo(nav.graph.id) { inclusive = true } }
                },
            )
        }

        composable(
            Routes.SCAN,
            arguments = listOf(navArgument("mode") { type = NavType.StringType; defaultValue = ScanMode.WASTE.name }),
        ) { entry ->
            val mode = runCatching { ScanMode.valueOf(entry.arguments?.getString("mode").orEmpty()) }
                .getOrDefault(ScanMode.WASTE)
            ScanScreen(
                initialMode = mode,
                scanFlow = scanFlow,
                onBack = { nav.popBackStack() },
                onWastePhoto = { uri ->
                    scanFlow.analyzeWaste(uri)
                    nav.navigate(Routes.RESULT)
                },
                onBinPhoto = { uri ->
                    scanFlow.verifyBin(uri)
                    nav.navigate(Routes.REWARD) { popUpTo(Routes.HOME) }
                },
            )
        }
        composable(Routes.RESULT) {
            ResultScreen(
                scanFlow = scanFlow,
                onRetake = {
                    scanFlow.resetWaste()
                    nav.popBackStack()
                },
                onGoToBin = {
                    nav.navigate(Routes.BINS) { popUpTo(Routes.HOME) }
                },
                onSkip = {
                    scanFlow.skip()
                    scanFlow.resetWaste()
                    nav.popBackStack(Routes.HOME, inclusive = false)
                },
            )
        }
        composable(Routes.REWARD) {
            RewardScreen(
                scanFlow = scanFlow,
                onDone = {
                    scanFlow.resetBin()
                    nav.popBackStack(Routes.HOME, inclusive = false)
                },
                onScanAgain = {
                    scanFlow.resetBin()
                    nav.navigate(Routes.scan(ScanMode.WASTE)) { popUpTo(Routes.HOME) }
                },
                onRetryBin = {
                    scanFlow.resetBin()
                    nav.navigate(Routes.scan(ScanMode.BIN)) { popUpTo(Routes.HOME) }
                },
            )
        }
    }
}