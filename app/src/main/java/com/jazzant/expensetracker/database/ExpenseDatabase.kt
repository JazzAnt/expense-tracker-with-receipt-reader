package com.jazzant.expensetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jazzant.expensetracker.database.expense.Expense
import com.jazzant.expensetracker.database.expense.ExpenseDao

@Database(entities = [Expense::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        private var INSTANCE: ExpenseDatabase? = null
        fun getInstance(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = ExpenseDatabase::class.java,
                    name = "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}