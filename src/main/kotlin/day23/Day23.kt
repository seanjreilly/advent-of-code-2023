package day23

import utils.Point
import utils.parseGridWithPoints
import utils.readInput
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day23")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return findLongestPath(input).toLong()
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun findLongestPath(input: List<String>): Int {
    require(input.first().count { it == '.' } == 1)
    require(input.last().count { it == '.' } == 1)
    val (bounds, rawData) = parseGridWithPoints(input)
    val data = rawData.toMap()

    val start = data.entries.first { it.key.y == 0 && it.value == '.' }.key
    val end = data.entries.first { it.key.y == bounds.lastY && it.value == '.' }.key

    val queue = ArrayDeque<Pair<Point, Set<Point>>>()
    var maxCost = Int.MIN_VALUE
    queue += start to emptySet()
    while (queue.isNotEmpty()) {
        val (point, visitedPoints) = queue.removeFirst()
        if (point == end) {
            val cost = visitedPoints.size
            maxCost = max(maxCost, cost)
            continue
        }

        val updatedVisitedPoints = visitedPoints + point
        point.getCardinalNeighbours()
            .asSequence()
            .filter { it in bounds }
            .filter { it !in visitedPoints }
            .filter {
                when (data[it]) {
                    '.' -> true
                    '^' -> it == point.north()
                    'v' -> it == point.south()
                    '>' -> it == point.east()
                    '<' -> it == point.west()
                    else -> false//really #
                }
            }
            .forEach { newPoint -> queue += newPoint to updatedVisitedPoints }
    }
    return maxCost
}