package org.groover.bar.data.util

import org.apache.commons.text.similarity.FuzzyScore
import java.util.Locale

/**
 * Class that is responsible for fuzzy search.
 */
class SearchHandler {
    companion object {
        // (Searches for the specified string in the data)
        fun <T> search(
            searchStr: String,
            data: List<T>,
            transform: (T) -> String,
        ): List<T> {
            if (searchStr == "")
                return data

            val locale = Locale.ROOT
            val formattedSearchStr = searchStr.lowercase(locale)
            val scorer = FuzzyScore(locale)

            // Cache fuzzy search scores
            val scores = data.associateWith { element ->
                val str = transform(element).lowercase(locale)

                val scoreStartsWith = if (str.startsWith(searchStr)) 10 else 0
                val scoreStartsWithSpace = if (str.startsWith("$searchStr ")) 10 else 0
                val scoreFuzzy = scorer.fuzzyScore(str, formattedSearchStr)

                scoreStartsWith + scoreStartsWithSpace + scoreFuzzy
            }

            // Sort
            return data.sortedByDescending { scores[it] }
        }
    }
}
