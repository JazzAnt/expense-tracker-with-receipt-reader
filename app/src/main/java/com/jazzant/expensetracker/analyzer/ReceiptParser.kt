package com.jazzant.expensetracker.analyzer

/**
 * Parses the price labels using the given strategy and n-value
 * @param strategy The chosen strategy for parsing
 * @param priceLabels The list of price labels to be parsed (usually obtained from Text.toPriceLabelsAsFloatList())
 * @param n A value used by the strategy. Each strategy uses it differently.
 * @return The value of the price label parsed using the strategy
 */
fun parseReceipt(
    strategy: Strategy,
    priceLabels: List<Float>,
    n: Int
): Float
{
    return when (strategy)
    {
        Strategy.NTH_PRICE_LABEL_FROM_LAST -> nthPriceLabelFromLast(priceLabels, n)
        Strategy.NTH_HIGHEST_PRICE_LABEL -> nthHighestPriceLabel(priceLabels, n)
    }
}

private fun nthPriceLabelFromLast(
    priceLabels: List<Float>,
    n: Int
): Float
{ TODO() }

private fun nthHighestPriceLabel(
    priceLabels: List<Float>,
    n: Int
): Float
{ TODO() }