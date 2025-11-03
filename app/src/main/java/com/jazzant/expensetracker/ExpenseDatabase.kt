package com.jazzant.expensetracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import java.util.Date

@Database(entities = [Expense::class], version = 1)
@TypeConverters(DateConverters::class)
abstract class ExpenseDatabase : RoomDatabase(){
    abstract fun expenseDao(): ExpenseDao
}

class DateConverters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun toTimeStamp(value: LocalDate?): Long?{
        return value?.toEpochDay()
    }
}