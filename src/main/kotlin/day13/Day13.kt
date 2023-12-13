package day13

import utils.readInput
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day13")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val patterns = input.chunkOnPredicate { it.isBlank() }
    val (rowResults, columnsToFind) = patterns
        .map { Pair(findMirrorRow(it), it) }
        .partition { it.first != null }

    val columnScore = columnsToFind
        .map { it.second.transpose() }
        .mapNotNull { findMirrorRow(it) }
        .sumOf { it.toLong() }

    val rowScore = rowResults.sumOf { it.first!!.toLong() }

    return (rowScore * 100) + columnScore
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun findMirrorRow(input: List<String>): Int? {
    val matchingHashCodes = input.withIndex().groupBy({ it.value.customHashCode() }, { it.index }).values

    fun verifyMirror(mirrorIndex: Int): Int? {
        var (max, min) = Pair(mirrorIndex, mirrorIndex - 1)
        while (max < input.size && min >= 0) {
            val criteria = listOf(min--, max++)
            if (matchingHashCodes.none { it.containsAll(criteria) }) {
                return null
            }
        }
        return mirrorIndex
    }

    return input.indices
        .windowed(2, 1)
        .filter { indices -> matchingHashCodes.any { it.containsAll(indices) } }
        .map { it.last() }
        .firstOrNull { verifyMirror(it) != null }
}

internal fun List<String>.transpose() : List<String> {
    if (this.any { it.length != first().length }) {
        throw IllegalArgumentException("every string in the list must have the same length")
    }
    return (0 until first().length).map { col -> this.map { it[col] }.joinToString("") }
}

internal fun <T> List<T>.chunkOnPredicate(predicate: (T) -> Boolean) : List<List<T>> {
    var remainingEntries = this
    val result = mutableListOf<List<T>>()

    while(remainingEntries.isNotEmpty()) {
        val chunk = remainingEntries.takeWhile{ !predicate(it) }
        result.add(chunk)
        remainingEntries = remainingEntries.subList(min(chunk.size + 1, remainingEntries.size), remainingEntries.size)
    }
    return result
}

typealias HashCode = UInt
internal fun HashCode.oneCharAwayFromEqual(other: HashCode): Boolean = (this xor other).countOneBits() == 1

internal fun String.customHashCode(): HashCode {
    require(length <= 32) { "Only strings of length 32 or shorter are supported" }
    require(toCharArray().all { it in listOf('#', '.') }) { "Only strings composed of '#' or '.' are supported" }
    return this.toCharArray().withIndex().filter { it.value == '#' }.map { 1u shl it.index }.reduceOrNull(UInt::or) ?: 0u
}