package day13

import utils.LongPuzzle
import kotlin.math.min

fun main() = Solution().run()
class Solution : LongPuzzle() {
    override fun part1(input: List<String>) = calculateScore(input, ::findMirrorRow)
    override fun part2(input: List<String>) = calculateScore(input, ::findMirrorRowWithSmudge)
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
    //(if the original was transposed the other way, we ignore it)
    val originalMatch = findMirrorRowInternal(hashCodes)

    //the only one-character changes that matter are making a line that didn't match another line before match now
    return hashCodes
        .withIndex()
        .asSequence()
        .flatMap { hashCodes.withIndex().map { other -> it to other } }
        .filter { it.first.value.oneCharAwayFromEqual(it.second.value) }
        .map { it.first.index to it.second.index }
        .map { pair -> hashCodes.toMutableList().also { it[pair.first] = hashCodes[pair.second] } }
        .firstNotNullOfOrNull { findMirrorRowInternal(it, disallowedValue = originalMatch) }
}

internal fun findMirrorRowInternal(hashCodes: List<HashCode>, disallowedValue: Int? = null): Int? {
    fun verifyMirror(mirrorIndex: Int): Int? {
        var (max, min) = Pair(mirrorIndex, mirrorIndex - 1)
        while (max < hashCodes.size && min >= 0) {
            if (hashCodes[min--] != hashCodes[max++]) {
                return null
            }
        }
        return mirrorIndex
    }

    return hashCodes.indices
        .asSequence()
        .windowed(2, 1)
        .filter { hashCodes[it.first()] == hashCodes[it.last()] }
        .map { it.last() }
        .filter { it != disallowedValue }
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