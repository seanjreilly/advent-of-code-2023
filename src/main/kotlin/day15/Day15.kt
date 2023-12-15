package day15

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day15")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input.first()
        .split(',')
        .filter { it.isNotBlank() }
        .sumOf { it.hash().toLong() }
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun String.hash(): Int {
    var result = 0
    for (c: Char in this.toCharArray()) {
        result += c.code
        result *= 17
        result %= 256
    }
    return result
}