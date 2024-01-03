package day24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.LongBounds
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
        fun `findIntersectionXY should return null given another Hailstone that does not intersect with this one`() {
            val hailstoneA = Hailstone(sampleInput[1])
            val hailstoneB = Hailstone(sampleInput[2])

            val result = hailstoneA.findIntersectionXY(hailstoneB)

            assert(result == null)
        }

        @Test
        fun `findIntersectionXY should return the intersection point given another Hailstone that intersects with this one`() {
            val hailstoneA = Hailstone(sampleInput[0])
            val hailstoneB = Hailstone(sampleInput[1])

            val result = hailstoneA.findIntersectionXY(hailstoneB)!!

            assert(truncate3(result.x) == 14.333)
            assert(truncate3(result.y) == 15.333)
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

    companion object {
        fun truncate3(double: Double) = truncate(double * 1000) / 1000.0
    }
}