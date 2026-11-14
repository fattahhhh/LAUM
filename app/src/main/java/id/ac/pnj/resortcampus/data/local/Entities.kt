package id.ac.pnj.resortcampus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "majors")
data class MajorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val faculty: String,
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true), Index("majorId")],
    foreignKeys = [
        ForeignKey(
            entity = MajorEntity::class,
            parentColumns = ["id"],
            childColumns = ["majorId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
)
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val majorId: String?,
    val createdAt: Long,
)

object ScanStatus {
    const val PENDING = "PENDING"
    const val REWARDED = "REWARDED"
    const val EXPIRED = "EXPIRED"
    const val CANCELLED = "CANCELLED"
}

@Entity(
    tableName = "scan_sessions",
    indices = [Index("userId"), Index("imageHash")],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ScanSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val category: String,
    val itemName: String,
    val binType: String,
    val confidence: Double,
    val status: String,
    /** SHA-256 dari foto terkompresi, untuk mendeteksi upload ulang. Foto aslinya tidak disimpan. */
    val imageHash: String,
    val expiresAt: Long,
    val createdAt: Long,
)

@Entity(
    tableName = "contributions",
    indices = [Index("userId"), Index("majorId"), Index("createdAt")],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MajorEntity::class,
            parentColumns = ["id"],
            childColumns = ["majorId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ContributionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val majorId: String,
    val scanSessionId: String?,
    val itemName: String,
    val category: String,
    val points: Int,
    val co2eKg: Double,
    val createdAt: Long,
)

@Entity(tableName = "bin_locations")
data class BinLocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    /** Koordinat relatif pada peta kampus statis (0..1). */
    val x: Float,
    val y: Float,
    val areaLabel: String,
    /** anorganik | organik | kertas | b3 */
    val binType: String,
)

/** Hasil agregasi leaderboard per jurusan. */
data class MajorScoreRow(
    val majorId: String,
    val name: String,
    val faculty: String,
    val points: Int,
)

/** Hasil agregasi leaderboard per user beserta jurusannya. */
data class UserScoreRow(
    val userId: String,
    val userName: String,
    val majorId: String,
    val majorName: String,
    val faculty: String,
    val points: Int,
)

/** Ringkasan statistik user. */
data class UserStatsRow(
    val items: Int,
    val points: Int,
    val co2eKg: Double,
)
