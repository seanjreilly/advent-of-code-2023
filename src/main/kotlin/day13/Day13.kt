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
    return calculateScore(input, ::findMirrorRow)
}

fun part2(input: List<String>): Long {
    return calculateScore(input, ::findMirrorRowWithSmudge)
}

private fun calculateScore(input: List<String>, transformation: (List<String>) -> Int?): Long {
    val patterns = input.chunkOnPredicate { it.isBlank() }
    val (rowResults, columnsToFind) = patterns
        .map { Pair(transformation(it), it) }
        .partition { it.first != null }

    val columnScore = columnsToFind
        .map { it.second.transpose() }
        .mapNotNull { transformation(it) }
        .sumOf { it.toLong() }

    val rowScore = rowResults.sumOf { it.first!!.toLong() }

    return (rowScore * 100) + columnScore
}

internal fun findMirrorRow(pattern: List<String>): Int? {
    return findMirrorRowInternal(pattern.map { it.customHashCode() })
}

internal fun findMirrorRowWithSmudge(pattern: List<String>): Int? {
    val hashCodes = pattern.map { it.customHashCode() }

    //find the original match, so we can ensure we don't return it
    val originalMatch = findMirrorRowInternal(hashCodes)

    //brute force all possible 1 character changes
//    val potentialAnswers = hashCodes
//        .indices
//        .flatMap { lineIndex -> (0 until pattern.first().length).map { Pair(lineIndex, it) }}
//        .map { (lineIndex, columnIndex) -> ArrayList(hashCodes).also { it[lineIndex] = hashCodes[lineIndex].flipBit(columnIndex) } }
//        .map { findMirrorRowInternal(it) }
//        .filterNotNull()
//        .filter { it != originalMatch }

    //the only one-character changes that matter are making a line that didn't match another line before match now
    val potentialAnswers = hashCodes
        .withIndex()
        .flatMap { hashCodes.withIndex().map { other -> it to other } }
        .asSequence()
        .filter { it.first.value.oneCharAwayFromEqual(it.second.value) }
        .map { it.first.index to it.second.index }
        .map { pair -> ArrayList(hashCodes).also<ArrayList<HashCode>> { it[pair.first] = hashCodes[pair.second] } }
        .map { findMirrorRowInternal(it) }
        .filterNotNull()
        .filter { it != originalMatch }

    return potentialAnswers.firstOrNull()
}

internal fun findMirrorRowInternal(hashCodes: List<HashCode>): Int? {
    val matchingHashCodes = hashCodes.withIndex().groupBy({ it.value }, { it.index }).values

    fun verifyMirror(mirrorIndex: Int): Int? {
        var (max, min) = Pair(mirrorIndex, mirrorIndex - 1)
        while (max < hashCodes.size && min >= 0) {
            val criteria = listOf(min--, max++)
            if (matchingHashCodes.none { it.containsAll(criteria) }) {
                return null
            }
        }
        return mirrorIndex
    }

    return hashCodes.indices
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

internal fun HashCode.flipBit(index: Int): HashCode = (1u shl index) xor this

internal fun String.customHashCode(): HashCode {
    require(length <= 32) { "Only strings of length 32 or shorter are supported" }
    require(toCharArray().all { it in listOf('#', '.') }) { "Only strings composed of '#' or '.' are supported" }
    return this.toCharArray().withIndex().filter { it.value == '#' }.map { 1u shl it.index }.reduceOrNull(UInt::or) ?: 0u
}