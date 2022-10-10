package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //Done: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var dataSource:FakeDataSource
    private lateinit var remindersListViewModel:RemindersListViewModel
    private lateinit var reminderList:MutableList<ReminderDTO>

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutinesRule = MainCoroutineRule()

    @Before
    fun setUp(){
        reminderList= mutableListOf(
            ReminderDTO("Island1","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55),
            ReminderDTO("Island2","this island2 is so near to my house","long:29.014,lat:30.5",29.014,30.55),
            ReminderDTO("Island3","this island3 is so near to my house","long:29.014,lat:30.5",29.014,30.55))

        dataSource = FakeDataSource(reminderList)
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @Test
    fun loadReminders_ReturnReminders()=mainCoroutinesRule.runBlockingTest {
        // Given
        remindersListViewModel.loadReminders()
        //Then check if loaded list is actual my fake list
        MatcherAssert.assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, CoreMatchers.`is`(3))
        //Give: add new reminder
        val newReminder =  ReminderDTO("Island4","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55)
        dataSource.saveReminder(newReminder)
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, CoreMatchers.not(3))
    }
    @ExperimentalCoroutinesApi
    @Test
    fun invalidateAllData_ReturnError()= runBlockingTest{
        //Given No reminder with error and load Reminders
        dataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        //Then check if show no data is true  and
        MatcherAssert.assertThat(remindersListViewModel.showNoData.getOrAwaitValue(),CoreMatchers.`is`(true))
        MatcherAssert.assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(),CoreMatchers.`is`("Can not fount Reminders"))
     }


    @ExperimentalCoroutinesApi
    @Test
    fun loadingReminders_ReturnTrue()= runBlockingTest{
        mainCoroutinesRule.pauseDispatcher()
        //Then load Reminders
        remindersListViewModel.loadReminders()
        //Then Check if loading is equals to true
        MatcherAssert.assertThat(remindersListViewModel.showLoading.getOrAwaitValue(),CoreMatchers.`is`(true))
        //Then Resume Dispatcher
        mainCoroutinesRule.resumeDispatcher()
        //Loading will disappear
        MatcherAssert.assertThat(remindersListViewModel.showLoading.getOrAwaitValue(),CoreMatchers.`is`(false))
    }

    @After
    fun stopKoinExe(){
        stopKoin()
    }
}