package day24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.LongBounds
import utils.findFactors
import utils.readInput
import kotlin.math.abs
import kotlin.math.truncate

class Day24Test {
    private val sampleInput = """
        19, 13, 30 @ -2,  1, -2
        18, 19, 22 @ -1, -1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @  1, -5, -3
    """.trimIndent().lines()

    @Nested
    inner class HailstoneTest {
        @Test
        fun `constructor should extract a 3d point and velocity from an input line`() {
            val result = Hailstone(sampleInput.first())

            assert(result.point == LongPoint3D(19, 13, 30))
            assert(result.velocity == Velocity3D(-2, 1, -2))
        }

        @Test
        fun `findIntersectionXY should return null given another Hailstone that does not intersect with this one on the XY axis`() {
            val hailstoneA = Hailstone(sampleInput[1])
            val hailstoneB = Hailstone(sampleInput[2])

            val result = hailstoneA.findIntersectionXY(hailstoneB)

            assert(result == null)
        }

        @Test
        fun `findIntersectionXY should return the intersection point given another Hailstone that intersects with this one on the Xy axis`() {
            val hailstoneA = Hailstone(sampleInput[0])
            val hailstoneB = Hailstone(sampleInput[1])

            val result = hailstoneA.findIntersectionXY(hailstoneB)!!

            assert(truncate3(result.x) == 14.333)
            assert(truncate3(result.y) == 15.333)
        }

        @Test
        fun `findIntersectionXZ should return null given another Hailstone that does not intersect with this one on the XZ axis`() {
            val commonVelocity = Velocity3D(1, 1, 1)
            val hailstoneA = Hailstone(LongPoint3D(0,0,0), commonVelocity)
            val hailstoneB = Hailstone(LongPoint3D(1, 1, 1), commonVelocity)

            val result = hailstoneA.findIntersectionXZ(hailstoneB)

            assert(result == null)
        }

        @Test
        fun `findIntersectionXZ should return the intersection point given another Hailstone that intersects with this one on the XZ axis`() {
            val hailstoneA = Hailstone("24, 13, 10 @ -3, 1, 2")
            val hailstoneB = Hailstone("20, 19, 15 @ 1, -5, -3")

            val result = hailstoneA.findIntersectionXZ(hailstoneB)!!

            assert(result.x == 21.0)
            assert(result.z == 12.0)
        }

        @Test
        fun `find intersection3D should return null given another hailstone that does not intersect with this one in 3d space`() {
            val hailstoneA = Hailstone("18, 19, 22 @ -1, -1, -2")
            val hailstoneB = Hailstone("18, 19, 22 @ -1, -1, -2")

            val result = hailstoneA.findIntersection3D(hailstoneB)

            assert(result == null)
        }

        @Test
        fun `find intersection3D should return an intersection point and an intersection time given another hailstone that intersects with this one in 3d space`() {
            val hailstoneA = Hailstone("24, 13, 10 @ -3, 1, 2")
            val hailstoneB = Hailstone("19, 13, 30 @ -2, 1, -2")

            val result = hailstoneA.findIntersection3D(hailstoneB)!!

            assert(result.first == LongPoint3D(9, 18, 20))
            assert(result.second == 5L)
        }

        @Test
        fun `isFuture2D returns true given a DoublePoint that the hailstone will encounter in the future`() {
            val hailstone = Hailstone(sampleInput[0])
            val potentialFuturePoint = DoublePoint(14.333, 15.333)

            assert(hailstone.isFuture2D(potentialFuturePoint))
        }

        @Test
        fun `isFuture2D returns false given a DoublePoint that the hailstone will encounter in the past`() {
            val hailstoneA = Hailstone(sampleInput[0])
            val hailstoneB = Hailstone(sampleInput[4])

            val intersection = hailstoneA.findIntersectionXY(hailstoneB)!!

            assert(!hailstoneA.isFuture2D(intersection))
        }

        @Test
        fun `isFuture2D returns true given a DoublePoint that the hailstone will encounter in the future, even when another hailstone intersects it in the past`() {
            val hailstoneA = Hailstone(sampleInput[0])
            val hailstoneB = Hailstone(sampleInput[4])

            val intersection = hailstoneA.findIntersectionXY(hailstoneB)!!

            assert(hailstoneB.isFuture2D(intersection))
        }

        @Test
        fun `isFuture2D returns true given the DoublePoint that the hailstone is at now`() {
            val hailstone = Hailstone(sampleInput[0])
            val potentialFuturePoint = DoublePoint(hailstone.point.x.toDouble(), hailstone.point.y.toDouble())

            assert(hailstone.isFuture2D(potentialFuturePoint))
        }

    }

