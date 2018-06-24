package com.kalk.jmr

import android.arch.lifecycle.Observer
import android.support.test.runner.AndroidJUnit4
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.genre.GenreDao
import com.kalk.jmr.ui.genres.GenresViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class GenreViewModelTest {

    private val genreDaoMock = mock(GenreDao::class.java)
    @Mock lateinit var observer: Observer<List<Genre>>
    private lateinit var genreViewModel: GenresViewModel
    val genreList = listOf(Genre(1, "rock"), Genre(2, "pop"))

    @Before
    fun setup() {

    }

    @Test
    fun genres_are_observable_after_init() {
        /*
            `when`(genreDaoMock.getAllGenres()).thenReturn(genreList)
            genreViewModel = GenresViewModel(GenreRepository.getInstance(genreDaoMock))
            //genreViewModel.genres.observeForever(observer)
            val genres = genreViewModel.genres.value
            assertNotEquals(genres, null)
        */
        }
}