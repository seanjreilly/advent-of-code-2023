package day12

import utils.readInput
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day12")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input.map(ConditionRecord::parse).sumOf { it.countPossibleArrangements() }
}

fun part2(input: List<String>): Long {
    return 0
}

internal data class ConditionRecord(val damagedRecord: String, val alternateFormat: List<Int>) {
    private val recordChars = "$damagedRecord.".toCharArray()

    fun countPossibleArrangements(parsePosition: Int = 0, currentRunLength: Int = 0, remainingConstraints: List<Int> = alternateFormat): Long {
        return countPossibleArrangementsInternal(parsePosition, currentRunLength, remainingConstraints)
    }

    private fun countPossibleArrangementsInternal(parsePosition: Int, currentRunLength: Int, remainingConstraints: List<Int>) : Long {
        if (remainingConstraints.isEmpty()) {
            /*
            1 legal possibility here, IF there are no definitely broken springs left
            (wildcards are ok, they would just all resolve to '.')
            if there are broken string left this isn't legal
            */
            return if ('#' !in recordChars.drop(parsePosition)) { 1 } else { 0 }
        }

        /*
        end of the character string
        we've appended a '.' to the end, so we know that it doesn't end in the middle of a run
        */
        if (parsePosition == recordChars.size) {
            // legal if there are no unresolved constraints left
            @Suppress("KotlinConstantConditions") //analyzer error — it's not always empty
            return if (remainingConstraints.isEmpty()) { 1 } else { 0 }
        }

        fun doNotStartRun() = countPossibleArrangements(parsePosition + 1, 0, remainingConstraints)
        fun endCurrentRun() = countPossibleArrangements(parsePosition + 1, 0, remainingConstraints.drop(1))
        fun startOrContinueRun() = countPossibleArrangements(parsePosition + 1, currentRunLength + 1, remainingConstraints)

        return when(recordChars[parsePosition]) {
            '.' -> {
                when (currentRunLength) {
                    0 -> doNotStartRun()
                    remainingConstraints.first() -> endCurrentRun()
                    else -> 0 //wouldn't be legal
                }
            }
            '#' -> {
                when(currentRunLength) {
                    remainingConstraints.first() -> 0 //not legal
                    else -> startOrContinueRun()
                }
            }
            '?' -> {
                when(currentRunLength) {
                    0 -> startOrContinueRun() + doNotStartRun() //start or don't start
                    remainingConstraints.first() -> endCurrentRun()
                    else -> startOrContinueRun() //continue the current run
                }
            }
            else -> throw IllegalStateException("unexpected character '${recordChars[parsePosition]}'")
        }
    }

    companion object {
        internal fun parse(line: String): ConditionRecord {
            return ConditionRecord(line.substringBefore(' '), line.substringAfter(' ').split(',').map { it.toInt() })
        }
    }
}

internal fun unfold(line: String): String {
    val parts = line.split(' ')
    return (List(5) { parts.first() }).joinToString("?") + " " + (List(5) { parts.last() }).joinToString(",")
}