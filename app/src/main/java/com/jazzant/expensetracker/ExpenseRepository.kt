package com.jazzant.expensetracker

class ExpenseRepository (private val expenseDao: ExpenseDao){
    fun getAllExpenses() = expenseDao.getAllExpenses()

    fun getAllCategories() = expenseDao.getAllCategories()

    fun getSumOfExpenses() = expenseDao.getSumOfExpenses()
    suspend fun insert(expense: Expense){
        expenseDao.insert(expense)
    }

    suspend fun delete(expense: Expense){
        expenseDao.delete(expense)
    }

    suspend fun update(expense: Expense){
        expenseDao.update(expense)
    }
}