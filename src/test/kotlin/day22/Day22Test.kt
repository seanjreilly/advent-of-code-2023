package day22

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

class Day22Test {
    private val sampleInput = """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent().lines()

    @Test
    fun `part1 should parse a collection of bricks, let them all fall, and return the count of bricks that can be safely disintegrated`() {
        assert(part1(sampleInput) == 5L)
    }

    @Test
    fun `part2 should parse a collection of bricks, let them all fall, count the number of bricks that would fall for each brick that was disintegrated, and return the total amount`() {
        assert(part2(sampleInput) == 7L)
    }
    
    @Nested
    inner class BrickTest {
        @Test
        fun `brick should contain all points from startPoint to endPoint, given a brick pointed in the x dimension`() {
            val firstPoint = Point3D(1, 2, 3)
            val lastPoint = Point3D(5, 2, 3)
            val brick: Iterable<Point3D> = Brick(firstPoint, lastPoint)

            assert(brick.count() == 5)
            assert(firstPoint in brick)
            assert(Point3D(2, 2, 3) in brick)
            assert(Point3D(3, 2, 3) in brick)
            assert(Point3D(4, 2, 3) in brick)
            assert(lastPoint in brick)
        }

        @Test
        fun `brick should contain all points in the brick, given a brick pointed in the y dimension`() {
            val firstPoint = Point3D(1, 2, 3)
            val lastPoint = Point3D(1, 6, 3)
            val brick = Brick(firstPoint, lastPoint)

            assert(brick.count() == 5)
            assert(firstPoint in brick)
            assert(Point3D(1, 3, 3) in brick)
            assert(Point3D(1, 4, 3) in brick)
            assert(Point3D(1, 5, 3) in brick)
            assert(lastPoint in brick)
        }

        @Test
        fun `brick should contain all points in the brick, given a brick pointed in the z dimension`() {
            val firstPoint = Point3D(1, 2, 3)
            val lastPoint = Point3D(1, 2, 7)
            val brick = Brick(firstPoint, lastPoint)

            assert(brick.count() == 5)
            assert(firstPoint in brick)
            assert(Point3D(1, 2, 4) in brick)
            assert(Point3D(1, 2, 5) in brick)
            assert(Point3D(1, 2, 6) in brick)
            assert(brick.lastPoint in brick)
        }

        @Test
        fun `brick should contain all points in the brick, given a single point brick`() {
            val brick = Brick(Point3D(9, 9, 9), Point3D(9, 9, 9))

            assert(brick.count() == 1)
            assert(brick.firstPoint in brick)
        }

        @Test
        fun `minZ should return the minimum z value out of every point in the brick`() {
            val brickA = Brick(Point3D(1, 2, 3), Point3D(1, 2, 7)) //vertical brick
            val brickB = Brick(Point3D(1, 2, 3), Point3D(19, 2, 3)) //x-horizontal brick
            val brickC = Brick(Point3D(1, 2, 5), Point3D(1, 20, 5)) //y-horizontal brick

            assert(brickA.minZ == 3)
            assert(brickB.minZ == 3)
            assert(brickC.minZ == 5)
        }

        @Test
        fun `bottomPoints should return all points in the brick where z == minZ`() {
            val brickA = Brick(Point3D(1, 2, 3), Point3D(1, 2, 5)) //vertical brick
            val brickB = Brick(Point3D(1, 2, 3), Point3D(3, 2, 3)) //x-horizontal brick
            val brickC = Brick(Point3D(1, 2, 5), Point3D(1, 4, 5)) //y-horizontal brick

            assert(brickA.bottomPoints == setOf(brickA.firstPoint))
            assert(brickB.bottomPoints == brickB.toSet())
            assert(brickC.bottomPoints == brickC.toSet())
        }

        @Test
        fun `aboveGround should return true iff minZ is greater than 1`() {
            val bricks = sampleInput.map { parseBrick(it) }

            bricks.drop(1).forEach { brick ->
                assert(brick.aboveGround)
            }
        }

        @Test
        fun `aboveGround should return false if minZ is 1`() {
            val brick = parseBrick(sampleInput.first())

            assert(!brick.aboveGround)
        }

        @Test
        fun `moveDown should return a new block with every point moved -1 on the z axis, iff the block is above ground`() {
            val brick = parseBrick("1,1,8~1,1,18")

            val result = brick.moveDown()
            assert(result == parseBrick("1,1,7~1,1,17"))
        }

        @Test
        fun `moveDown should throw an exception iff the block is on the ground`() {
            val brick = parseBrick(sampleInput.first())

            val exception = assertThrows<IllegalStateException> { brick.moveDown() }
            assert(exception.message == "brick is on the ground and cannot move down")
        }
    }

    @Nested
    inner class Point3DTest {
        @Test
        fun `constructor should return a Point3D given a comma delimited list`() {
            val point = Point3D("1,2,3")
            assert(point == Point3D(1, 2, 3))
        }

        @Test
        fun `down() should return the point translated by -1 in the Z axis`() {
            val point = Point3D(9, 9, 9)
            assert(point.down() == Point3D(9, 9, 8))
        }
    }

    @Test
    fun `parseBrick should return a brick`() {
        val brick = parseBrick(sampleInput.first())

        assert(brick.firstPoint == Point3D(1, 0, 1))
        assert(brick.lastPoint == Point3D(1, 2, 1))
    }

    @Test
    fun `moveAllBricksDown should allow all bricks to fall as far as they can`() {
        val bricks = sampleInput.map { parseBrick(it) }

        val fallenBricks = bricks.moveAllDown()

        val expectedResult = listOf(
            parseBrick("1,0,1~1,2,1"),
            parseBrick("0,0,2~2,0,2"),
            parseBrick("0,2,2~2,2,2"),
            parseBrick("0,0,3~0,2,3"),
            parseBrick("2,0,3~2,2,3"),
            parseBrick("0,1,4~2,1,4"),
            parseBrick("1,1,5~1,1,6")
        )

        assert(fallenBricks.size == expectedResult.size)
        expectedResult.indices.forEach { assert(expectedResult[it] in fallenBricks) }
    }

    @Test
    fun `findSafeBricksToDisintegrate should return bricks that don't support any other bricks`() {
        val bricks = sampleInput.map { parseBrick(it) }.moveAllDown()

        val result: Set<Brick> = bricks.findSafeBricksToDisintegrate()

        val expectedResult = listOf(
            parseBrick("0,0,2~2,0,2"),
            parseBrick("0,2,2~2,2,2"),
            parseBrick("0,0,3~0,2,3"),
            parseBrick("2,0,3~2,2,3"),
            parseBrick("1,1,5~1,1,6")
        )

        assert(result.size == 5)
        expectedResult.indices.forEach { assert(expectedResult[it] in result) }
    }

    @Test
    fun `findChainReactionCounts should count the total number of blocks that would fall if each block was disintegrated`() {
        val bricks = sampleInput.map { parseBrick(it) }.moveAllDown()

        val result = bricks.findChainReactionCounts()

        val expectedResult = mapOf(
            parseBrick("1,0,1~1,2,1") to 6,
            parseBrick("0,0,2~2,0,2") to 0,
            parseBrick("0,2,2~2,2,2") to 0,
            parseBrick("0,0,3~0,2,3") to 0,
            parseBrick("2,0,3~2,2,3") to 0,
            parseBrick("0,1,4~2,1,4") to 1,
            parseBrick("1,1,5~1,1,6") to 0
        )

        assert(result.size == expectedResult.size)
        expectedResult.entries.forEach {
            assert(result[it.key] == it.value)
        }
    }

    @Test
    fun `findChainReactionCounts should return the correct results when a brick falls into the same position as another brick`() {
        val rawBricks = """
            1,0,1~1,2,1
            0,0,2~2,0,2
            0,0,3~2,0,3
        """.trimIndent().lines()
        //the third brick should fall into the second brick's exact position when the first brick is disintegrated

        val bricks = rawBricks.map { parseBrick(it) }

        val result = bricks.findChainReactionCounts()
        assert(result[bricks.first()] == 2)
    }
}