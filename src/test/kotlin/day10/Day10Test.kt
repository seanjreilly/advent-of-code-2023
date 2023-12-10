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
    fun `findLoop should return the points in the loop given a simple loop`() {
        val pointsInLoop: Set<Point> = findLoop(sampleInput)
        assert(pointsInLoop.size == 8)
        assert(Point(1,1) in pointsInLoop)
        assert(Point(1,2) in pointsInLoop)
        assert(Point(1,3) in pointsInLoop)
        assert(Point(2,3) in pointsInLoop)
        assert(Point(3,3) in pointsInLoop)
        assert(Point(3,2) in pointsInLoop)
        assert(Point(3,1) in pointsInLoop)
        assert(Point(2,1) in pointsInLoop)
    }

    @Test
    fun `findLoop should return the points in the loop given a more complicated loop`() {
        val pointsInLoop: Set<Point> = findLoop(moreComplexLoop)
        assert(pointsInLoop.size == 16)
        assert(Point(0, 2) in pointsInLoop)
        assert(Point(0, 3) in pointsInLoop)
        assert(Point(0, 4) in pointsInLoop)
        assert(Point(1, 4) in pointsInLoop)
        assert(Point(1, 3) in pointsInLoop)
        assert(Point(2, 3) in pointsInLoop)
        assert(Point(3, 3) in pointsInLoop)
        assert(Point(4, 3) in pointsInLoop)
        assert(Point(4, 2) in pointsInLoop)
        assert(Point(3, 2) in pointsInLoop)
        assert(Point(3, 1) in pointsInLoop)
        assert(Point(3, 0) in pointsInLoop)
        assert(Point(2, 0) in pointsInLoop)
        assert(Point(2, 1) in pointsInLoop)
        assert(Point(1, 1) in pointsInLoop)
        assert(Point(1, 2) in pointsInLoop)
    }

    @Test
    fun `findLoop should not be confused by extra pipe segments that aren't in the loop`() {
        assert(findLoop(sampleInputWithExtraPipes) == findLoop(sampleInput))
        assert(findLoop(moreComplexLoopWithExtraPipes) == findLoop(moreComplexLoop))
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