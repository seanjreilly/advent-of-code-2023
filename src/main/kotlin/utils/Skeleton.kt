@file:Suppress("UNUSED_PARAMETER", "unused") //just a template

package utils

import kotlin.system.measureTimeMillis

/*
 * The skeleton code to run each day
 */

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("DayXX")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return 0
}

fun part2(input: List<String>): Long {
    return 0
}

/*
 * Skeleton code for the test file
 */
private val sampleInput = """
    """.trimIndent().lines()