package com.kalk.jmr

import com.kalk.jmr.db.location.Coordinates
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.ui.recommendations.Token
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

class UtilityTests {
    val threeHours = 10_800_000L
    val fortyFiveMinutes = 2_700_000L
    val fifteenMinutes = 900_000L
    val tokenString = "abc"
    val dummyLoc = UserLocation("UUID", coordinates = Coordinates(0.0,0.0))

    @Test
    fun should_Request_New_Token_when_string_is_empty() {
        assertTrue(shouldRequestNewToken(Token("", 0), threeHours))
    }

    @Test
    fun should_Request_New_Token_when_time_passed_is_greater_than_55min() {
        assertTrue(shouldRequestNewToken(Token(tokenString, 3_360_000L), threeHours))
    }

    @Test
    fun should_not_Request_New_Token_when_time_passed_is_less_than_55min() {
        assertFalse(shouldRequestNewToken(Token(tokenString, 3_240_000L), fortyFiveMinutes))
    }

    @Test
    fun should_request_new_location_when_time_passed_is_less_than_fifteenMinutes() {
        shouldRequestNewLocation(dummyLoc, 800_000L, fifteenMinutes)
    }

    @Test
    fun should_request_new_location_when_time_passed_is_greater_than_fifteenMinutes() {
        shouldRequestNewLocation(dummyLoc, 1000_000L, fifteenMinutes)
    }

    @Test
    @Ignore
    fun should_request_new_location_when_long_distance_is_greater_than_() {

    }

    @Test
    @Ignore
    fun should_request_new_location_when_lat_distance_is_greater_than_() {

    }

    @Test
    fun getfromservice() {
        /*
        val token = "BQB9g8fTUXea8olwcH0-UuO8QZuavmCa71EKVGZ3dXIq-zk2Q4YtmJAsWMGk22Af6_XKp9adAGXC1iZgAR5AXAFR-Pp2ne47MA75bJ7n_jzLdE3DC4XUdTCCJs1rRvzNnkQKxCiSTEn3NcPVinh8WB2nm-54mFch55YV01k-"
        val service = buildJMRWebService()
        val message = GenreMessage(token, "rock")
        println(message)
        val rec = service.postRecommendationGenre(message).execute()
        println(rec.body())
        Assert.assertEquals(rec.isSuccessful, true)
        */
    }
}