package day10

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day10SolutionTest {
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

    private val solution = Day10Solution()

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
        assert(solution.part1(sampleInputWithExtraPipes) == 4L)
        assert(solution.part1(moreComplexLoopWithExtraPipes) == 8L)
    }

    @Test
    fun `part2 should calculate the number of tiles enclosed by the loop`() {
        val loop = """
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
        """.trimIndent().lines()

        assert(solution.part2(loop) == 4L)
    }

    @Test
    fun `part2 should calculate the number of tiles enclosed by the loop, and squeezing between pipes counts as a path to outside`() {
        val loop = """
            ..........
            .S------7.
            .|F----7|.
            .||....||.
            .||....||.
            .|L-7F-J|.
            .|..||..|.
            .L--JL--J.
            ..........
        """.trimIndent().lines()

        assert(solution.part2(loop) == 4L)
    }

    @Test
    fun `part2 should calculate the number of tiles enclosed by the loop given a larger loop`() {
        val loop = """
            .F----7F7F7F7F-7....
            .|F--7||||||||FJ....
            .||.FJ||||||||L7....
            FJL7L7LJLJ||LJ.L-7..
            L--J.L7...LJS7F-7L7.
            ....F-J..F7FJ|L7L7L7
            ....L7.F7||L7|.L7L7|
            .....|FJLJ|FJ|F7|.LJ
            ....FJL-7.||.||||...
            ....L---J.LJ.LJLJ...
        """.trimIndent().lines()

        assert(solution.part2(loop) == 8L)
    }

    @Test
    fun `part2 should treat pipe segments that aren't part of the main loop as ground tiles for the purposes of being enclosed by the loop`() {
        val loop = """
            FF7FSF7F7F7F7F7F---7
            L|LJ||||||||||||F--J
            FL-7LJLJ||||||LJL-77
            F--JF--7||LJLJ7F7FJ-
            L---JF-JLJ.||-FJLJJ7
            |F|F-JF---7F7-L7L|7|
            |FFJF7L7F-JF7|JL---7
            7-L-JL7||F7|L7F-7F7|
            L.L7LFJ|||||FJL7||LJ
            L7JLJL-JLJLJL--JLJ.L
        """.trimIndent().lines()

        assert(solution.part2(loop) == 10L)
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