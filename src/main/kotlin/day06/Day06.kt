package day06

import utils.readInput
import java.math.MathContext
import java.math.RoundingMode
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
        .map { it }
        .reduce(Long::times)
}

fun part2(input: List<String>): Long {
    return parsePart2(input).waysToBeatRecordDistance()
}

internal data class RaceDescription(val time: Long, val recordDistance: Long) {
    fun waysToBeatRecordDistance(): Long {
        // this is actually a quadratic equation of the form tx - x^2 = d (where t = time and d = distance)
        // it starts and ends at negative infinity (forced to zero by the function)
        // the points where x = 0 are the times when we begin and end to break the record
        // we can solve for those using the formula for finding quadratic equation roots
        // x = (-t +/- sqrt(t^2 - 4d))/ -2

        val bSquaredMinus4ac = time.toBigDecimal().pow(2) - (recordDistance.toBigDecimal() * 4.toBigDecimal())
        val rootBSquaredMinus4ac = bSquaredMinus4ac.sqrt(MathContext.DECIMAL128)


        val roots = listOf(
            (time.toBigDecimal().negate() - rootBSquaredMinus4ac) / ((-2).toBigDecimal()),
            (time.toBigDecimal().negate() + rootBSquaredMinus4ac) / ((-2).toBigDecimal()),
        )

        val lowestRoot = roots.min()
        val highestRoot = roots.max()

        val shortestRecordBreakingButtonPress = lowestRoot.round(MathContext(64, RoundingMode.UP)).toLong()
        val longestRecordBreakingButtonPress = highestRoot.round(MathContext(64, RoundingMode.DOWN)).toLong()
        val algebraAnswer = longestRecordBreakingButtonPress - shortestRecordBreakingButtonPress

        //correct for a quadratic root that is an exact match: there's one fewer record breaking option in this case.
        if (shortestRecordBreakingButtonPress * (time - shortestRecordBreakingButtonPress) == recordDistance) {
           return algebraAnswer - 1
        }

        return algebraAnswer
    }
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

internal fun parsePart2(input: List<String>): RaceDescription {
    return RaceDescription(extractDigitsIntoOneNumber(input.first()), extractDigitsIntoOneNumber(input.last()))
}

private fun extractDigitsIntoOneNumber(line:String) : Long {
    return line
        .substringAfter(":")
        .toCharArray()
        .filter { it.isDigit() }
        .joinToString("")
        .toLong()
}