package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.*
import com.udacity.project4.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    Done: Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var reminderList: List<ReminderDTO>
    private val newReminder =
        ReminderDTO(
            "Island4",
            "this island1 is so near to my house",
            "long:29.014,lat:30.5",
            29.014,
            30.55
        )


    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun Initialize() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

        reminderList = listOf(
            ReminderDTO(
                "Island1",
                "this island1 is so near to my house",
                "long:29.014,lat:30.5",
                29.014,
                30.55,
                "1"
            ),
            ReminderDTO(
                "Island2",
                "this island2 is so near to my house",
                "long:29.014,lat:30.5",
                29.014,
                30.55,
                "2"
            ),
            ReminderDTO(
                "Island3",
                "this island3 is so near to my house",
                "long:29.014,lat:30.5",
                29.014,
                30.55,
                "3"
            )
        )
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun getReminders_returnError() = mainCoroutineRule.runBlockingTest {
        //No Given because we need to return null
        //When
        val loadedReminderList = remindersLocalRepository.getReminders()
        //Then check if it is successfully
        assertThat(loadedReminderList, not(Success(ArrayList(reminderList))))
    }

    @Test
    fun getReminders_returnSuccessfully() = mainCoroutineRule.runBlockingTest {
        // Given : save reminders list
        reminderList.forEach {
            remindersLocalRepository.saveReminder(it)
        }
        //When
        val loadedSuccessfullyReminderList = remindersLocalRepository.getReminders()
        //Then check if it is successfully
        assertThat(loadedSuccessfullyReminderList, `is`(Success(ArrayList(reminderList))))
    }


    @Test
    fun getReminderById_ReturnSuccessfully() =mainCoroutineRule.runBlockingTest{
        // Given : reminder
        reminderList.forEach {
            remindersLocalRepository.saveReminder(it)
        }
        //When : search result by id
        val searchResult = remindersLocalRepository.getReminder("3") as Success
        //Then: check if this result is not successfully found
        assertThat(searchResult.data, notNullValue())
    }

    @Test
    fun getReminderById_ReturnError() =mainCoroutineRule.runBlockingTest{
        // Given : reminder
        reminderList.forEach {
            remindersLocalRepository.saveReminder(it)
        }
        //When : search result by id
        val searchResult = remindersLocalRepository.getReminder("4")
        //Then: check if this result is not successfully found
        assertThat(searchResult, `is`(Error("Reminder not found!")))
    }


    @Test
    fun deleteAllReminders_ReturnEmpty() =mainCoroutineRule.runBlockingTest{
        // Given : reminder
        reminderList.forEach {
            remindersLocalRepository.saveReminder(it)
        }
        //When : search result by id
         remindersLocalRepository.deleteAllReminders()
        val reminderResponse = remindersLocalRepository.getReminders()
        //Then: check if this result is not successfully found
        assertThat(reminderResponse, not(Success(ArrayList(reminderList))))
    }
}