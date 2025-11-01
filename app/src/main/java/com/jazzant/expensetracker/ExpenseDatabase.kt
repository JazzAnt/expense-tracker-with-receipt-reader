package com.jazzant.expensetracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase(){
    abstract fun expenseDao(): ExpenseDao

    //companion object so that it can create databases without creating a value
    companion object {
        private var INSTANCE: ExpenseDatabase? = null

        /**
         * If database is instantiated return the instance. Otherwise instantiate it first.
         * Synchronized so that it won't create 2 databases if 2 threads tries to access it.
         */
        fun getInstance(context: Context): ExpenseDatabase{
            return INSTANCE ?: synchronized(this){
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