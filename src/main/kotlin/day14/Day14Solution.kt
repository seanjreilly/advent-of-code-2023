package day14

import utils.IntSolution
import utils.Point
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() = Day14Solution().run()
class Day14Solution : IntSolution() {
    override fun part1(input: List<String>) = parse(input).also { it.tiltNorth() }.totalLoad()
    override fun part2(input: List<String>) = parse(input).also { it.spinCycle(1_000_000_000) }.totalLoad()
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

    val brickLocationsByColumn: Map<Int, TreeSet<Int>> = boulders.groupBy({ it.x }, { it.y }).mapValues { TreeSet(it.value) }
    val brickLocationsByRow: Map<Int, TreeSet<Int>> = boulders.groupBy({ it.y }, { it.x }).mapValues { TreeSet(it.value) }

    fun tiltNorth() {
        val newBallPositions = mutableSetOf<Point>()
        val ballsByX = balls.sortedByDescending { it.y }.groupBy { it.x } //reverse the sort because we'll take from the end
        xCoords.forEach { x ->
            val brickPositions = brickLocationsByColumn[x] ?: TreeSet()
            val ballsInColumn = ballsByX[x]?.toMutableList() ?: ArrayList()
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
        val ballsByY = balls.sortedByDescending { it.x }.groupBy { it.y } //reverse the sort because we'll take from the end
        yCoords.forEach { y ->
            val brickPositions = brickLocationsByRow[y] ?: TreeSet()
            val ballsInRow = ballsByY[y]?.toMutableList() ?: ArrayList()
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
        val ballsByX = balls.sortedBy { it.y }.groupBy { it.x } // double-reverse the sort because we'll take from the end
        xCoords.forEach { x ->
            val brickPositions = brickLocationsByColumn[x] ?: TreeSet()
            val ballsInColumn = ballsByX[x]?.toMutableList() ?: ArrayList()
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
        val ballsByY = balls.sortedBy { it.x }.groupBy { it.y } //double reverse the sort because we'll take from the end
        yCoords.forEach { y ->
            val brickPositions = brickLocationsByRow[y] ?: TreeSet()
            val ballsInRow = ballsByY[y]?.toMutableList() ?: ArrayList()
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
}