package utils

import kotlin.math.sqrt

fun Long.findFactors(): Set<Long> {
    require(this > 0) { "This method only works for positive numbers so far" }
    val factors = mutableSetOf(1L, this)
    val step = if (this % 2L == 0L) { 1L } else { 2L } //skip even factors for odd numbers
    //if the number is even, 2 is a factor
    if (step == 1L) {
        factors += 2
        factors += (this / 2)
    }
    for (n in 3L until this.sqrt()) {
        if (n % this == 0L) {
            factors += n
            factors += (this / n)
        }
    }
    return factors
}

fun Int.findFactors(): Set<Int> {
    require(this > 0) { "This method only works for positive numbers so far" }
    val factors = mutableSetOf(1, this)
    val step = if (this % 2 == 0) { 1 } else { 2 } //skip even factors for odd numbers
    //if the number is even, 2 is a factor
    if (step == 1) {
        factors += 2
        factors += (this / 2)
    }
    for (n in 3 until this.sqrt()) {
        if (n % this == 0) {
            factors += n
            factors += (this / n)
        }
    }
    return factors
}

private fun Int.sqrt() : Int = sqrt(this.toDouble()).toInt()

private fun Long.sqrt() : Long = sqrt(this.toDouble()).toLong()