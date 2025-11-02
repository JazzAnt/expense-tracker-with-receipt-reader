package com.jazzant.expensetracker

import androidx.compose.runtime.snapshots.SnapshotStateList

class ExpenseRepository (private val expenseDao: ExpenseDao){
    val expenses: SnapshotStateList<Expense> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense){
        expenseDao.insert(expense)
    }

    suspend fun delete(expense: Expense){
        expenseDao.delete(expense)
    }

    suspend fun update(expense: Expense){
        expenseDao.update(expense)
    }

    suspend fun deleteAll(){
        expenseDao.deleteAllExpenses()
    }
}