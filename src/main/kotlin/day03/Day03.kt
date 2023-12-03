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
    return findPartNumbers(input).sumOf { it.value.toLong() }
}

fun part2(input: List<String>): Long {
    return findGears(input).sumOf { it.firstPartNumber.toLong() * it.secondPartNumber.toLong() }
}

private val DIGITS_REGEX = """\d+""".toRegex()

internal fun findPartNumbers(input: List<String>): List<PartNumber> {
    val validSymbolPositions = input.buildSymbolPositions()
    return input.flatMapIndexed { lineNumber, line ->
        DIGITS_REGEX.findAll(line)
            .map { match ->
                val adjacentSymbolPositions = generatePotentialSymbolPositions(match, lineNumber)
                    .filter { it in validSymbolPositions }
                    .toSet()
                PartNumber(match.value, adjacentSymbolPositions)
            }
            .filter { it.adjacentSymbolPositions.isNotEmpty() }
            .toList()
    }
}

private fun generatePotentialSymbolPositions(match: MatchResult, lineNumber: Int): List<SymbolPosition> {
    val extendedRange = (match.range.first - 1)..(match.range.last + 1) //check +/- 1 character from match
    val lineNumbers = (lineNumber - 1)..(lineNumber + 1) //on the line above, the line, and the line below
    return lineNumbers.flatMap { ln -> extendedRange.map { SymbolPosition(ln, it) } }
}

internal typealias Predicate = (Char) -> Boolean

internal fun List<String>.buildSymbolPositions(predicate: Predicate = Char::isSymbol): Set<SymbolPosition> {
    return this
        .flatMapIndexed { lineNumber, line ->
            line.toCharArray()
                .withIndex()
                .filter { predicate.invoke(it.value) }
                .map { it.index }
                .toSet().map { SymbolPosition(lineNumber, it) }
        }
        .toSet()
}

internal fun Char.isSymbol() = this != '.' && !this.isDigit()

internal data class SymbolPosition(val line: Int, val column: Int)

internal data class PartNumber(val value: String, val adjacentSymbolPositions: Set<SymbolPosition>) {
    internal constructor(value: String, vararg adjacentSymbolPositions: SymbolPosition) : this(value, adjacentSymbolPositions.toSet())
}

internal class Gear(val firstPartNumber: String, val secondPartNumber: String) {
    val partNumbers = setOf(firstPartNumber, secondPartNumber)
}

internal fun findGears(input: List<String>): List<Gear> {
    //build an inverse mapping of symbol positions to adjacent part numbers
    val partNumbersByAdjacentSymbolPosition = findPartNumbers(input)
        .flatMap { partNumber -> partNumber.adjacentSymbolPositions.map { Pair(it, partNumber.value) } }
        .groupBy( { it.first }, { it.second } )

    return input
        .buildSymbolPositions { it == '*' }
        .map { partNumbersByAdjacentSymbolPosition[it] ?: emptyList() } //a star symbol might not be adjacent to any part numbers
        .filter { it.size == 2 }
        .map { Gear(it.first(), it.last()) }
}