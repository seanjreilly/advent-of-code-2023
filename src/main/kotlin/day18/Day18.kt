package day18

import utils.CardinalDirection
import utils.Point
import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day18")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val border = digBorder(input)
    val interiorPoints = findInteriorPoints(border)
    return (border.size + interiorPoints.size).toLong()
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun digBorder(instructions: List<String>): Set<Point> {
    var lastPoint = Point(0, 0) //starts here
    val border = mutableSetOf(lastPoint)

    instructions
        .map { it.substringBefore(' ') to it.substringAfter(' ').substringBefore(' ').toInt() }
        .map {
            when (it.first) {
                "U" -> CardinalDirection.North
                "D" -> CardinalDirection.South
                "L" -> CardinalDirection.West
                "R" -> CardinalDirection.East
                else -> throw IllegalArgumentException("Unexpected direction '${it.first}'")
            } to it.second
        }
        .map { (direction, squares) ->
            repeat(squares) {
                lastPoint = lastPoint.move(direction)
                border += lastPoint
            }
        }

    require(lastPoint == Point(0, 0)) { "Border must end at its start point" }
    return border
}

data class BoundingBox(val xBounds: IntRange, val yBounds: IntRange) {
    fun points() : Sequence<Point> = xBounds.asSequence().flatMap { x -> yBounds.map { y -> Point(x,y) } }
    operator fun contains(point: Point): Boolean = (point.x in xBounds) && (point.y in yBounds)
}

fun Set<Point>.boundingBox(): BoundingBox {
    val xBounds = this.minOf { it.x }..this.maxOf { it.x }
    val yBounds = this.minOf { it.y }..this.maxOf { it.y }
    return BoundingBox(xBounds, yBounds)
}

internal fun findInteriorPoints(border: Set<Point>): Set<Point> {
    val boundingBox = border.boundingBox()
    val pointsToCheck = boundingBox.points().filter { it !in border }

    val knownInteriorPoints = mutableSetOf<Point>()
    val knownExteriorPoints = mutableSetOf<Point>()

    point@for (potentialPoint in pointsToCheck) {
        //check if this point is an interior point or not
        val alreadyChecked = mutableSetOf<Point>()
        val queue = ArrayDeque<Point>()
        queue.addLast(potentialPoint)
        while (queue.isNotEmpty()) {
            val point = queue.removeFirst()
            if (point in alreadyChecked) {
                continue
            }
            alreadyChecked += point

            if (point in knownExteriorPoints) {
                //we've found the outside, and everything we've touched so far is inside too
                knownExteriorPoints.addAll(alreadyChecked)
                continue@point
            }

            if (point in knownInteriorPoints) {
                //we've found the inside, and everything we've touched so far is inside too
                knownInteriorPoints.addAll(alreadyChecked)
                continue@point
            }

            if (point !in boundingBox) {
                //we've found the outside
                knownExteriorPoints.addAll(alreadyChecked) //everything we've touched so far is outside too
                continue@point
            }

            queue += point.getCardinalNeighbours().filter { it !in border }
        }

        //we've found the inside
        knownInteriorPoints.addAll(alreadyChecked) //everything we've touched in this loop is inside too
        continue@point
    }
    return knownInteriorPoints
}