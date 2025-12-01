package com.jazzant.expensetracker.database.receiptmodel

import com.jazzant.expensetracker.database.expense.Expense

class ReceiptModelRepository(private val receiptModelDao: ReceiptModelDao) {
    fun getAllReceiptModels() = receiptModelDao.getAllReceiptModels()

    suspend fun insert(receiptModel: ReceiptModel){
        receiptModelDao.insert(receiptModel)
    }

    suspend fun delete(receiptModel: ReceiptModel){
        receiptModelDao.delete(receiptModel)
    }

    suspend fun update(receiptModel: ReceiptModel){
        receiptModelDao.update(receiptModel)
    }
}