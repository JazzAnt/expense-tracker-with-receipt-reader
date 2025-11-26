package com.jazzant.expensetracker.database.receiptmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipt_model_table")
data class ReceiptModel(
    @PrimaryKey
    @ColumnInfo(name = "keyword") val keyword: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "parserStrategyId") val parserStrategyId: Int,
    @ColumnInfo(name = "parserStrategyValue1") val parserStrategyValue1: Int
)
