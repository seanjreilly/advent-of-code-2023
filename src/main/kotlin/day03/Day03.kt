package day03

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day03")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val symbolPositions = input.buildSymbolPositions()
    return input.flatMapIndexed { index, line -> findPartNumbers(line, index, symbolPositions) }.sumOf { it.toLong() }
}

fun part2(input: List<String>): Long {
    return 0
}

private val DIGITS_REGEX = """\d+""".toRegex()

internal fun findPartNumbers(line: String, lineNumber: Int, symbolPositions: Map<Int, Set<Int>>): List<String> {
    return DIGITS_REGEX.findAll(line)
        .filter { match ->
            val extendedRange = (match.range.first - 1)..(match.range.last + 1) //check +/- 1 character from match
            val lineNumbers = (lineNumber - 1) .. (lineNumber + 1) //on the line above, the line, and the line below

            //if any position in the range on any of the three lines matches a symbol position for that line, then this is a good match
            lineNumbers
                .map { symbolPositions[it] ?: emptySet() } //empty set if the line is before the first line or after the last one
                .map { it.intersect(extendedRange)}
                .any { it.isNotEmpty() }
        }
        .map { it.value }
        .toList()
}

internal fun List<String>.buildSymbolPositions() = this
    .map { it.symbolPositions() }
    .withIndex()
    .associate { Pair(it.index, it.value) }

internal fun String.symbolPositions(): Set<Int> {
    return this.toCharArray()
        .withIndex()
        .filter { it.value.isSymbol() }
        .map { it.index }
        .toSet()
}

internal fun Char.isSymbol() = this != '.' && !this.isDigit()