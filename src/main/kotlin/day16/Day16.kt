package day16

import utils.CardinalDirection
import utils.CardinalDirection.*
import utils.Point
import utils.readInput
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day16")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return Contraption(input).energizeTiles().size.toLong()
}

fun part2(input: List<String>): Long {
    return 0
}

typealias PointAndDirection = Pair<Point, CardinalDirection>

internal class Contraption (input: List<String>)  {
    fun energizeTiles(): Set<Point> {
        val energizedTiles = mutableSetOf<Point>()
        val queue = ArrayDeque<PointAndDirection>()
        val cache = mutableSetOf<PointAndDirection>()


        queue.addLast(Point(0,0) to East)
        while (queue.isNotEmpty()) {
            val (point, direction) = queue.removeFirst()

            // is within bounds?
            if (point.x !in validXCoords || point.y !in validYCoourds) {
                continue
            }

            //check cache
            if ((point to direction) in cache) {
                continue
            }

            // energize tile
            energizedTiles += point

            //retrieve tile
            val tile = tiles[point.y][point.x]

            fun move(direction: CardinalDirection) = point.move(direction) to direction
            fun keepGoing() = listOf(move(direction))
            fun turn (newDirection: CardinalDirection) = listOf(move(newDirection))
            fun split(a: CardinalDirection, b: CardinalDirection) = listOf(move(a), move(b))

            // find next direction(s)
            val nextDirection: List<PointAndDirection> = when(tile) {
                '.' -> keepGoing()
                '/' -> when(direction) {
                    North -> turn(East)
                    East -> turn(North)
                    South -> turn(West)
                    West -> turn(South)
                }
                '\\' -> when(direction) {
                    North -> turn(West)
                    East -> turn(South)
                    South -> turn(East)
                    West -> turn(North)
                }
                '|' -> when (direction) {
                    North, South -> keepGoing()
                    East, West -> split(North, South)
                }
                '-' -> when (direction) {
                    East, West -> keepGoing()
                    North, South -> split(East, West)
                }
                else -> throw IllegalStateException("unexpected tile '$tile'")
            }

            // add next direction(s) to queue
            nextDirection.forEach { queue.addLast(it) }

            // add to cache
            cache += (point to direction)
        }

        return energizedTiles
    }

    init {
        input.forEach { require(it.length == input.first().length) { "tile array must be rectangular" } }
    }

    val tiles: Array<CharArray> = input.map { it.toCharArray() }.toTypedArray()
    val validXCoords = tiles.first().indices
    val validYCoourds = tiles.indices
}