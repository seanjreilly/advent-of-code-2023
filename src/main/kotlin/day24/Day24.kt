package day24

import utils.LongBounds
import utils.readInput
import kotlin.math.roundToLong
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

    private val slopeXZ = velocity.deltaZ.toDouble() / velocity.deltaX
    private val zIntercept = point.z - (this.slopeXZ * point.x)

    fun findIntersectionXY(other: Hailstone): DoublePoint? {
        //use slope-intercept equations to find an intersection point
        if (this.slopeXY == other.slopeXY) {
            return null //lines are parallel
        }
        val intersectX = (other.yIntercept - yIntercept) / (this.slopeXY - other.slopeXY)
        val intersectY = (this.slopeXY * intersectX) + this.yIntercept

        if (intersectX.isNaN() || intersectY.isNaN()) {
            return null // not a real intersection
        }

        return DoublePoint(intersectX, intersectY)
    }

    fun findIntersectionXZ(other: Hailstone): DoublePointXZ? {
        //use slope-intercept equations to find an intersection point
        if (this.slopeXZ == other.slopeXZ) {
            return null // lines are parallel
        }
        val intersectX = (other.zIntercept - zIntercept) / (this.slopeXZ - other.slopeXZ)
        val intersectZ = (this.slopeXZ * intersectX) + this.zIntercept

        if (intersectX.isNaN() || intersectZ.isNaN()) {
            return null // not a real intersection
        }

        return DoublePointXZ(intersectX, intersectZ)
    }

    fun isFuture2D(potentialFuturePoint: DoublePoint): Boolean {
        // this ignores the case where x velocity is zero and a hailstone is moving straight up or down
        // however, that doesn't occur in my production input
        if (potentialFuturePoint.x == point.x.toDouble()) { return true } //stones intersect at current position
        return sign(potentialFuturePoint.x - point.x) == velocity.deltaX.sign.toDouble()
    }

    // Finds the (truncated to whole numbers) coordinates of the intersection point
    // if one exists, as well as the time they intersect.
    // Returns null if the rays do not intersect.
    fun findIntersection3D(other: Hailstone): Pair<LongPoint3D, Long>? {
        val xyIntersection = findIntersectionXY(other)
        val xzIntersection = findIntersectionXZ(other)

        if (xyIntersection == null || xzIntersection == null) {
            return null
        }

        val (x, y, z) = listOf(xyIntersection.x, xyIntersection.y, xzIntersection.z).map(Double::roundToLong)

        if (x != xzIntersection.x.roundToLong()) {
            return null // not actually an intersection
        }

//        check(x == xzIntersection.x.roundToLong()) { "X coordinates from 2 planar intersections don't match. X from XY is ${xyIntersection.x}. X from XZ is ${xzIntersection.x}" }

        val intersection = LongPoint3D(x,y,z)
        val netVelocity = velocity - other.velocity
        val intersectionTime = (other.point.x - point.x) / netVelocity.deltaX.toLong()
        return intersection to intersectionTime
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
internal data class DoublePointXZ(val x: Double, val z: Double)
internal data class Velocity3D(val deltaX: Int, val deltaY: Int, val deltaZ: Int) {
    internal operator fun minus(other: Velocity3D) : Velocity3D {
        return Velocity3D(this.deltaX - other.deltaX, this.deltaY - other.deltaY, this.deltaZ - other.deltaZ)
    }
}

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