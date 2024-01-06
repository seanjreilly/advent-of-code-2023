package utils

internal fun <T> List<T>.twoElementCombinations() : Sequence<Pair<T, T>> {
    require(size >= 2) { "must have at least 2 elements in the list to generate 2 element combinations" }

    val theList = this
    return sequence {
        for (i in 0 until (size - 1)) {
            for (j in (i + 1) until size) {
                yield(theList[i] to theList[j])
            }
        }
    }
}

internal fun <T> Collection<T>.twoElementCombinations() : Sequence<Pair<T,T>> = this.toList().twoElementCombinations()