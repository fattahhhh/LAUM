package id.ac.pnj.resortcampus

import android.app.Application
import id.ac.pnj.resortcampus.data.Repository
import id.ac.pnj.resortcampus.data.SessionStore
import id.ac.pnj.resortcampus.data.ai.WasteClassifier
import id.ac.pnj.resortcampus.data.local.AppDatabase

/** Dependency container sederhana (tanpa library DI). */
class AppContainer(app: Application) {
    private val database = AppDatabase.build(app)
    private val session = SessionStore(app)
    val repository = Repository(app, database, session, WasteClassifier())
}

class ReSortApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
