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
        if (shouldReturnError){
            return Result.Error("Can not fount Reminders")
        }
        reminders?.let { return Result.Success(ArrayList(reminders)) }
        return Result.Error("Can not fount Reminders")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // Done("save the reminder")
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //Done("return the reminder with the id")
        reminders?.forEach {
            if (id == it.id) {
                return Result.Success(it)
            }
        }
        return Result.Error("There is no reminder id like that")
    }

    override suspend fun deleteAllReminders() {
        // Done("delete all the reminders")
        reminders?.clear()
    }
}