package day02

import utils.readInput
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day02")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input.map { parse(it) }.filter { it.possible }.sumOf { it.id }.toLong()
}

fun part2(input: List<String>): Long {
    return 0
}

private const val RED = "red"
private const val GREEN = "green"
private const val BLUE = "blue"

internal fun parse(line: String): GameDescription {
    val id = line.substringBefore(':').substringAfter(' ').toInt()
    val maxValues = mutableMapOf(RED to 0, GREEN to 0, BLUE to 0)
    line
        .substringAfter(':')
        .split(',', ';')
        .map { it.trim() }
        .map { Pair(it.substringBefore(' ').toInt(), it.substringAfter(' ')) }
        .forEach { (value, color) ->
            maxValues[color] = max(maxValues[color]!!, value)
        }
    return GameDescription(id, maxValues[RED]!!, maxValues[GREEN]!!, maxValues[BLUE]!!)
}

data class GameDescription(val id: Int, val maxRed: Int, val maxGreen: Int, val maxBlue: Int) {
    val possible: Boolean = maxRed <= 12 && maxGreen <= 13 && maxBlue <= 14
}