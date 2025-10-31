package com.jazzant.expensetracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expense_table")
data class Expense(
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "date") val date: LocalDate
){
    @PrimaryKey(autoGenerate = true) var expenseId: Int = 0
}