    @Test
    fun `isRelevantXYIntersection should return true iff the hailstones intersect in the future for both hailstones, inside the boundary`() {
        val hailstones = sampleInput.map { Hailstone(it) }
        val bounds = LongBounds(7..27, 7..27)

        assert(isRelevantXYIntersection(hailstones[0], hailstones[1], bounds))
        assert(isRelevantXYIntersection(hailstones[0], hailstones[2], bounds))
        assert(!isRelevantXYIntersection(hailstones[0], hailstones[3], bounds))
        assert(!isRelevantXYIntersection(hailstones[0], hailstones[4], bounds))

        assert(!isRelevantXYIntersection(hailstones[1], hailstones[2], bounds))
        assert(!isRelevantXYIntersection(hailstones[1], hailstones[3], bounds))
        assert(!isRelevantXYIntersection(hailstones[1], hailstones[4], bounds))

        assert(!isRelevantXYIntersection(hailstones[2], hailstones[3], bounds))
        assert(!isRelevantXYIntersection(hailstones[2], hailstones[4], bounds))

        assert(!isRelevantXYIntersection(hailstones[3], hailstones[4], bounds))
    }

    @Test
    fun `countIntersections should return the number of hailstones that intersect in the future, given a bounds`() {
        val hailstones = sampleInput.map { Hailstone(it) }
        val bounds = LongBounds(7..27, 7..27)

        assert(countIntersections(hailstones, bounds) == 2L)
    }

    @Test
    fun `twoElementCombinations should return every 2 element combination as a pair`() {
        val list = listOf("a", "b", "c", "d", "e")
        val expectedResult = setOf(
            "a" to "b",
            "a" to "c",
            "a" to "d",
            "a" to "e",
            "b" to "c",
            "b" to "d",
            "b" to "e",
            "c" to "d",
            "c" to "e",
            "d" to "e",
        )

        assert( list.twoElementCombinations().toSet() == expectedResult)
    }

    @Test
    fun `check for duplicate velocity components in test and production  data`() {
        println("Duplicate velocity components in test data:")
        countDuplicateDeltaComponents(sampleInput.map { Hailstone(it) })

        println()
        println()

        println("Duplicate velocity components in production data:")
        countDuplicateDeltaComponents(readInput("Day24").map { Hailstone(it) })
    }

