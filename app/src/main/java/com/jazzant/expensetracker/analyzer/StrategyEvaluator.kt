package com.jazzant.expensetracker.analyzer

/**
 * Checks all available strategies with all possible N values and see which combination returns
 * a result that matches the desiredPriceLabel
 * @param priceLabels the list of price labels of the receipt (usually obtained from Text.toPriceLabelsAsFloatList())
 * @param desiredPriceLabel the price label that the strategy should produce when the strategy is used on priceLabels
 * @return a Map of all the Strategies that returns the desired price label and their respective N value
 */
fun evaluateAllPossibleStrategies(
    priceLabels: List<Float>,
    desiredPriceLabel: Float
): Map<Strategy, Int>
{
    val map = mutableMapOf<Strategy, Int>()
    val maximumN = priceLabels.size - 1
    Strategy.entries.forEach{
        for (n in 0..maximumN){
            val parsedPriceLabel = parseReceipt(it, priceLabels, n)
            if (parsedPriceLabel == desiredPriceLabel)
            {
                map.put(it, n)
                break
            }
        }
    }
    return map
}