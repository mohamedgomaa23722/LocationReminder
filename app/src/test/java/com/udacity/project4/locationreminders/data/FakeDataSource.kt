package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    //    Done: Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        // Done("Return the reminders")
        if (shouldReturnError) {
            return Result.Error("Can not fount Reminders")
        }
        return Result.Success(ArrayList(reminders))
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // Done("save the reminder")
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //Done("return the reminder with the id")
        if (shouldReturnError) {
            return Result.Error("There is no reminder id like that")
        }
        val result: ReminderDTO? = reminders?.firstOrNull{it.id == id}
        return if (result !=null)
            Result.Success(result)
        else
            Result.Error("Reminder not found!")
    }

    override suspend fun deleteAllReminders() {
        // Done("delete all the reminders")
        reminders?.clear()
    }
}