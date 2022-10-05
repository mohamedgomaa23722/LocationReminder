package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    //Done: provide testing to the SaveReminderView and its live data objects

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private val reminderOne =  ReminderDataItem("Island1","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55)
    private val reminderTwo =  ReminderDataItem("Island2","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55)
    private val reminderThree =  ReminderDataItem("Island3","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55)
    private val reminderWithOutTitle =  ReminderDataItem("","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55)
    private val reminderWithOutLocation =  ReminderDataItem("Island5","this island1 is so near to my house","",0.0,0.0)
    private val reminderWithOutLocationAndTitle =  ReminderDataItem("","","",0.0,0.0)

    private val app:Application = ApplicationProvider.getApplicationContext()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutinesRule = MainCoroutineRule()

    @Before
    fun Setup(){
        dataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(app, dataSource)
    }

    @After
    fun closeKoin(){
        stopKoin()
    }

    @Test
    fun saveReminder() = mainCoroutinesRule.runBlockingTest {
        //Given : Reminder object
        mainCoroutinesRule.pauseDispatcher()
        //Save Reminder
        saveReminderViewModel.saveReminder(reminderThree)
        //Check the loading livedata
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),CoreMatchers.`is`(true))
        //Resume
        mainCoroutinesRule.resumeDispatcher()
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),CoreMatchers.`is`(false))
        MatcherAssert.assertThat(saveReminderViewModel.showToast.getOrAwaitValue(),CoreMatchers.`is`(app.getString(R.string.reminder_saved)))
        MatcherAssert.assertThat(saveReminderViewModel.navigationCommand.getOrAwaitValue(),CoreMatchers.`is`(NavigationCommand.Back))
    }

    @Test
    fun saveAndInvalidate_returnValid()=mainCoroutinesRule.runBlockingTest {
        //Given : correct reminder objects
        val result = saveReminderViewModel.validateEnteredData(reminderOne)
        //then check the data
        MatcherAssert.assertThat(result,CoreMatchers.`is`(true))
    }


    @Test
    fun saveAndInvalidate_EmptyTitle_returnInValid()=mainCoroutinesRule.runBlockingTest {
        //Given : incorrect reminder objects with out title
        saveReminderViewModel.validateAndSaveReminder(reminderWithOutTitle)
        //then check the data
        MatcherAssert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),CoreMatchers.`is`(R.string.err_enter_title))
    }

    @Test
    fun saveAndInvalidate_EmptyLocation_returnInValid()=mainCoroutinesRule.runBlockingTest {
        //Given : incorrect reminder objects with out location
        saveReminderViewModel.validateAndSaveReminder(reminderWithOutLocation)
        //then check the data
        MatcherAssert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),CoreMatchers.`is`(R.string.err_select_location))
    }

    @Test
    fun saveAndInvalidate_EmptyLocationAndTitle_returnInValid()=mainCoroutinesRule.runBlockingTest {
        //Given : incorrect reminder objects with out title
        saveReminderViewModel.validateAndSaveReminder(reminderWithOutLocationAndTitle)
        //then check the data
        MatcherAssert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),CoreMatchers.`is`(R.string.err_enter_title))
    }
}