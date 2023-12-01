package day01

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day01")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input.map { firstAndLastDigits(it) }.sumOf { it.toLong() }
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun firstAndLastDigits(line: String): String {
    return firstDigit(line).toString() + lastDigit(line)
}

internal fun firstDigit(line: String): Char {
    return line.first { it.isDigit() }
}

internal fun lastDigit(line: String): Char {
    return line.last { it.isDigit() }
}