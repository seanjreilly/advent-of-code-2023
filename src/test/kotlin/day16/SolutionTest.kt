package day16

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.CardinalDirection.*
import utils.Point
import utils.PointAndDirection

class SolutionTest {
    private val sampleInput = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent().lines()

    private val solution = Solution()

    @Test
    fun `part1 should construct a contraption, energize the tiles, and return the number of energized tiles`() {
        assert(solution.part1(sampleInput) == 46L)
    }

    @Test
    fun `part2 should find the initial beam configuration that energizes the most tiles and return that number`() {
        assert(solution.part2(sampleInput) == 51L)
    }
    
    @Nested
    inner class ContraptionTest {
        @Test
        fun `constructor should return a valid Contraption with appropriate bounds and tile data given input`() {
            val contraption = Contraption(sampleInput)

            assert(contraption.validXCoords == 0..9)
            assert(contraption.validYCoords == 0..9)
            assert(contraption.tiles.map { it.joinToString("") }.toList() == sampleInput)
        }

        @Test
        fun `energizeTiles should follow a beam of light through the contraption and return the tiles it energizes`() {
            val rawExpectedEnergizedTiles = """
                ######....
                .#...#....
                .#...#####
                .#...##...
                .#...##...
                .#...##...
                .#..####..
                ########..
                .#######..
                .#...#.#..
            """.trimIndent().lines()

            val expectedEnergizedTiles = rawExpectedEnergizedTiles
                .flatMapIndexed { y, line ->
                    line.mapIndexed { x, char -> Point(x,y) to char }
                }
                .filter { it.second == '#' }
                .map { it.first }
                .toSet()

            val energizedTiles = Contraption(sampleInput).energizeTiles()
            assert(energizedTiles == expectedEnergizedTiles)
        }

        @Test
        fun `startingConfigurations should include every point on the north edge heading south`() {
            val entries: Set<PointAndDirection> = Contraption(sampleInput).startingConfigurations()

            val entriesOnNorthEdge = entries.filter { it.direction == South }
            assert(entriesOnNorthEdge.size == 10)
            assert(Point(0,0) facing South in entriesOnNorthEdge)
            assert(Point(1,0) facing South in entriesOnNorthEdge)
            assert(Point(2,0) facing South in entriesOnNorthEdge)
            assert(Point(3,0) facing South in entriesOnNorthEdge)
            assert(Point(4,0) facing South in entriesOnNorthEdge)
            assert(Point(5,0) facing South in entriesOnNorthEdge)
            assert(Point(6,0) facing South in entriesOnNorthEdge)
            assert(Point(7,0) facing South in entriesOnNorthEdge)
            assert(Point(8,0) facing South in entriesOnNorthEdge)
            assert(Point(9,0) facing South in entriesOnNorthEdge)
        }

        @Test
        fun `startingConfigurations should include every point on the east edge heading west`() {
            val entries: Set<PointAndDirection> = Contraption(sampleInput).startingConfigurations()

            val entriesOnEastEdge = entries.filter { it.direction == West }
            assert(entriesOnEastEdge.size == 10)
            assert(Point(9, 0) facing West in entriesOnEastEdge)
            assert(Point(9, 1) facing West in entriesOnEastEdge)
            assert(Point(9, 2) facing West in entriesOnEastEdge)
            assert(Point(9, 3) facing West in entriesOnEastEdge)
            assert(Point(9, 4) facing West in entriesOnEastEdge)
            assert(Point(9, 5) facing West in entriesOnEastEdge)
            assert(Point(9, 6) facing West in entriesOnEastEdge)
            assert(Point(9, 7) facing West in entriesOnEastEdge)
            assert(Point(9, 8) facing West in entriesOnEastEdge)
            assert(Point(9, 9) facing West in entriesOnEastEdge)
        }

        @Test
        fun `startingConfigurations should include every point on the south edge heading north`() {
            val entries: Set<PointAndDirection> = Contraption(sampleInput).startingConfigurations()

            val entriesOnSouthEdge = entries.filter { it.direction == North }
            assert(entriesOnSouthEdge.size == 10)
            assert(Point(0, 9) facing North in entriesOnSouthEdge)
            assert(Point(1, 9) facing North in entriesOnSouthEdge)
            assert(Point(2, 9) facing North in entriesOnSouthEdge)
            assert(Point(3, 9) facing North in entriesOnSouthEdge)
            assert(Point(4, 9) facing North in entriesOnSouthEdge)
            assert(Point(5, 9) facing North in entriesOnSouthEdge)
            assert(Point(6, 9) facing North in entriesOnSouthEdge)
            assert(Point(7, 9) facing North in entriesOnSouthEdge)
            assert(Point(8, 9) facing North in entriesOnSouthEdge)
            assert(Point(9, 9) facing North in entriesOnSouthEdge)
        }

        @Test
        fun `startingConfigurations should include every point on the west edge heading east`() {
            val entries: Set<PointAndDirection> = Contraption(sampleInput).startingConfigurations()

            val entriesOnWestEdge = entries.filter { it.direction == East }
            assert(entriesOnWestEdge.size == 10)
            assert(Point(0, 0) facing East in entriesOnWestEdge)
            assert(Point(0, 1) facing East in entriesOnWestEdge)
            assert(Point(0, 2) facing East in entriesOnWestEdge)
            assert(Point(0, 3) facing East in entriesOnWestEdge)
            assert(Point(0, 4) facing East in entriesOnWestEdge)
            assert(Point(0, 5) facing East in entriesOnWestEdge)
            assert(Point(0, 6) facing East in entriesOnWestEdge)
            assert(Point(0, 7) facing East in entriesOnWestEdge)
            assert(Point(0, 8) facing East in entriesOnWestEdge)
            assert(Point(0, 9) facing East in entriesOnWestEdge)
        }
    }
}