package com.jazzant.expensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptModelDao {
    @Insert
    suspend fun insert(receiptModel: ReceiptModel)

    @Query("SELECT * FROM receipt_model_table")
    fun getAllReceiptModels(): Flow<List<ReceiptModel>>
}