    @Test
    fun `find possible part 2 rock velocity components for test data`() {
        val hailstones = sampleInput.map { Hailstone(it) }
        println("Sample data velocity guesses:")

        val potentialXComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaX }, { it.point.x })
        println("\tPotential X velocity components for rock: $potentialXComponentValues")

        val potentialYComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaY }, { it.point.y })
        println("\tPotential Y velocity components for rock: $potentialYComponentValues")

        val potentialZComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaZ }, { it.point.z })
        println("\tPotential Z velocity components for rock: $potentialZComponentValues")

        val numberOfCombinations = potentialXComponentValues.size * potentialYComponentValues.size * potentialZComponentValues.size
        println("$numberOfCombinations potential possibilities for rock volocity")
    }

    @Test
    fun `find possible part 2 rock velocity components for production data`() {
        val hailstones = readInput("Day24").map { Hailstone(it) }
        println("Production data velocity guesses:")

        val potentialXComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaX }, { it.point.x })
        println("\tPotential X velocity components for rock: $potentialXComponentValues")

        val potentialYComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaY }, { it.point.y })
        println("\tPotential Y velocity components for rock: $potentialYComponentValues")

        val potentialZComponentValues = findPotentialValuesForVelocityComponent(hailstones, { it.velocity.deltaZ }, { it.point.z })
        println("\tPotential Z velocity components for rock: $potentialZComponentValues")

        val numberOfCombinations = potentialXComponentValues.size * potentialYComponentValues.size * potentialZComponentValues.size
        println("$numberOfCombinations potential possibilities for rock volocity")
    }

    @Test
    fun `find possible rock velocities for test data and turn each one into a point`() {
        val hailstones = sampleInput.map { Hailstone(it) }

        val potentialVelocities = findPotentialRockVelocities(hailstones)
        println("${potentialVelocities.size} potential possibilities for rock velocity:")

        val potentialVelocitiesAndOrigins = potentialVelocities
            .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

        println("${potentialVelocitiesAndOrigins.size} possibilities for point and velocity:")
        potentialVelocitiesAndOrigins
            .forEach { (origin, velocity) ->
                println("\t$origin @ $velocity")
            }
    }

    @Test
    fun `find possible solutions for test data and try to verify them`() {
        val hailstones = sampleInput.map { Hailstone(it) }
        val potentialSolutions = findPotentialRockVelocities(hailstones)
            .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

        println("Considering ${potentialSolutions.size} potential solutions:")
        val solutions = potentialSolutions.filter { (position, velocity) ->
            print("\t: $position @ $velocity:")
            verifyPotentialPart2Solution(position, velocity, hailstones)
        }

        println()
        println("${solutions.size} solutions found")
    }

    @Test
    fun `find possible rock velocities for production data and turn each one into a point`() {
        val hailstones = readInput("Day24").map { Hailstone(it) }

        val potentialVelocities = findPotentialRockVelocities(hailstones)
        println("${potentialVelocities.size} potential possibilities for rock velocity:")

        val potentialVelocitiesAndOrigins = potentialVelocities
            .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

        println("${potentialVelocitiesAndOrigins.size} possibilities for point and velocity:")
        potentialVelocitiesAndOrigins
            .forEach { (origin, velocity) ->
                println("\t$origin @ $velocity")
            }
    }

    companion object {
        fun truncate3(double: Double) = truncate(double * 1000) / 1000.0
    }

    private fun countDuplicateDeltaComponents(hailstones: Collection<Hailstone>) : Triple<Int, Int, Int> {
        val byDeltaX = hailstones.groupBy { it.velocity.deltaX }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.first()
        val byDeltaY = hailstones.groupBy { it.velocity.deltaY }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.first()
        val byDeltaZ = hailstones.groupBy { it.velocity.deltaZ }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.first()
        println("\tFound ${byDeltaX.value} entries with a deltaX of ${byDeltaX.key}")
        println("\tFound ${byDeltaY.value} entries with a deltaY of ${byDeltaY.key}")
        println("\tFound ${byDeltaZ.value} entries with a deltaZ of ${byDeltaZ.key}")

        return Triple(byDeltaX.key, byDeltaX.key, byDeltaZ.key)
    }

    private fun verifyPotentialPart2Solution(rockPosition: LongPoint3D, rockVelocity: Velocity3D, hailstones: Collection<Hailstone>): Boolean {

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

        val matchCount = hailstones.count { verify(it) }
        println("\t$matchCount out of ${hailstones.size} hailstones hit by rock")
        return matchCount == hailstones.size
    }

    private fun findPotentialRockOrigin(potentialRockVelocity: Velocity3D, hailstones: Collection<Hailstone>) : LongPoint3D? {
        //any two hailstones will do
        val hailstoneA = hailstones.first()
        val hailstoneB = hailstones.last()

        // each of these is the line of potential starting points that can hit this hailstone with the given velocity
        val hailstoneAPrime = hailstoneA.copy(velocity = hailstoneA.velocity - potentialRockVelocity)
        val hailstoneBPrime = hailstoneB.copy(velocity = hailstoneB.velocity - potentialRockVelocity)

        // the intersection of these two lines is the potential starting point
        return hailstoneAPrime.findIntersection3D(hailstoneBPrime)?.first
    }

    private fun findPotentialRockVelocities(hailstones: Collection<Hailstone>) : Set<Velocity3D> {
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

    private fun findPotentialValuesForVelocityComponent(
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

        val positiveOrNegativeOne = listOf(1L, -1L)
        val plusOrMinusDeltaValue = listOf(deltaValue.toLong(), deltaValue * -1L)

        // find the potential delta values that will work for these 2 hailstones in this velocity coordinate
        // the rock has to get from p1 to p2 in exactly an integer number of turns
        // it could be going backwards, and the velocity of the hailstones could be moving towards the rock
        // or away from it.
        fun findPotentialDeltaValues(hailstonePair: Pair<Hailstone, Hailstone>): Set<Long> {
            val (h1, h2) = hailstonePair
            val p1 = positionComponentSelector(h1)
            val p2 = positionComponentSelector(h2)
            val factors = abs(p1 - p2).findFactors() //the rock has to get from p1 to p2 in exactly an integer number of turns
            return factors
                .flatMap { factor -> positiveOrNegativeOne.map { it * factor } } // times plus or minus one
                .flatMap { value -> plusOrMinusDeltaValue.map { it + value } } // plus or minus this hailstone's velocity component
                .toSet()
        }

        //the values have to work for each potential combination of 2 hailstones — throw out anything that doesn't work with a different pair
        val hailstoneCombinations = duplicateHailstones.twoElementCombinations().toList()
        val possibleValues = findPotentialDeltaValues(hailstoneCombinations.first()).toMutableSet()
        hailstoneCombinations
            .drop(1)
            .forEach { possibleValues.retainAll(findPotentialDeltaValues(it)) }

        return possibleValues
    }
}