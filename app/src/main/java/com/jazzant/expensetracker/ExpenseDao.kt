package com.jazzant.expensetracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Query("SELECT * FROM expense_table ORDER BY date ASC")
    fun getAllExpenses(): LiveData<MutableList<Expense>>

    @Query("DELETE FROM expense_table")
    suspend fun deleteAllExpenses()
}