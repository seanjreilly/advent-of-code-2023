package day02

import utils.LongPuzzle
import kotlin.math.max

private const val RED = "red"
private const val GREEN = "green"
private const val BLUE = "blue"

fun main() = Solution().run()
class Solution : LongPuzzle() {

    override fun part1(input: List<String>) = input.map(::parse).filter { it.possible }.sumOf { it.id }.toLong()
    override fun part2(input: List<String>): Long = input.map(::parse).sumOf { it.power }.toLong()

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
}
data class GameDescription(val id: Int, val maxRed: Int, val maxGreen: Int, val maxBlue: Int) {
    val possible: Boolean = maxRed <= 12 && maxGreen <= 13 && maxBlue <= 14
    val power: Int = maxRed * maxGreen * maxBlue
}