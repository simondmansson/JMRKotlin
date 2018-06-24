package com.kalk.jmr

import com.kalk.jmr.ui.recommendations.Token
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilityTests {
    val threeHours = 10_800_000L
    val fortyFiveMinutes = 2_700_000L
    val tokenString = "abc"
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
}