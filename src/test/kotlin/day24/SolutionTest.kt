package day24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.LongBounds
import utils.twoElementCombinations
import kotlin.math.truncate

class SolutionTest {
    private val sampleInput = """
        19, 13, 30 @ -2,  1, -2
        18, 19, 22 @ -1, -1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @  1, -5, -3
    """.trimIndent().lines()

    private val solution = Solution()

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
            val hailstoneB = Hailstone("18, 19, 24 @ -1, -1, -2")

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
    fun `assert that duplicate velocity components for each axis exist in test and production  data`() {
        countDuplicateDeltaComponents(sampleInput.map { Hailstone(it) })
        countDuplicateDeltaComponents(solution.readInput().map { Hailstone(it) })
    }

    @Test
    fun `findPotentialRockVelocities should return a set of potential velocities for a rock that can hit every hailstone`() {
        val hailstones = sampleInput.map { Hailstone(it) }

        val potentialVelocities = findPotentialRockVelocities(hailstones)

        assert(potentialVelocities.isNotEmpty())
        assert(Velocity3D(-3, 1, 2) in potentialVelocities)
    }

    @Test
    fun `findPotentialRockOrigin should derive a rock position from a potential velocity, if one exists`() {
        val hailstones = sampleInput.map { Hailstone(it) }

        val potentialVelocities = findPotentialRockVelocities(hailstones)

        val potentialVelocitiesAndOrigins = potentialVelocities
            .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

        assert(potentialVelocitiesAndOrigins.isNotEmpty())
        val expectedRockPosition = potentialVelocitiesAndOrigins.first { it.second == Velocity3D(-3, 1, 2) }
        assert(expectedRockPosition.first == LongPoint3D(24, 13, 10))
    }

    @Test
    fun `verifyPotentialPart2Solution should verify potential solutions`() {
        val hailstones = sampleInput.map { Hailstone(it) }
        val potentialSolutions = findPotentialRockVelocities(hailstones)
            .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

        val solutions = potentialSolutions.filter { (position, velocity) ->
            verifyPotentialPart2Solution(position, velocity, hailstones)
        }

        assert(solutions.size == 1)
        assert(solutions.first().first == LongPoint3D(24, 13, 10))
        assert(solutions.first().second == Velocity3D(-3, 1, 2))
    }

    @Test
    fun `find possible solutions for production data and try to verify them`() {
        val hailstones = solution.readInput().map { Hailstone(it) }
        val potentialSolutions = findPotentialRockVelocities(hailstones)
            .mapNotNull { findPotentialRockOrigin(it, hailstones)?.to(it) }

        println("Considering ${potentialSolutions.size} potential solutions:")
        val solutions = potentialSolutions.filter { (position, velocity) ->
            print("\t: $position @ $velocity:")
            verifyPotentialPart2Solution(position, velocity, hailstones)
        }

        println()
        println("${solutions.size} solutions found")

        val answer = solutions.first()
        println ("Answer is ${answer.first.x + answer.first.y + answer.first.z}")
    }

    @Test
    fun `findRockThatHitsAllHailstones should find the position and velocity of a rock that will hit all hailstones`() {
        val hailstones = sampleInput.map { Hailstone(it) }

        val (position, velocity) = findRockThatHitsAllHailstones(hailstones)

        assert(position == LongPoint3D(24, 13, 10))
        assert(velocity == Velocity3D(-3, 1, 2))
    }

    @Test
    fun `findRockThatHitsAllHailstones should return an answer for production data`() {
        val hailstones = solution.readInput().map { Hailstone(it) }

        findRockThatHitsAllHailstones(hailstones)
    }

    @Test
    fun `part2 should find position of the rock that hits every hailstone, and return the sum of its x, y, and z axes`() {
        assert(solution.part2(sampleInput) == 24L + 13L + 10L)
    }

    companion object {
        fun truncate3(double: Double) = truncate(double * 1000) / 1000.0
    }

    private fun countDuplicateDeltaComponents(hailstones: Collection<Hailstone>) {
        val byDeltaX = hailstones.groupBy { it.velocity.deltaX }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.first()
        val byDeltaY = hailstones.groupBy { it.velocity.deltaY }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.first()
        val byDeltaZ = hailstones.groupBy { it.velocity.deltaZ }.mapValues { it.value.size }.entries.sortedByDescending { it.value }.first()
        assert(byDeltaX.value > 1)
        assert(byDeltaY.value > 1)
        assert(byDeltaZ.value > 1)
    }
}