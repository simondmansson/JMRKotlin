package com.kalk.jmr.db.playlistTracks

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.track.Track


@Entity(tableName = "playlistTracks", primaryKeys = ["playlist", "track"])
data class PlaylistTrack(
        @ForeignKey(entity = Playlist::class, parentColumns = ["id"], childColumns = ["playlist"])
        val playlist: String,
        @ForeignKey(entity = Track::class, parentColumns = ["uri"], childColumns = ["track"])
        val track: String
)