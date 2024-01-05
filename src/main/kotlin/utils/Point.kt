package utils

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    fun north() = Point(x, y - 1)
    fun northEast() = Point(x + 1, y - 1)
    fun east() = Point(x + 1, y)
    fun southEast() = Point(x + 1, y + 1)
    fun south() = Point(x, y + 1)
    fun southWest() = Point(x - 1, y + 1)
    fun west() = Point(x - 1, y)
    fun northWest() = Point(x - 1, y - 1)

    fun getCardinalNeighbours() : Collection<Point> {
        return listOf(
            north(),
            south(),
            east(),
            west(),
        )
    }

    fun getCardinalAndDiagonalNeighbours() : Collection<Point> {
        return getCardinalNeighbours() + listOf(
            northEast(),
            northWest(),
            southEast(),
            southWest()
        )
    }

    fun manhattanDistance(other: Point): Int {
        return abs(other.x - x) + abs(other.y - y)
    }

    fun pointsWithManhattanDistance(manhattanDistance: Int): Sequence<Point> {
        return (0..manhattanDistance)
            .asSequence()
            .flatMap { xAdjustment ->
                val yAdjustment = manhattanDistance - xAdjustment
                sequenceOf(
                    Point(this.x + xAdjustment, this.y + yAdjustment),
                    Point(this.x + xAdjustment, this.y - yAdjustment),
                    Point(this.x - xAdjustment, this.y + yAdjustment),
                    Point(this.x - xAdjustment, this.y - yAdjustment),
                )
            }
    }

    fun move(cardinalDirection: CardinalDirection): Point {
        return cardinalDirection.moveOperation.invoke(this)
    }

    infix fun facing(direction: CardinalDirection) = PointAndDirection(this, direction)
}

enum class TurnDirection {
    Left, Right
}

enum class CardinalDirection(internal val moveOperation: (Point) -> Point) {
    North(Point::north),
    East(Point::east),
    South(Point::south),
    West(Point::west);

    fun turn(direction: TurnDirection): CardinalDirection {
        var index = entries.indexOf(this)
        when (direction) {
            TurnDirection.Left -> index--
            TurnDirection.Right -> index++
        }
        return entries[index.mod(entries.size)]
    }

    fun opposite(): CardinalDirection {
        val index = entries.indexOf(this) + 2
        return entries[index.mod(entries.size)]
    }
}

data class PointAndDirection(val point: Point, val direction: CardinalDirection)

data class Bounds(val validXCoordinates: IntRange, val validYCoordinates: IntRange) : Iterable<Point> {
    val lastX = validXCoordinates.last
    val lastY = validYCoordinates.last
    operator fun contains(point: Point) = point.x in validXCoordinates && point.y in validYCoordinates

    override fun iterator() = iterator {
        for (x in validYCoordinates) {
            for (y in validYCoordinates) {
                yield(Point(x, y))
            }
        }
    }
}

/*
    Common extension functions for accessing a nested array with a point
 */
operator fun <T> Array<Array<T>>.get(p: Point) : T = this[p.x][p.y]
operator fun Array<CharArray>.get(p: Point) = this[p.x][p.y]
operator fun Array<IntArray>.get(p: Point) = this[p.x][p.y]