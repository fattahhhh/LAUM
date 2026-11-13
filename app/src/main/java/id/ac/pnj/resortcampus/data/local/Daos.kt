package id.ac.pnj.resortcampus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MajorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MajorEntity>)

    @Query("SELECT * FROM majors ORDER BY name")
    suspend fun getAll(): List<MajorEntity>

    @Query("SELECT * FROM majors WHERE id = :id")
    suspend fun getById(id: String): MajorEntity?

    @Query("SELECT COUNT(*) FROM majors")
    suspend fun count(): Int
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: String): Flow<UserEntity?>
}

@Dao
interface ScanSessionDao {
    @Insert
    suspend fun insert(session: ScanSessionEntity)

    @Query("SELECT * FROM scan_sessions WHERE id = :id")
    suspend fun getById(id: String): ScanSessionEntity?

    @Query("UPDATE scan_sessions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query(
        "UPDATE scan_sessions SET status = 'EXPIRED' " +
            "WHERE status = 'PENDING' AND expiresAt < :now"
    )
    suspend fun expireOld(now: Long)

    @Query(
        "UPDATE scan_sessions SET status = 'CANCELLED' " +
            "WHERE userId = :userId AND status = 'PENDING'"
    )
    suspend fun cancelPendingForUser(userId: String)

    @Query("SELECT COUNT(*) FROM scan_sessions WHERE userId = :userId AND createdAt >= :since")
    suspend fun countSince(userId: String, since: Long): Int

    @Query(
        "SELECT COUNT(*) FROM scan_sessions WHERE userId = :userId " +
            "AND imageHash = :hash AND status != 'CANCELLED'"
    )
    suspend fun countByHash(userId: String, hash: String): Int
}

@Dao
interface ContributionDao {
    @Insert
    suspend fun insert(item: ContributionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ContributionEntity>)

    @Query(
        """
        SELECT m.id AS majorId, m.name AS name, m.faculty AS faculty,
               COALESCE(SUM(CASE WHEN c.createdAt >= :since THEN c.points ELSE 0 END), 0) AS points
        FROM majors m
        LEFT JOIN contributions c ON c.majorId = m.id
        GROUP BY m.id
        ORDER BY points DESC, m.name ASC
        """
    )
    fun observeLeaderboard(since: Long): Flow<List<MajorScoreRow>>

    @Query(
        """
        SELECT COUNT(*) AS items, COALESCE(SUM(points), 0) AS points, COALESCE(SUM(co2eKg), 0) AS co2eKg
        FROM contributions WHERE userId = :userId
        """
    )
    fun observeUserStats(userId: String): Flow<UserStatsRow>

    @Query("SELECT * FROM contributions WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    fun observeHistory(userId: String, limit: Int = 30): Flow<List<ContributionEntity>>

    @Query("SELECT createdAt FROM contributions WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeContributionTimes(userId: String): Flow<List<Long>>
}

@Dao
interface BinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BinLocationEntity>)

    @Query("SELECT * FROM bin_locations ORDER BY name")
    fun observeAll(): Flow<List<BinLocationEntity>>
}
