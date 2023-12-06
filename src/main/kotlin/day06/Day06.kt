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
    val times = parseSpaceDelimitedNumbers(input.first())
    val recordDistances = parseSpaceDelimitedNumbers(input.last())
    return times.zip(recordDistances, ::RaceDescription)
}

private fun parseSpaceDelimitedNumbers(line: String): List<Long> {
    return line
        .substringAfter(":")
        .split(' ')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toLong() }
}

internal data class RaceDescription(val time: Long, val recordDistance: Long) {
    fun waysToBeatRecordDistance(): Int {
        val longRange = 0L until time
        return longRange
            .map { it * (time - it) }
            .count { it > recordDistance }
    }
}