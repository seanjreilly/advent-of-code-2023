package day21

import utils.*
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day21")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return Garden(input).findLegalDestinations(64).size.toLong()
}

fun part2(input: List<String>): Long {
    return 0
}

internal class Garden(val rocks: Set<Point>, val bounds: Bounds, val startPoint: Point) {
    fun findLegalDestinations(steps: Int): Set<Point> {
        val distanceTree = djikstras(bounds.toSet(), startPoint, neighboursMapping = ::neighboursMapping)
        return distanceTree
            // points that are reachable in fewer than N steps count IF the difference between N and the point's score
            // is a multiple of 2 (the elf can leave and re-enter a point in 2 steps)
            .filter { it.value == steps || (it.value < steps && (steps - it.value) % 2 == 0)}
            .map { it.key }
            .toSet()
    }

    private fun neighboursMapping(currentPoint: Point)  = currentPoint
        .getCardinalNeighbours()
        .filter { it in bounds }
        .filter { it !in rocks }
        .map { it to 1 }

    companion object {
        operator fun invoke(input: List<String>) : Garden {
            val (bounds, points) = parseGridWithPoints(input)
            val startPoint = points.first { it.second == 'S' }.first
            val rocks = points.filter { it.second == '#' }.map { it.first }.toSet()
            return Garden(rocks, bounds, startPoint)
        }
    }
}