package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //  Done: Add testing implementation to the RemindersDao.kt
    private lateinit var database: RemindersDatabase
    private lateinit var  reminderList:List<ReminderDTO>
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initializeDatabase() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        reminderList= listOf(
            ReminderDTO("Island1","this island1 is so near to my house","long:29.014,lat:30.5",29.014,30.55),
            ReminderDTO("Island2","this island2 is so near to my house","long:29.014,lat:30.5",29.014,30.55),
            ReminderDTO("Island3","this island3 is so near to my house","long:29.014,lat:30.5",29.014,30.55)
        )
    }

    @After
    fun closeDataBase() = database.close()

    @Test
    fun insertLocationReminder()= runBlockingTest {
        //Given : save Reminder Item
        val reminder = ReminderDTO("Island","this island is so near to my house","long:29.014,lat:30.5",29.014,30.55)
        database.reminderDao().saveReminder(reminder)
        //When : get this reminder item by it's id from database
        val mockedReminder = database.reminderDao().getReminderById(reminder.id)
        //Then : Check if the mocked data get from db is equals to what i have inserted
        assertThat(mockedReminder as ReminderDTO, notNullValue())
        assertThat(mockedReminder.id, `is`(reminder.id))
        assertThat(mockedReminder.title, `is`(reminder.title))
        assertThat(mockedReminder.description, `is`(reminder.description))
        assertThat(mockedReminder.location, `is`(reminder.location))
        assertThat(mockedReminder.latitude, `is`(reminder.latitude))
        assertThat(mockedReminder.longitude, `is`(reminder.longitude))
    }


    @Test
    fun getAllReminders()= runBlockingTest{
        //Given : Save list of reminders
        reminderList.forEach{
            database.reminderDao().saveReminder(it)
        }
        //when : get all reminders from db
        val loaded = database.reminderDao().getReminders()
        //then check if size of each lists is equals
        assertThat(loaded.size, `is`(reminderList.size))
    }

    @Test
    fun DeleteAllReminders() = runBlockingTest {
        //Given : Save List of Reminders
        reminderList.forEach{
            database.reminderDao().saveReminder(it)
        }
        //When delete all reminders from db
        database.reminderDao().deleteAllReminders()
        val loaded = database.reminderDao().getReminders()
        //then check if reminders items is deleted
        assertThat(loaded.size, `is`(0))
    }
}