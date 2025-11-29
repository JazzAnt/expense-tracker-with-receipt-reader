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
    @Query("SELECT * FROM expense_table WHERE category=:category  ORDER BY date DESC")
    fun getAllExpenses(category: String): Flow<List<Expense>>
    @Query("SELECT * FROM expense_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<Expense>>
    @Query("SELECT * FROM expense_table WHERE category=:category AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getAllExpenses(category: String, startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expense_table WHERE LOWER(name) LIKE LOWER(:name)")
    fun searchExpensesByName(name: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expense_table WHERE LOWER(name) LIKE LOWER(:name)")
    fun sumOfExpensesByName(name: String): Flow<Float>

    @Query("SELECT DISTINCT category FROM expense_table ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT SUM(amount) FROM expense_table")
    fun getSumOfExpenses(): Flow<Float>
    @Query("SELECT SUM(amount) FROM expense_table WHERE category=:category")
    fun getSumOfExpenses(category: String): Flow<Float>
    @Query("SELECT SUM(amount) FROM expense_table WHERE date BETWEEN :startDate AND :endDate")
    fun getSumOfExpenses(startDate: Long, endDate: Long): Flow<Float>
    @Query("SELECT SUM(amount) FROM expense_table WHERE category=:category AND date BETWEEN :startDate AND :endDate")
    fun getSumOfExpenses(category: String, startDate: Long, endDate: Long): Flow<Float>

}