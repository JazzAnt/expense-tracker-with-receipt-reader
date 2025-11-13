package com.jazzant.expensetracker

class ReceiptModelRepository(private val receiptModelDao: ReceiptModelDao) {
    fun getAllReceiptModels() = receiptModelDao.getAllReceiptModels()

    suspend fun insert(receiptModel: ReceiptModel){
        receiptModelDao.insert(receiptModel)
    }
}