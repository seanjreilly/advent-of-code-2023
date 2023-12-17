package day16

import utils.CardinalDirection
import utils.CardinalDirection.*
import utils.Point
import utils.PointAndDirection
import utils.readInput
import java.lang.IllegalStateException
import kotlin.streams.asStream
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
    val contraption = Contraption(input)
    return contraption.startingConfigurations()
        .asSequence()
        .asStream()
        .parallel()
        .map { contraption.energizeTiles(it) }
        .map { it.size.toLong() }
        .max(Comparator.naturalOrder()).get()
}

//typealias PointAndDirection = Pair<Point, CardinalDirection>

internal class Contraption (input: List<String>)  {
    init {
        input.forEach { require(it.length == input.first().length) { "tile array must be rectangular" } }
    }

    val tiles: Array<CharArray> = input.map { it.toCharArray() }.toTypedArray()
    val validXCoords = tiles.first().indices
    val validYCoords = tiles.indices

    fun energizeTiles(start: PointAndDirection = Point(0,0) facing East): Set<Point> {
        val energizedTiles = mutableSetOf<Point>()
        val queue = ArrayDeque<PointAndDirection>()
        val cache = mutableSetOf<PointAndDirection>()


        queue.addLast(start)
        while (queue.isNotEmpty()) {
            val (point, direction) = queue.removeFirst()

            // is within bounds?
            if (point.x !in validXCoords || point.y !in validYCoords) {
                continue
            }

            //check cache
            if ((point facing direction) in cache) {
                continue
            }

            // energize tile
            energizedTiles += point

            //retrieve tile
            val tile = tiles[point.y][point.x]

            fun move(direction: CardinalDirection) = point.move(direction) facing direction
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
            cache += (point facing direction)
        }

        return energizedTiles
    }

    fun startingConfigurations(): Set<PointAndDirection> {
        return (
            validXCoords.map { Point(it, validYCoords.first) facing South } +
            validYCoords.map { Point(validXCoords.last, it) facing West } +
            validXCoords.map { Point(it, validYCoords.last) facing North } +
            validYCoords.map { Point(validXCoords.first, it) facing East }
        ).toSet()
    }
}