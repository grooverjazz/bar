package org.groover.bar.data.util

import com.willowtreeapps.fuzzywuzzy.diffutils.FuzzySearch
import java.util.Locale

/**
 * Class that is responsible for fuzzy search.
 */
class SearchHandler {
    companion object {
        fun startsWithRatio(
            searchStr: String,
            str: String
        ): Int {
            var count = 0
            for (i in 0..<searchStr.length) {
                if (str[i] == searchStr[i]) count++
                else break
            }
            return count / searchStr.length * 100
        }
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

            // Cache fuzzy search scores
            val scores = data.associateWith { element ->
                val str = transform(element).lowercase(locale)
                val last_name = str.split(" ").last()

                val scoreStartsWith = startsWithRatio(formattedSearchStr, str) / 2
                val scoreStartsWithLastName = if (scoreStartsWith == 0) startsWithRatio(formattedSearchStr, last_name) / 2 - 1 else 0

                val scoreFuzzy = FuzzySearch.partialRatio(str, formattedSearchStr)

                scoreStartsWith + scoreStartsWithLastName + scoreFuzzy
            }

            // Sort
            return data.filter({scores[it]!! > 50}).sortedByDescending { scores[it] }
        }
    }
}
