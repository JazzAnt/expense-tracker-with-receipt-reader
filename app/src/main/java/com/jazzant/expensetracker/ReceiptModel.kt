package com.jazzant.expensetracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipt_model_table")
data class ReceiptModel(
    @PrimaryKey
    @ColumnInfo(name = "keyword") val keyword: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amountRecognizerType") val amountRecognizerType: Int,
    @ColumnInfo(name = "amountRecognizerValue1") val amountRecognizerValue1: Int
)
