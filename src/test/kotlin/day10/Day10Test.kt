package day10

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day10Test {
    private val sampleInput = """
        .....
        .S-7.
        .|.|.
        .L-J.
        .....
    """.trimIndent().lines()

    private val sampleInputWithExtraPipes = """
        -L|F7
        7S-7|
        L|7||
        -L-J|
        L|-JF
    """.trimIndent().lines()

    private val moreComplexLoop = """
        ..F7.
        .FJ|.
        SJ.L7
        |F--J
        LJ...
    """.trimIndent().lines()

    private val moreComplexLoopWithExtraPipes = """
        7-F7-
        .FJ|7
        SJLL7
        |F--J
        LJ.LJ
    """.trimIndent().lines()
    
    @Test
    fun `findS should return the point containing the S symbol`() {
        assert(findS(sampleInput) == Point(1, 1))
        assert(findS(sampleInputWithExtraPipes) == Point(1, 1))
        assert(findS(moreComplexLoop) == Point(0, 2))
        assert(findS(moreComplexLoopWithExtraPipes) == Point(0, 2))
    }

    @Test
    fun `findLoopLength should return the length of the loop from S to S`() {
        assert(findLoopLength(sampleInput) == 8)
        assert(findLoopLength(moreComplexLoop) == 16)
    }

    @Test
    fun `findLoopLength should not be confused by extra pipe segments around the S`() {
        assert(findLoopLength(sampleInputWithExtraPipes) == 8)
        assert(findLoopLength(moreComplexLoopWithExtraPipes) == 16)
    }

    @Test
    fun `part1 should return the length of the loop divided by 2`() {
        assert(part1(sampleInputWithExtraPipes) == 4L)
        assert(part1(moreComplexLoopWithExtraPipes) == 8L)
    }

    @Nested
    inner class PointTest {
        @Test
        fun `isWithinBounds should return true iff the point is within the bounds of the input`() {
            assert(Point(0,0).isWithinBounds(sampleInput))
            assert(Point(2,2).isWithinBounds(sampleInput))
            assert(Point(4,4).isWithinBounds(sampleInput))
            assert(!Point(5,4).isWithinBounds(sampleInput))
            assert(!Point(3,5).isWithinBounds(sampleInput))
            assert(!Point(-1,2).isWithinBounds(sampleInput))
            assert(!Point(2,-1).isWithinBounds(sampleInput))
        }
    }
}