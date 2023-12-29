package utils

import kotlin.math.abs

data class LongPoint(val x: Long, val y: Long) {
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())
    fun north() = LongPoint(x, y - 1)
    fun northEast() = LongPoint(x + 1, y - 1)
    fun east() = LongPoint(x + 1, y)
    fun southEast() = LongPoint(x + 1, y + 1)
    fun south() = LongPoint(x, y + 1)
    fun southWest() = LongPoint(x - 1, y + 1)
    fun west() = LongPoint(x - 1, y)
    fun northWest() = LongPoint(x - 1, y - 1)

    fun getCardinalNeighbours() : Collection<LongPoint> {
        return listOf(
            north(),
            south(),
            east(),
            west(),
        )
    }

    fun manhattanDistance(other: LongPoint): Long {
        return abs(other.x - x) + abs(other.y - y)
    }
}

data class LongBounds(val validXCoordinates: LongRange, val validYCoordinates: LongRange) : Iterable<LongPoint> {
    constructor(validXCoordinates: IntRange, validYCoordinates: IntRange) : this(validXCoordinates.toLongRange(), validYCoordinates.toLongRange())
    operator fun contains(point: LongPoint) = point.x in validXCoordinates && point.y in validYCoordinates

    override fun iterator() = iterator {
        for (x in validYCoordinates) {
            for (y in validYCoordinates) {
                yield(LongPoint(x, y))
            }
        }
    }
}

fun IntRange.toLongRange() : LongRange = this.first.toLong() .. this.last.toLong()