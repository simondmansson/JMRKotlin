package com.kalk.jmr

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.genre.GenreRepository
import com.kalk.jmr.ui.genres.GenresViewModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class GenreViewModelTest {

    val repo = mock<GenreRepository>()
    lateinit var liveData:MutableLiveData<List<Genre>>
    private lateinit var genreViewModel: GenresViewModel
    val genreList = listOf(Genre(1, "rock"), Genre(2, "pop"))

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() //we need this for testing live data

    @Before
    fun setup() {
        liveData = MutableLiveData()
        liveData.value = genreList
       repo.stub {
           on { getGenres() } doReturn liveData
       }
    }

    @Test
    fun genres_are_present_after_init() {
        genreViewModel = GenresViewModel(repo)
        val observer = mock<Observer<List<Genre>>>()
        genreViewModel.genres.observeForever(observer)
        Assert.assertEquals(2, genreViewModel.genres.value?.size)
    }

    @Test
    fun genreText_change_when_new_genre_is_chosen() {
        genreViewModel = GenresViewModel(repo)
        val observer = mock<Observer<List<Genre>>>()
        val textObserver = mock<Observer<String>>()
        genreViewModel.genres.observeForever(observer) //have to observe this as well for genreText change to trigger?
        genreViewModel.genreText.observeForever(textObserver)
        genreViewModel.chosenGenre.value = 1
        Assert.assertEquals("Pop", genreViewModel.genreText.value) //Big P since we capitalize
        genreViewModel.chosenGenre.value = 0
        Assert.assertEquals("Rock", genreViewModel.genreText.value) //Big P since we capitalize
    }
}