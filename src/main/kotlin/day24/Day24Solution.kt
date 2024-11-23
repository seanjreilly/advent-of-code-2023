package day24

import utils.LongBounds
import utils.LongSolution
import utils.twoElementCombinations
import kotlin.math.roundToLong
import kotlin.math.sign

fun main() = Day24Solution().run()
class Day24Solution : LongSolution() {

    override fun part1(input: List<String>): Long {
        val coordinateRange = 200_000_000_000_000L..400_000_000_000_000L
        val bounds = LongBounds(coordinateRange, coordinateRange)
        return countIntersections(input.map { Hailstone(it) }, bounds)
    }

    override fun part2(input: List<String>): Long {
        val (solutionPosition, _) = findRockThatHitsAllHailstones(input.map { Hailstone(it) })
        return solutionPosition.x + solutionPosition.y + solutionPosition.z
    }
}

internal fun countIntersections(hailstones: List<Hailstone>, bounds: LongBounds): Long {
    return hailstones
        .twoElementCombinations()
        .count { isRelevantXYIntersection(it.first, it.second, bounds) }
        .toLong()
}

internal fun findRockThatHitsAllHailstones(hailstones: List<Hailstone>): Pair<LongPoint3D, Velocity3D> {
    // find potential velocities, by taking advantage of hailstones with the same X, Y, and Z velocities
    val potentialSolutionVelocities = findPotentialRockVelocities(hailstones)

    // turn potential velocities into potential solutions by deriving the initial position (if any) for each velocity
    val potentialSolutions = potentialSolutionVelocities
        .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

    // we know there is a solution, and we know there's only one
    // check each potential solution and see if it hits every hailstone
    return potentialSolutions
        .first { (position, velocity) -> verifyPotentialPart2Solution(position, velocity, hailstones) }
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

        val intersection = LongPoint3D(x, y, z)
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

internal fun verifyPotentialPart2Solution(rockPosition: LongPoint3D, rockVelocity: Velocity3D, hailstones: Collection<Hailstone>): Boolean {

    fun findIntersectionTime(hailstone: Hailstone): Long? {
        val xClosingVelocity = rockVelocity.deltaX - hailstone.velocity.deltaX
        return if (xClosingVelocity == 0) {
            if (rockPosition.x != hailstone.point.x) {
                return null //rock will never hit the hailstone
            } else {
                // parallel and equal in the x-coordinate
                // try finding t using Y coordinates...
                val yClosingVelocity = rockVelocity.deltaY - hailstone.velocity.deltaY
                if (yClosingVelocity == 0) {
                    if (rockPosition.y != hailstone.point.y) {
                        return null //rock will never hit the hailstone
                    } else {
                        TODO("can't yet handle a case where the rock is parallel to a hailstone in x AND y ")
                    }
                }
                ((hailstone.point.y - rockPosition.y).toBigInteger() / yClosingVelocity.toBigInteger()).toLong()
            }
        } else {
            ((hailstone.point.x - rockPosition.x).toBigInteger() / xClosingVelocity.toBigInteger()).toLong()
        }
    }

    fun verify(hailstone: Hailstone) : Boolean {
        val t = findIntersectionTime(hailstone) ?: return false //no intersection time means no intersection
        if (t < 0L) {
            return false //rock and hailstone have to collide in the future (or at time 0?)
        }

        val rockXAtT = rockPosition.x + (t * rockVelocity.deltaX)
        val hailstoneXAtT = hailstone.point.x + (t * hailstone.velocity.deltaX)

        if (rockXAtT != hailstoneXAtT) {
            return false
        }

        val rockYAtT = rockPosition.y + (t * rockVelocity.deltaY)
        val hailstoneYAtT = hailstone.point.y + (t * hailstone.velocity.deltaY)

        if (rockYAtT != hailstoneYAtT) {
            return false
        }

        val rockZAtT = rockPosition.z + (t * rockVelocity.deltaZ)
        val hailstoneZAtT = hailstone.point.z + (t * hailstone.velocity.deltaZ)

        return rockZAtT == hailstoneZAtT
    }
    return hailstones.all { verify(it) }
}

internal fun findPotentialRockOrigin(potentialRockVelocity: Velocity3D, hailstones: Collection<Hailstone>) : LongPoint3D? {
    //any two hailstones will do
    val hailstoneA = hailstones.first()
    val hailstoneB = hailstones.last()

    // each of these is the line of potential starting points that can hit this hailstone with the given velocity
    val hailstoneAPrime = hailstoneA.copy(velocity = hailstoneA.velocity - potentialRockVelocity)
    val hailstoneBPrime = hailstoneB.copy(velocity = hailstoneB.velocity - potentialRockVelocity)

    // the intersection of these two lines is the potential starting point
    return hailstoneAPrime.findIntersection3D(hailstoneBPrime)?.first
}

internal fun findPotentialRockVelocities(hailstones: Collection<Hailstone>) : Set<Velocity3D> {
    val potentialXComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaX }, { it.point.x })
    val potentialYComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaY }, { it.point.y })
    val potentialZComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaZ }, { it.point.z })

    return potentialXComponentValues.flatMap { deltaX ->
        potentialYComponentValues.flatMap { deltaY ->
            potentialZComponentValues.map { deltaZ ->
                Velocity3D(deltaX.toInt(), deltaY.toInt(), deltaZ.toInt())
            }
        }
    }.toSet()
}

