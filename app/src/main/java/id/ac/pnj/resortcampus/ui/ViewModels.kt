package id.ac.pnj.resortcampus.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ac.pnj.resortcampus.ReSortApp
import id.ac.pnj.resortcampus.data.CurrentUser
import id.ac.pnj.resortcampus.data.LeaderboardEntry
import id.ac.pnj.resortcampus.data.Period
import id.ac.pnj.resortcampus.data.Repository
import id.ac.pnj.resortcampus.data.RewardInfo
import id.ac.pnj.resortcampus.data.ScanAnalysis
import id.ac.pnj.resortcampus.data.UserFacingException
import id.ac.pnj.resortcampus.data.local.BinLocationEntity
import id.ac.pnj.resortcampus.data.local.ContributionEntity
import id.ac.pnj.resortcampus.data.local.MajorEntity
import id.ac.pnj.resortcampus.data.local.UserStatsRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/** Membuat ViewModel dengan Repository dari [ReSortApp]. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(
    owner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current),
    crossinline create: (Repository) -> VM,
): VM {
    val repo = (LocalContext.current.applicationContext as ReSortApp).container.repository
    return viewModel(viewModelStoreOwner = owner, factory = viewModelFactory { initializer { create(repo) } })
}

private fun Throwable.friendly(fallback: String): String =
    if (this is UserFacingException) message ?: fallback else fallback

private val WHILE_SUBSCRIBED = SharingStarted.WhileSubscribed(5_000)

// ---------------- Start ----------------

class StartViewModel(private val repo: Repository) : ViewModel() {
    /** null = masih memuat; true/false = sudah login atau belum. */
    private val _loggedIn = MutableStateFlow<Boolean?>(null)
    val loggedIn: StateFlow<Boolean?> = _loggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repo.ensureSeeded() }
            runCatching { repo.expireOldSessions() }
            _loggedIn.value = runCatching { repo.isLoggedIn() }.getOrDefault(false)
        }
    }
}

// ---------------- Auth ----------------

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val majors: List<MajorEntity> = emptyList(),
    val success: Boolean = false,
)

class AuthViewModel(private val repo: Repository) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val majors = runCatching { repo.majors() }.getOrDefault(emptyList())
            _state.update { it.copy(majors = majors) }
        }
    }

    fun login(email: String, password: String) = submit { repo.login(email, password) }

    fun register(name: String, email: String, password: String, majorId: String?) =
        submit { repo.register(name, email, password, majorId) }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun submit(block: suspend () -> Result<Unit>) {
        if (_state.value.loading) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val r = block()
            _state.update {
                it.copy(
                    loading = false,
                    success = r.isSuccess,
                    error = r.exceptionOrNull()?.friendly("Terjadi kesalahan, coba lagi"),
                )
            }
        }
    }
}

// ---------------- Home ----------------

