package day14

import utils.Point
import utils.readInput
import java.util.*
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day14")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return parse(input).also { it.tiltNorth() }.totalLoad()
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun parse(input: List<String>): Platform {
    val validXCoordinates = input.first().indices
    val validYCoordinates = input.indices

    val charLocations = input
        .flatMapIndexed { y, line -> line.mapIndexed { x, char -> char to Point(x,y) } }
        .groupBy({ it.first }, {it.second })

    val startingBallLocations = charLocations['O']!!.toSet()
    val boulderLocations = charLocations['#']!!.toSet()

    return Platform(validXCoordinates, validYCoordinates, startingBallLocations, boulderLocations)
}

internal class Platform(val xCoords: IntRange, val yCoords: IntRange, var balls: Set<Point>, boulders: Set<Point>) {
    fun tiltNorth() {
        val newBallPositions = mutableSetOf<Point>()
        xCoords.forEach { x ->
            val brickPositions = brickLocationsByColumn[x] ?: TreeSet()
            val ballsInColumn = balls.filter { it.x == x }.sortedByDescending { it.y }.toMutableList() //reverse the list: we'll take from the end
            var minY = -1
            while (ballsInColumn.isNotEmpty()) {
                val p: Point = ballsInColumn.removeLast()
                minY = max(minY, brickPositions.floor(p.y) ?: -1) + 1
                newBallPositions += Point(x, minY)
            }
        }
        balls = newBallPositions
    }

    fun totalLoad(): Long {
        return balls.map { (yCoords.last + 1) - it.y }.sumOf { it.toLong() }
    }

    val brickLocationsByColumn: Map<Int, TreeSet<Int>> = boulders.groupBy({ it.x }, { it.y }).mapValues { TreeSet(it.value) }
}