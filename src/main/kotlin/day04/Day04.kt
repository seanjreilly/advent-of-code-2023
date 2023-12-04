package day04

import utils.readInput
import kotlin.math.pow
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day04")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input
        .map { parseCard(it) }
        .map { it.points() }
        .sumOf { it.toLong() }
}

fun part2(input: List<String>): Long {
    return 0
}

internal class Card(val winningNumbers: Set<Int>, val numbersYouHave: Set<Int>) {
    val matchingNumberCount = winningNumbers.intersect(numbersYouHave).size

    fun points(): Int {
        if (matchingNumberCount == 0) {
            return 0
        }
        return (2.0).pow(matchingNumberCount - 1).toInt()
    }
}

internal fun parseCard(inputLine: String): Card {
    val winningNumbers = inputLine
        .substringBefore('|')
        .substringAfter(':')
        .split(' ')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toInt() }
        .toSet()

    val numbersYouHave = inputLine
        .substringAfter('|')
        .split(' ')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toInt() }
        .toSet()

    return Card(winningNumbers, numbersYouHave)
}
