package day14

import utils.Point
import utils.readInput
import java.util.*
import kotlin.math.max
import kotlin.math.min
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
    return parse(input).also { it.tiltNorth() }.totalLoad().toLong()
}

fun part2(input: List<String>): Long {
    return parse(input).also { it.spinCycle(1_000_000_000) }.totalLoad().toLong()
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

    fun tiltWest() {
        val newBallPositions = mutableSetOf<Point>()
        yCoords.forEach { y ->
            val brickPositions = brickLocationsByRow[y] ?: TreeSet()
            val ballsInRow = balls.filter { it.y == y }.sortedByDescending { it.x }.toMutableList() //reverse the list: we'll take from the end
            var minX = -1
            while (ballsInRow.isNotEmpty()) {
                val p: Point = ballsInRow.removeLast()
                minX = max(minX, brickPositions.floor(p.x) ?: -1) + 1
                newBallPositions += Point(minX, y)
            }
        }
        balls = newBallPositions
    }

    fun tiltSouth() {
        val newBallPositions = mutableSetOf<Point>()
        xCoords.forEach { x ->
            val brickPositions = brickLocationsByColumn[x] ?: TreeSet()
            val ballsInColumn = balls.filter { it.x == x }.sortedBy { it.y }.toMutableList() //"reverse" the list: we'll take from the end
            var maxY = yCoords.last + 1
            while (ballsInColumn.isNotEmpty()) {
                val p: Point = ballsInColumn.removeLast()
                maxY = min(maxY, brickPositions.ceiling(p.y) ?: Int.MAX_VALUE) - 1
                newBallPositions += Point(x, maxY)
            }
        }
        balls = newBallPositions
    }

    fun tiltEast() {
        val newBallPositions = mutableSetOf<Point>()
        yCoords.forEach { y ->
            val brickPositions = brickLocationsByRow[y] ?: TreeSet()
            val ballsInRow = balls.filter { it.y == y }.sortedBy { it.x }.toMutableList() //"reverse" the list: we'll take from the end
            var maxX = xCoords.last + 1
            while (ballsInRow.isNotEmpty()) {
                val p: Point = ballsInRow.removeLast()
                maxX = min(maxX, brickPositions.ceiling(p.x) ?: Int.MAX_VALUE) - 1
                newBallPositions += Point(maxX, y)
            }
        }
        balls = newBallPositions
    }

    fun totalLoad(): Int {
        return balls.sumOf { (yCoords.last + 1) - it.y }
    }

    fun spinCycle(spins: Int) {
        val cycleDetection = mutableMapOf<Set<Point>, Int>()
        var spinsSoFar = 0

        do {
            tiltNorth()
            tiltWest()
            tiltSouth()
            tiltEast()

            spinsSoFar++

            val cycleStart = cycleDetection[balls]
            if (cycleStart != null) {
                val cycleLength = spinsSoFar - cycleStart
                val spinsRemainingInPartialCycle = (spins - cycleStart) % cycleLength

                // we could spin a few more times, but since we've run a full cycle
                // we already have the intermediate value in the cache
                balls = cycleDetection.entries.first { it.value == spinsRemainingInPartialCycle + cycleStart }.key
                return
            } else {
                cycleDetection[balls] = spinsSoFar
            }
        } while (spinsSoFar < spins)
    }

    val brickLocationsByColumn: Map<Int, TreeSet<Int>> = boulders.groupBy({ it.x }, { it.y }).mapValues { TreeSet(it.value) }
    val brickLocationsByRow: Map<Int, TreeSet<Int>> = boulders.groupBy({ it.y }, { it.x }).mapValues { TreeSet(it.value) }
}