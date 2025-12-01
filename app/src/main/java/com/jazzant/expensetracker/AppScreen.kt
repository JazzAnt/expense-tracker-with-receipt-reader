package com.jazzant.expensetracker

enum class AppScreen(){
    HOME_SCREEN,
    RECEIPT_MODEL_LIST,
    EDIT_EXPENSE,
    REQUEST_CAMERA_PERMISSION,
    CAMERA_PREVIEW,
    TEXT_RECOGNIZER,
    TEXT_ANALYZER,
    CHOOSE_KEYWORD,
    CHOOSE_NAME,
    CHOOSE_AMOUNT,
    CHOOSE_CATEGORY,
    ANALYZING_STRATEGY,
    CHOOSE_STRATEGY
}

val CAMERA_ANALYZE_SCREENS = arrayOf(
    AppScreen.CAMERA_PREVIEW,
    AppScreen.TEXT_RECOGNIZER,
    AppScreen.TEXT_ANALYZER,
)
val RECEIPT_MODELING_SCREENS = arrayOf(
    AppScreen.CHOOSE_KEYWORD,
    AppScreen.CHOOSE_NAME,
    AppScreen.CHOOSE_AMOUNT,
    AppScreen.CHOOSE_CATEGORY,
    AppScreen.ANALYZING_STRATEGY,
    AppScreen.CHOOSE_STRATEGY,
)

fun getReceiptModelingScreenTitle(appScreen: AppScreen): String{
    return when (appScreen) {
        AppScreen.CHOOSE_KEYWORD -> "Select the Keyword"
        AppScreen.CHOOSE_NAME -> "Select the Name"
        AppScreen.CHOOSE_AMOUNT -> "Select the Expense Amount"
        AppScreen.CHOOSE_CATEGORY -> "Select the Category"
        AppScreen.ANALYZING_STRATEGY -> "Analyzing Strategies"
        AppScreen.CHOOSE_STRATEGY -> "Select the Strategy"
        else -> "(if you see this the dev did something wrong)"
    }

}
