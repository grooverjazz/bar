package org.groover.bar.util.data

import org.apache.commons.text.similarity.FuzzyScore
import java.util.Locale

class SearchHandler {
    companion object {
        public fun <T> search(
            searchStr: String,
            dataList: List<T>,
            transform: (T) -> String
        ): List<T> {
            val locale = Locale.ROOT
            val formattedSearchStr = searchStr.lowercase(locale)
            val scorer = FuzzyScore(locale)

            // Cache fuzzy search scores
            val scores = dataList.associateWith { d ->
                val dataStr = transform(d).lowercase(locale)

                val scoreStartsWith = if (dataStr.startsWith(searchStr)) 10 else 0
                val scoreStartsWithSpace = if (dataStr.startsWith(searchStr + " ")) 10 else 0
                val scoreFuzzy = scorer.fuzzyScore(dataStr, formattedSearchStr)

                scoreStartsWith + scoreStartsWithSpace + scoreFuzzy
            }

            // Sort
            return dataList.sortedByDescending { scores[it] }
        }
    }
}
