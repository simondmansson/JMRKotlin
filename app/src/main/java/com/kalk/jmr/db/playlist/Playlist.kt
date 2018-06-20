package com.kalk.jmr.db.playlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.db.userActivity.UserActivity

@Entity(tableName = "playlists",
        indices = [
        Index(value = ["id"], unique = true)
        ])
data class Playlist(
        @PrimaryKey
        val id: Int,
        val title: String,
        @ForeignKey(entity = UserLocation::class, parentColumns = ["id"], childColumns = ["location"])
        val location:Int,
        @ForeignKey(entity = UserActivity::class, parentColumns = ["id"], childColumns = ["activity"])
        val activity:Int,
        @ForeignKey(entity = Genre::class, parentColumns = ["id"], childColumns = ["genre"])
        val genre:Int,
        val time:Int
        )