package com.jazzant.expensetracker.database.receiptmodel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jazzant.expensetracker.database.expense.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptModelDao {
    @Insert
    suspend fun insert(receiptModel: ReceiptModel)

    @Query("SELECT * FROM receipt_model_table")
    fun getAllReceiptModels(): Flow<List<ReceiptModel>>

    @Delete
    suspend fun delete(receiptModel: ReceiptModel)

    @Update
    suspend fun update(receiptModel: ReceiptModel)
}