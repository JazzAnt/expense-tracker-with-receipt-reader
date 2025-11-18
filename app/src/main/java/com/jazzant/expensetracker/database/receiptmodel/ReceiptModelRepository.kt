package com.jazzant.expensetracker.database.receiptmodel

class ReceiptModelRepository(private val receiptModelDao: ReceiptModelDao) {
    fun getAllReceiptModels() = receiptModelDao.getAllReceiptModels()

    suspend fun insert(receiptModel: ReceiptModel){
        receiptModelDao.insert(receiptModel)
    }
}