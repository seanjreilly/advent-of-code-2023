package day06

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day06")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return parse(input)
        .map { it.waysToBeatRecordDistance() }
        .map { it.toLong() }
        .reduce(Long::times)
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun parse(input: List<String>): List<RaceDescription> {
    val times = parseSpaceDelimitedInts(input.first())
    val recordDistances = parseSpaceDelimitedInts(input.last())
    return times.zip(recordDistances, ::RaceDescription)
}

private fun parseSpaceDelimitedInts(line: String): List<Int> {
    return line
        .substringAfter(":")
        .split(' ')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toInt() }
}

internal data class RaceDescription(val time: Int, val recordDistance: Int) {
    fun waysToBeatRecordDistance(): Int {
        return (0 until time)
            .map { it * (time - it) }
            .count { it > recordDistance }
    }
}