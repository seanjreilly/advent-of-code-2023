package day09

import utils.LongPuzzle

fun main() = Solution().run()
class Solution : LongPuzzle() {
    override fun part1(input: List<String>) = input.map(::parseLongs).sumOf { predictNextValue(it) }

    override fun part2(input: List<String>) = input.map(::parseLongs).sumOf { predictNextValue(it.reversed()) }
}
internal fun parseLongs(line: String) = line.split(" ").map { it.toLong() }

internal fun predictNextValue(history: List<Long>): Long {
    if (history.all { it == 0L }) {
        return 0L //base case: the next value of a history of all zeros is zero
    }
    val delta = history.windowed(2, 1).map { it[1] - it[0] }
    return history.last() + predictNextValue(delta)
}