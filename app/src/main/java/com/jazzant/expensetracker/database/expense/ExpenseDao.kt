package com.jazzant.expensetracker.database.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Query("SELECT * FROM expense_table ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT DISTINCT category FROM expense_table ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT SUM(amount) FROM expense_table")
    fun getSumOfExpenses(): Flow<Float>
}