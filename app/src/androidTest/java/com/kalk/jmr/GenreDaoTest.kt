package com.kalk.jmr
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.genre.GenreDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenreDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: GenreDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.genreDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun genre_is_added_to_database() {
        val grunge = Genre(1, "grunge")
        dao.addGenre(grunge)
        val queryResult = dao.byId(1)
        assertEquals(queryResult.id, grunge.id)
        assertEquals(queryResult.genre, grunge.genre)
    }


    @Test
    fun cannot_insert_genre_with_same_id() {
        val grunge = Genre(1, "grunge")
        val rock = Genre(1, "rock")
        dao.addGenre(grunge)
        dao.addGenre(rock)
        val queryResult = dao.byId(1)
        assertEquals(queryResult.id, grunge.id)
        assertEquals(queryResult.genre, grunge.genre)
        assertNotEquals(queryResult.genre, rock.genre)
    }


    @Test
    fun cannot_insert_genre_with_same_type_of_genre() {
        val grunge = Genre(1, "grunge")
        val grunge2 = Genre(2, "grunge")
        dao.addGenre(grunge)
        dao.addGenre(grunge2)
        val queryResult = dao.byId(1)
        assertEquals(queryResult.id, grunge.id)
        assertEquals(queryResult.genre, grunge.genre)
        val queryResult2 = dao.byId(2)
        assertEquals(queryResult2, null)

    }
}