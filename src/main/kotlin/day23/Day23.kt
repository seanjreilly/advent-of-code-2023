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
    return findLongestPathPart2(input).toLong()
}

internal fun findLongestPath(input: List<String>): Int {
    val predicate: (Point, Point, Char) -> Boolean = { newPoint, currentPoint, tileValue ->
        when (tileValue) {
            '.' -> true
            '^' -> newPoint == currentPoint.north()
            'v' -> newPoint == currentPoint.south()
            '>' -> newPoint == currentPoint.east()
            '<' -> newPoint == currentPoint.west()
            else -> false//really #
        }
    }

    return findPath(input, predicate)
}

internal fun findLongestPathPart2(input: List<String>): Int {
    return findPath(input) { _, _, tileValue -> tileValue != '#' }
}

private fun findPath(input: List<String>, predicate: (Point, Point, Char) -> Boolean): Int {
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
            .filter { predicate(it, point, data[it]!!) }
            .forEach { newPoint -> queue += newPoint to updatedVisitedPoints }
    }
    return maxCost
}
