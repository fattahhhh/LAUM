package id.ac.pnj.resortcampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MajorEntity::class,
        UserEntity::class,
        ScanSessionEntity::class,
        ContributionEntity::class,
        BinLocationEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun majorDao(): MajorDao
    abstract fun userDao(): UserDao
    abstract fun scanSessionDao(): ScanSessionDao
    abstract fun contributionDao(): ContributionDao
    abstract fun binDao(): BinDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "resort_campus.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
