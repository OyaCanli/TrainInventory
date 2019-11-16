package com.canli.oya.traininventoryroom.ui.trains

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrainViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var trainViewModel: TrainViewModel

    @Before
    fun setupViewModel() {
        trainViewModel = TrainViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun atLaunch_defaultUIStateIsLoading() {
        val value = trainViewModel.trainListUiState.showLoading
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
    }

}