package com.kalk.jmr.db.playlist

import com.kalk.jmr.db.track.Track

/**
 * A previously created playlist. That may be started again
 */
data class HistoryPlaylist(val id: String, val title: String, val uri: List<Track>)
