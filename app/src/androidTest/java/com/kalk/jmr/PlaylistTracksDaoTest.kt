package com.kalk.jmr
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.playlistTracks.PlaylistTrack
import com.kalk.jmr.db.playlistTracks.PlaylistTracksDao
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlaylistTracksDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var pldao: PlaylistTracksDao
    private lateinit var tdao: TrackDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        pldao = db.playListTracksDao()
        tdao = db.trackDao()
    }

    @Test
    fun should_add_playlistTracks() {
        val track = Track (1, "asd", "333")
        tdao.addTrack(track)
        pldao.addPlaylistTrack(PlaylistTrack(1, 1))
        val pl = pldao.findTracksbyId(1)
        assertEquals(pl.size, 1)
        assertEquals(pl[0].id, track.id)
        assertEquals(pl[0].uri, track.uri)
        assertEquals(pl[0].title, track.title)
    }
}