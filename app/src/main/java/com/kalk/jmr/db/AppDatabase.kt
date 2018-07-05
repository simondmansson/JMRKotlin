package com.kalk.jmr.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.genre.GenreDao
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.db.location.UserLocationDao
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.playlist.PlaylistDao
import com.kalk.jmr.db.playlistTracks.PlaylistTrack
import com.kalk.jmr.db.playlistTracks.PlaylistTracksDao
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
import com.kalk.jmr.db.userActivity.UserActivity
import com.kalk.jmr.db.userActivity.UserActivityDao

@Database(entities = [
    (Genre::class),
    (UserActivity::class),
    (UserLocation::class),
    (Track::class),
    (Playlist::class),
    (PlaylistTrack::class)],
        version = 1,
        exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun genreDao(): GenreDao
    abstract fun userActivityDao(): UserActivityDao
    abstract fun locationDao(): UserLocationDao
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playListTracksDao():PlaylistTracksDao

    companion object {
        @Volatile private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return sInstance ?: synchronized(this) {
                sInstance ?: buildDatabase(context).also { sInstance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context,
                    AppDatabase::class.java, "db")
                    .addCallback(object: RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val seed = OneTimeWorkRequestBuilder<DatabaseSeedWorker>().build()
                            WorkManager.getInstance().enqueue(seed)
                        }
                    })
                    .build()
        }
    }
}