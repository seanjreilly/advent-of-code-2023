package day24

import utils.LongBounds
import utils.readInput
import kotlin.math.sign
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day24")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val coordinateRange = 200_000_000_000_000L .. 400_000_000_000_000L
    val bounds = LongBounds(coordinateRange, coordinateRange)
    return countIntersections(input.map { Hailstone(it) }, bounds)
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun countIntersections(hailstones: List<Hailstone>, bounds: LongBounds): Long {
    return hailstones
        .twoElementCombinations()
        .count { isRelevantXYIntersection(it.first, it.second, bounds) }
        .toLong()
}

internal data class Hailstone(val point: LongPoint3D, val velocity: Velocity3D) {

    private val slopeXY = velocity.deltaY.toDouble() / velocity.deltaX
    private val yIntercept = point.y - (this.slopeXY * point.x)

    fun findIntersectionXY(other: Hailstone): DoublePoint? {
        //use slope-intercept equations to find an intersection point
        if (this.slopeXY == other.slopeXY) {
            return null //lines are parallel
        }
        val intersectX = (other.yIntercept - yIntercept) / (this.slopeXY - other.slopeXY)
        val intersectY = (this.slopeXY * intersectX) + this.yIntercept

        return DoublePoint(intersectX, intersectY)
    }

    fun isFuture2D(potentialFuturePoint: DoublePoint): Boolean {
        // this ignores the case where x velocity is zero and a hailstone is moving straight up or down
        // however, that doesn't occur in my production input
        if (potentialFuturePoint.x == point.x.toDouble()) { return true } //stones intersect at current position
        return sign(potentialFuturePoint.x - point.x) == velocity.deltaX.sign.toDouble()
    }

    companion object {
        internal operator fun invoke(line: String) : Hailstone {
            val (x, y, z) = line.substringBefore(" @").split(", ").map { it.trim().toLong() }
            val (deltaX, deltaY, deltaZ) = line.substringAfter("@ ").split(", ").map { it.trim().toInt() }
            return Hailstone(LongPoint3D(x,y,z), Velocity3D(deltaX, deltaY, deltaZ))
        }
    }
}

internal data class LongPoint3D(val x: Long, val y: Long, val z: Long)
internal data class DoublePoint(val x: Double, val y: Double)
internal data class Velocity3D(val deltaX: Int, val deltaY: Int, val deltaZ: Int)

internal fun isRelevantXYIntersection(hailstoneA: Hailstone, hailstoneB: Hailstone, bounds: LongBounds): Boolean {
    val intersection = hailstoneA.findIntersectionXY(hailstoneB) ?: return false
    val tests = listOf(hailstoneA::isFuture2D, hailstoneB::isFuture2D, { it in bounds })
    return tests.all { it.invoke(intersection) }
}

internal operator fun LongRange.contains(d: Double) = d in (first.toDouble() .. last.toDouble())
internal operator fun LongBounds.contains(point: DoublePoint) = (point.x in validXCoordinates) && (point.y in validYCoordinates)

internal fun <T> List<T>.twoElementCombinations() : Sequence<Pair<T, T>> {
    require(size >= 2) { "must have at least 2 elements in the list to generate 2 element combinations" }

    val theList = this
    return sequence {
        for (i in 0 until (size - 1)) {
            for (j in (i + 1) until size) {
                yield(theList[i] to theList[j])
            }
        }
    }
}