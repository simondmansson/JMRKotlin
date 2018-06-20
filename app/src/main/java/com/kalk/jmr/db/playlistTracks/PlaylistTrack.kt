package com.kalk.jmr.db.playlistTracks

import androidx.room.Entity
import androidx.room.ForeignKey
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.track.Track

@Entity(tableName = "playlistTracks", primaryKeys = ["playlist", "track"])
data class PlaylistTrack(
        @ForeignKey(entity = Playlist::class, parentColumns = ["id"], childColumns = ["playlist"])
        val playlist: Int,
        @ForeignKey(entity = Track::class, parentColumns = ["id"], childColumns = ["track"])
        val track: Int
)