internal fun findPotentialValuesForVelocityComponent(
    hailstones: Collection<Hailstone>,
    velocityComponentSelector: (Hailstone) -> Int,
    positionComponentSelector: (Hailstone) -> Long
): Set<Long> {
    /*
    Find the hailstones with duplicate velocity values for this coordinate (i.e., they are parallel in this dimension).
    Assuming that the rock's position and velocity have to be whole numbers, then a rock that can hit all of these
    hailstones can have only a few possible velocities in this dimension, because these hailstones are always the
    same distance apart.
    */

    val (deltaValue, duplicateHailstones) = hailstones
        .groupBy(velocityComponentSelector)
        .entries.maxByOrNull { it.value.size }!!

//        val positiveOrNegativeOne = listOf(1L, -1L)
//        val plusOrMinusDeltaValue = listOf(deltaValue.toLong(), deltaValue * -1L)

    // find the potential delta values that will work for these 2 hailstones in this velocity coordinate
    // the rock has to get from p1 to p2 in exactly an integer number of turns
    // it could be going backwards, and the velocity of the hailstones could be moving towards the rock
    // or away from it.
    fun findPotentialDeltaValues(hailstonePair: Pair<Hailstone, Hailstone>): Set<Long> {
        val (h1, h2) = hailstonePair
        val p1 = positionComponentSelector(h1)
        val p2 = positionComponentSelector(h2)
        val difference = p1 - p2

//            val factors = abs(difference).findFactors() //the rock has to get from p1 to p2 in exactly an integer number of turns
//            val factorsMethod = factors
//                .flatMap { factor -> positiveOrNegativeOne.map { it * factor } } // times plus or minus one
//                .flatMap { value -> plusOrMinusDeltaValue.map { it + value } } // plus or minus this hailstone's velocity component
//                .toSet()
//
//            return factorsMethod

        // using factors to find potential velocities *should* work, but there's a bug in it somewhere :-(
        // and it's much slower than just brute forcing anyway

        val bruteForceMethod = (-1000 .. 1000)
            .filter { v -> v != deltaValue }
            .filter { v -> difference % (v - deltaValue) == 0L }
            .map { it.toLong() }
            .toSet()

        return bruteForceMethod
    }

    //the values have to work for each potential combination of 2 hailstones — throw out anything that doesn't work with a different pair
    val hailstoneCombinations = duplicateHailstones.twoElementCombinations().toList()
    val possibleValues = findPotentialDeltaValues(hailstoneCombinations.first()).toMutableSet()
    hailstoneCombinations
        .drop(1)
        .forEach { possibleValues.retainAll(findPotentialDeltaValues(it)) }

    return possibleValues
}