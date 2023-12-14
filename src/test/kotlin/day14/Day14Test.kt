package day14

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day14Test {
    private val sampleInput = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent().lines()

    private val sampleInputAfterTiltNorth = """
        OOOO.#.O..
        OO..#....#
        OO..O##..O
        O..#.OO...
        ........#.
        ..#....#.#
        ..O..#.O.O
        ..O.......
        #....###..
        #....#....
    """.trimIndent().lines()

    @Test
    fun `part1 should parse a platform, tilt it north, and return the platform's total Load`() {
        assert(part1(sampleInput) == 136L)
    }

    @Test
    fun `part2 should parse a platform, spin it 1 billion times, and return the platform's total load`() {
        assert(part2(sampleInput) == 64L)
    }
    @Test
    fun `parse should produce a platform instance with x and y coordinate bounds`() {
        val platform: Platform = parse(sampleInput)

        assert(platform.xCoords == 0..9)
        assert(platform.yCoords == 0..9)
    }

    @Test
    fun `parse should produce a platform instance with valid starting ball locations`() {
        val platform: Platform = parse(sampleInput)

        val ballXCoordsByRow = platform.balls.groupBy({ it.y }, {it.x})

        assert(platform.balls.size == 18)
        assert(ballXCoordsByRow[0] == listOf(0))
        assert(ballXCoordsByRow[1] == listOf(0, 2, 3))
        assert(ballXCoordsByRow[2] == null)
        assert(ballXCoordsByRow[3] == listOf(0, 1, 4, 9))
        assert(ballXCoordsByRow[4] == listOf(1, 7))
        assert(ballXCoordsByRow[5] == listOf(0, 5))
        assert(ballXCoordsByRow[6] == listOf(2, 6, 9))
        assert(ballXCoordsByRow[7] == listOf(7))
        assert(ballXCoordsByRow[8] == null)
        assert(ballXCoordsByRow[9] == listOf(1, 2))
    }

    @Test
    fun `parse should produce a platform instance with valid brick locations by column`() {
        val platform: Platform = parse(sampleInput)

        val totalBricks = platform.brickLocationsByColumn.values.sumOf { it.size }
        assert(totalBricks == 17)

        assert(platform.brickLocationsByColumn[0] == setOf(8, 9))
        assert(platform.brickLocationsByColumn[1] == null)
        assert(platform.brickLocationsByColumn[2] == setOf(5))
        assert(platform.brickLocationsByColumn[3] == setOf(3))
        assert(platform.brickLocationsByColumn[4] == setOf(1))
        assert(platform.brickLocationsByColumn[5] == setOf(0, 2, 6, 8, 9))
        assert(platform.brickLocationsByColumn[6] == setOf(2, 8))
        assert(platform.brickLocationsByColumn[7] == setOf(5, 8))
        assert(platform.brickLocationsByColumn[8] == setOf(4))
        assert(platform.brickLocationsByColumn[9] == setOf(1, 5))
    }
    
    @Nested
    inner class PlatformTest {
        @Test
        fun `tiltNorth should move every ball north until it runs into the north edge, a brick, or another ball`() {
            val platform: Platform = parse(sampleInput)
            platform.tiltNorth()

            val expectedBallYCoordinatesByXCoordinate = parse(sampleInputAfterTiltNorth).balls.groupBy({it.x}, {it.y})
            val actualBallYCoordinatesByXCoordinate = platform.balls.groupBy({ it.x }, { it.y })

            platform.xCoords.forEach { x ->
                assert(actualBallYCoordinatesByXCoordinate[x] == expectedBallYCoordinatesByXCoordinate[x])
            }
        }

        @Test
        fun `tiltWest should move every ball west until it runs into the west edge, a brick, or another ball`() {
            val expectedResult = """
                O....#....
                OOO.#....#
                .....##...
                OO.#OO....
                OO......#.
                O.#O...#.#
                O....#OO..
                O.........
                #....###..
                #OO..#....
            """.trimIndent().lines()

            val platform: Platform = parse(sampleInput)
            platform.tiltWest()

            assert(platform.balls == parse(expectedResult).balls)
        }

        @Test
        fun `tiltSouth should move every ball south until it runs into the south edge, a brick, or another ball`() {
            val expectedResult = """
                .....#....
                ....#....#
                ...O.##...
                ...#......
                O.O....O#O
                O.#..O.#.#
                O....#....
                OO....OO..
                #OO..###..
                #OO.O#...O
            """.trimIndent().lines()

            val platform: Platform = parse(sampleInput)
            platform.tiltSouth()

            assert(platform.balls == parse(expectedResult).balls)
        }

        @Test
        fun `tiltEast should move every ball east until it runs into the east edge, a brick, or another ball`() {
            val expectedResult = """
                ....O#....
                .OOO#....#
                .....##...
                .OO#....OO
                ......OO#.
                .O#...O#.#
                ....O#..OO
                .........O
                #....###..
                #..OO#....
            """.trimIndent().lines()

            val platform: Platform = parse(sampleInput)
            platform.tiltEast()

            assert(platform.balls == parse(expectedResult).balls)
        }

        @Test
        fun `totalLoad should calculate each ball's distance from the south edge and return the sum`() {
            val platform = parse(sampleInputAfterTiltNorth)

            assert(platform.totalLoad() == 136)
        }
        
        @Test
        fun `spinCycle should move the platform north, west, south, and east once each for a single cycle`() {
            val expectedPositionsAfter1Cycle = """
                .....#....
                ....#...O#
                ...OO##...
                .OO#......
                .....OOO#.
                .O#...O#.#
                ....O#....
                ......OOOO
                #...O###..
                #..OO#....
            """.trimIndent().lines()

            val platform = parse(sampleInput)
            platform.spinCycle(1)

            assert(platform.balls == parse(expectedPositionsAfter1Cycle).balls)
        }

        @Test
        fun `spinCycle should move the balls into the expected position for 2 cycles`() {
            val expectedPositionsAfter2Cycles = """
                .....#....
                ....#...O#
                .....##...
                ..O#......
                .....OOO#.
                .O#...O#.#
                ....O#...O
                .......OOO
                #..OO###..
                #.OOO#...O
            """.trimIndent().lines()

            val platform = parse(sampleInput)
            platform.spinCycle(2)

            assert(platform.balls == parse(expectedPositionsAfter2Cycles).balls)
        }

        @Test
        fun `spinCycle should move the balls into the expected position for 3 cycles`() {
            val expectedPositionsAfter3Cycles = """
                .....#....
                ....#...O#
                .....##...
                ..O#......
                .....OOO#.
                .O#...O#.#
                ....O#...O
                .......OOO
                #...O###.O
                #.OOO#...O
            """.trimIndent().lines()

            val platform = parse(sampleInput)
            platform.spinCycle(3)

            assert(platform.balls == parse(expectedPositionsAfter3Cycles).balls)
        }

        @Test
        fun `spinCycle should be able to return results for very large numbers of spins`() {
            val platform = parse(sampleInput)
            val oneBillion = 1000000000
            platform.spinCycle(oneBillion)

            assert(platform.totalLoad() == 64)
        }
    }
}