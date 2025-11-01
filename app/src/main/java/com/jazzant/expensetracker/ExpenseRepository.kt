package com.jazzant.expensetracker

import androidx.lifecycle.LiveData

class ExpenseRepository (private val expenseDao: ExpenseDao){
    val expenses: LiveData<MutableList<Expense>> = expenseDao.getAllExpenses()

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