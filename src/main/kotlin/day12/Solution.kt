package day12

import utils.LongPuzzle

fun main() = Solution().run()
class Solution : LongPuzzle() {

    override fun part1(input: List<String>) = input
        .map(ConditionRecord::parse)
        .sumOf { it.countPossibleArrangements() }

    override fun part2(input: List<String>) = input
        .map(::unfold)
        .map(ConditionRecord::parse)
        .sumOf { it.countPossibleArrangements() }
}

internal data class ConditionRecord(val damagedRecord: String, val alternateFormat: List<Int>) {
    private val cache = mutableMapOf<Triple<Int, Int, List<Int>>, Long>()

    fun countPossibleArrangements(parsePosition: Int = 0, currentRunLength: Int = 0, remainingConstraints: List<Int> = alternateFormat): Long {
        val cacheKey = Triple(parsePosition, currentRunLength, remainingConstraints)
        return cache.getOrPut(cacheKey) { countPossibleArrangementsInternal(parsePosition, currentRunLength, remainingConstraints) }
    }

    private fun countPossibleArrangementsInternal(parsePosition: Int, currentRunLength: Int, remainingConstraints: List<Int>) : Long {
        if (parsePosition == damagedRecord.length) {
            // legal iff there is one constraint left that matches the current run count, or no constraints left
            return when(remainingConstraints) {
                listOf(currentRunLength), emptyList<Int>() -> 1
                else -> 0
            }
        }

        if (remainingConstraints.isEmpty()) {
            /*
            1 legal possibility here, IF there are no definitely broken springs left
            (wildcards are ok, they would just all resolve to '.')
            if there are broken string left this isn't legal
            */
            return if ('#' !in damagedRecord.drop(parsePosition)) { 1 } else { 0 }
        }

        fun unbrokenSpring() = countPossibleArrangements(parsePosition + 1, 0, remainingConstraints)
        fun endCurrentRunOfBrokenSprings() = countPossibleArrangements(parsePosition + 1, 0, remainingConstraints.drop(1))
        fun startOrContinueRunOfBrokenSprings() = countPossibleArrangements(parsePosition + 1, currentRunLength + 1, remainingConstraints)

        return when(val char = damagedRecord[parsePosition]) {
            '.' -> {
                when (currentRunLength) {
                    0 -> unbrokenSpring()
                    remainingConstraints.first() -> endCurrentRunOfBrokenSprings()
                    else -> 0 //wouldn't be legal
                }
            }
            '#' -> {
                when(currentRunLength) {
                    remainingConstraints.first() -> 0 //not legal
                    else -> startOrContinueRunOfBrokenSprings()
                }
            }
            '?' -> {
                when(currentRunLength) {
                    0 -> startOrContinueRunOfBrokenSprings() + unbrokenSpring() //start or don't start
                    remainingConstraints.first() -> endCurrentRunOfBrokenSprings()
                    else -> startOrContinueRunOfBrokenSprings() //continue the current run
                }
            }
            else -> throw IllegalStateException("unexpected character '$char'")
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