data class HomeUiState(
    val user: CurrentUser? = null,
    val myMajor: LeaderboardEntry? = null,
    val totalMajors: Int = 0,
    val stats: UserStatsRow = UserStatsRow(0, 0, 0.0),
    val streak: Int = 0,
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(repo: Repository) : ViewModel() {
    val state: StateFlow<HomeUiState> = repo.currentUser.flatMapLatest { cu ->
        if (cu == null) flowOf(HomeUiState())
        else combine(
            repo.leaderboard(Period.WEEK),
            repo.userStats(cu.user.id),
            repo.streak(cu.user.id),
        ) { board, stats, streak ->
            HomeUiState(
                user = cu,
                myMajor = board.firstOrNull { it.majorId == cu.user.majorId },
                totalMajors = board.size,
                stats = stats,
                streak = streak,
            )
        }
    }.stateIn(viewModelScope, WHILE_SUBSCRIBED, HomeUiState())
}

// ---------------- Leaderboard ----------------

data class LeaderboardUiState(
    val period: Period = Period.WEEK,
    val entries: List<LeaderboardEntry> = emptyList(),
    val myMajorId: String? = null,
    val loading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModel(repo: Repository) : ViewModel() {
    private val period = MutableStateFlow(Period.WEEK)

    val state: StateFlow<LeaderboardUiState> = combine(
        period,
        period.flatMapLatest { repo.leaderboard(it) },
        repo.currentUser,
    ) { p, entries, cu ->
        LeaderboardUiState(p, entries, cu?.user?.majorId, loading = false)
    }.stateIn(viewModelScope, WHILE_SUBSCRIBED, LeaderboardUiState())

    fun setPeriod(p: Period) {
        period.value = p
    }
}

// ---------------- Profil ----------------

data class ProfileUiState(
    val user: CurrentUser? = null,
    val stats: UserStatsRow = UserStatsRow(0, 0, 0.0),
    val history: List<ContributionEntity> = emptyList(),
    val streak: Int = 0,
    val aiConfigured: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(private val repo: Repository) : ViewModel() {
    val state: StateFlow<ProfileUiState> = repo.currentUser.flatMapLatest { cu ->
        if (cu == null) flowOf(ProfileUiState(aiConfigured = repo.isAiConfigured))
        else combine(
            repo.userStats(cu.user.id),
            repo.history(cu.user.id),
            repo.streak(cu.user.id),
        ) { stats, history, streak ->
            ProfileUiState(cu, stats, history, streak, repo.isAiConfigured)
        }
    }.stateIn(viewModelScope, WHILE_SUBSCRIBED, ProfileUiState())

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.logout()
            onDone()
        }
    }
}

// ---------------- Lokasi tong ----------------

class BinsViewModel(repo: Repository) : ViewModel() {
    val bins: StateFlow<List<BinLocationEntity>> =
        repo.bins().stateIn(viewModelScope, WHILE_SUBSCRIBED, emptyList())
}

// ---------------- Alur scan (dibagi antar layar, scope Activity) ----------------

sealed interface WasteState {
    data object Idle : WasteState
    data object Analyzing : WasteState
    data class Done(val analysis: ScanAnalysis) : WasteState
    data class Failed(val message: String) : WasteState
}

sealed interface BinState {
    data object Idle : BinState
    data object Verifying : BinState
    data class Rewarded(val reward: RewardInfo) : BinState
    data class Failed(val message: String) : BinState
}

/** Sesi yang menunggu verifikasi tong. */
data class PendingScan(val sessionId: String, val itemName: String, val binType: String, val expiresAt: Long)

class ScanFlowViewModel(private val repo: Repository) : ViewModel() {
    private val _waste = MutableStateFlow<WasteState>(WasteState.Idle)
    val waste: StateFlow<WasteState> = _waste.asStateFlow()

    private val _bin = MutableStateFlow<BinState>(BinState.Idle)
    val bin: StateFlow<BinState> = _bin.asStateFlow()

    private val _pending = MutableStateFlow<PendingScan?>(null)
    val pending: StateFlow<PendingScan?> = _pending.asStateFlow()

    private var lastWastePhoto: Uri? = null
    private var lastBinPhoto: Uri? = null

    fun analyzeWaste(photo: Uri) {
        lastWastePhoto?.let { if (it != photo) deleteTempPhoto(it) }
        lastWastePhoto = photo
        _waste.value = WasteState.Analyzing
        viewModelScope.launch {
            val r = repo.analyzeWaste(photo)
            r.onSuccess { a ->
                _waste.value = WasteState.Done(a)
                _pending.value = a.scanSessionId?.let {
                    PendingScan(it, a.result.itemName, a.result.binType, System.currentTimeMillis() + 15 * 60_000L)
                }
            }.onFailure { e ->
                _waste.value = WasteState.Failed(e.friendly("Gagal menganalisis foto. Coba lagi."))
            }
        }
    }

    fun verifyBin(photo: Uri) {
        val p = _pending.value
        if (p == null) {
            _bin.value = BinState.Failed("Belum ada sampah yang dipindai. Scan sampahnya dulu.")
            deleteTempPhoto(photo)
            return
        }
        lastBinPhoto?.let { if (it != photo) deleteTempPhoto(it) }
        lastBinPhoto = photo
        _bin.value = BinState.Verifying
        viewModelScope.launch {
            val r = repo.verifyBinAndReward(p.sessionId, photo)
            r.onSuccess {
                _pending.value = null
                _bin.value = BinState.Rewarded(it)
            }.onFailure { e ->
                if (e.message?.contains("kedaluwarsa") == true || e.message?.contains("sudah dipakai") == true) {
                    _pending.value = null
                }
                _bin.value = BinState.Failed(e.friendly("Gagal memverifikasi tong. Coba lagi."))
            }
        }
    }

    /** "Saya hanya memeriksa saja" -> batalkan sesi, tanpa poin. */
    fun skip() {
        val p = _pending.value ?: return
        _pending.value = null
        viewModelScope.launch { repo.cancelSession(p.sessionId) }
    }

    fun resetWaste() {
        lastWastePhoto?.let { deleteTempPhoto(it) }
        lastWastePhoto = null
        _waste.value = WasteState.Idle
    }

    fun resetBin() {
        lastBinPhoto?.let { deleteTempPhoto(it) }
        lastBinPhoto = null
        _bin.value = BinState.Idle
    }

    /** Foto hanya dipakai sementara dan tidak disimpan permanen (Green IT & privasi). */
    private fun deleteTempPhoto(uri: Uri) {
        if (uri.scheme == "file") uri.path?.let { runCatching { File(it).delete() } }
    }
}
