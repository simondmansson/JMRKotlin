package com.kalk.jmr

import com.kalk.jmr.webService.GenreMessage
import com.kalk.jmr.webService.HistoryMessage
import com.kalk.jmr.webService.JMRWebService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test for checking conversion of response body to correct pojo
 */
class WebServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var jmrWebService: JMRWebService

    val songs = """[{ "uri": "spotify:track:2RZWdE8kYPlCAcRUYDeuLC", "name": "Red Flag - Billy Talent" }, { "uri": "spotify:track:7kzKAuUzOITUauHAhoMoxA", "name": "Last Nite - The Strokes" }, { "uri": "spotify:track:7EbpUdQ4nQ5VnnFlZjvqYl", "name": "New Divide - Linkin Park" }, { "uri": "spotify:track:1fQaoh3imrMunWVZh5kf90", "name": "Shepherd of Fire - Avenged Sevenfold" }, { "uri": "spotify:track:5xS9hkTGfxqXyxX6wWWTt4", "name": "Mary Jane's Last Dance - Tom Petty and the Heartbreakers" }, { "uri": "spotify:track:7gSQv1OHpkIoAdUiRLdmI6", "name": "I Won't Back Down - Tom Petty" }, { "uri": "spotify:track:4oDZ5L8izBals6jKBJDBcX", "name": "Your Love - The Outfield" }, { "uri": "spotify:track:2b9lp5A6CqSzwOrBfAFhof", "name": "Crazy Train - Remastered - Ozzy Osbourne" }, { "uri": "spotify:track:01a0J96fRD91VnjQQUCqMK", "name": "The Kids Aren't Alright - The Offspring" }, { "uri": "spotify:track:3k9i7UzeSUYWIfUZFeFDUd", "name": "Layla - 40th Anniversary Version / 2010 Remastered - Derek & The Dominos" }]"""


    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        val url = mockWebServer.url("")
        jmrWebService = buildJMRWebService(url.toString())
    }

    @After
    fun teardown() {
        mockWebServer.close()
    }

    @Test
    fun `retrieves a list of tracks when calling backends genre uri`() {
        mockWebServer.enqueue( MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(songs))

        val res = jmrWebService
                .postRecommendationGenre(GenreMessage("token", "rock"))
                .execute()

        assertTrue(res.body() != null)
        val tracks = res.body()!!
        assertEquals("spotify:track:2RZWdE8kYPlCAcRUYDeuLC", tracks[0].uri)
        assertEquals("Red Flag - Billy Talent", tracks[0].name)
    }

    @Test
    fun `retrieves a list of tracks when calling backends history uri`() {
        mockWebServer.enqueue( MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(songs))

        val res = jmrWebService.
                postRecommendationHistory(HistoryMessage("token", listOf("track1", "track2")))
                .execute()

        assertTrue(res.body() != null)
        val tracks = res.body()!!
        assertEquals("spotify:track:2RZWdE8kYPlCAcRUYDeuLC", tracks[0].uri)
        assertEquals("Red Flag - Billy Talent", tracks[0].name)
    }
}