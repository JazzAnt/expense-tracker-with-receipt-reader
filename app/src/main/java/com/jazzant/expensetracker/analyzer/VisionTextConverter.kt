package com.jazzant.expensetracker.analyzer

import com.google.mlkit.vision.text.Text

/**
 * @return true if the text contains price labels
 */
fun Text.containsPriceLabels(): Boolean
{ return this.toPriceLabelsList().isNotEmpty() }

/**
 * @return true if the text contains the keyword (case insensitive)
 */
fun Text.containsKeyword(keyword: String): Boolean {
    return this.text.contains(keyword, ignoreCase = true)
}
/**
 * Returns all the textBlocks as a String
 * @return A List of all the textBlocks as a String
 */
fun Text.toBlockList(): List<String>{
    val list = mutableListOf<String>()
    this.textBlocks.forEach { textBlock ->
        list.add(textBlock.text)
    }
    return list
}

/**
 * Returns all the textLines as a String
 * @return A list of all the textLines as a String
 */
fun Text.toLineList(): List<String>{
    val list = mutableListOf<String>()
    this.textBlocks.forEach { textBlock ->
        textBlock.lines.forEach { textLine ->
            list.add(textLine.text)
        }
    }
    return list
}

/**
 * Returns a list of all the numbers in the list that resembles a price label as Floats.
 * Emphasis on RESEMBLE, this function finds all numbers with the formatting of a price label
 * (e.g. 10.00) but does not guarantee that it is indeed a price label. Also this function is made
 * with Canadian receipts in mind, no guarantee that it will work perfectly for other currencies.
 * @return a List of all numbers that resembles a price label as a Float.
 */
fun Text.toPriceLabelsList(): List<Float>{
    val list = mutableListOf<Float>()
    val regex = Regex("\\d+\\.\\d+")
    val matches = regex.findAll(this.text)
    for (match in matches)
    {
        val decimal = match.value.split('.')[1]
        //If the decimal value is greater than 2 then it's assumed that it's not a price tag
        if (decimal.length > 2)
        { continue }
        try
        {
            val float = match.value.toFloat()
            list.add(float)
        }
        catch (e: NumberFormatException)
        { continue }
    }
    return list
}