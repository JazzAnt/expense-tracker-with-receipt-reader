package com.jazzant.expensetracker.database.expense

class ExpenseRepository (private val expenseDao: ExpenseDao){
    fun getAllExpenses() = expenseDao.getAllExpenses()
    fun getAllExpenses(category: String) = expenseDao.getAllExpenses(category = category)
    fun getAllExpenses(dateRange: Pair<Long, Long>) = expenseDao.getAllExpenses(startDate = dateRange.first, endDate = dateRange.second)
    fun getAllExpenses(category: String, dateRange: Pair<Long, Long>) = expenseDao.getAllExpenses(category = category, startDate = dateRange.first, endDate = dateRange.second)

    fun searchExpensesByName(name: String) = expenseDao.searchExpensesByName(name)
    fun sumOfExpensesByName(name: String) = expenseDao.sumOfExpensesByName(name)
    fun getAllCategories() = expenseDao.getAllCategories()

    fun getSumOfExpenses() = expenseDao.getSumOfExpenses()
    fun getSumOfExpenses(category: String) = expenseDao.getSumOfExpenses(category = category)
    fun getSumOfExpenses(dateRange: Pair<Long, Long>) = expenseDao.getSumOfExpenses(startDate = dateRange.first, endDate = dateRange.second)
    fun getSumOfExpenses(category: String, dateRange: Pair<Long, Long>) = expenseDao.getSumOfExpenses(category = category, startDate = dateRange.first, endDate = dateRange.second)
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