package day21

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Bounds
import utils.Point
import utils.parseGridWithPoints

class Day21Test {
    private val sampleInput = """
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """.trimIndent().lines()

    @Test
    fun `part1 should construct a Garden instance and count the number of squares the elf can reach in 64 steps`() {
        assert(part1(sampleInput) == 42L)
    }
    
    @Nested
    inner class GardenTest {
        @Test
        fun `constructor should return a Garden instance with bounds, rocks, and start points given input`() {
            val input = """
            .#.
            .S.
            ##.
        """.trimIndent().lines()

            val garden = Garden(input)

            assert(garden.startPoint == Point(1,1))
            assert(garden.bounds == Bounds(0..2, 0..2))
            assert(garden.rocks == setOf(Point(1,0), Point(0,2), Point(1,2)))
        }
        
        @Test
        fun `findLegalDestinationSquares should return the squares the Elf can travel to in exactly 1 step, given 1`() {
            val expectedResult = setOf(Point(5, 4), Point(4, 5))
            val garden = Garden(sampleInput)

            val result = garden.findLegalDestinations(1)

            assert(result == expectedResult)
        }

        @Test
        fun `findDestinationSquares should return the squares the Elf can travel to in exactly 2 steps, given 2`() {
            val resultDiagram = """
                ...........
                .....###.#.
                .###.##..#.
                ..#.#O..#..
                ....#.#....
                .##O.O####.
                .##.O#...#.
                .......##..
                .##.#.####.
                .##..##.##.
                ...........
            """.trimIndent().lines()

            val expectedResult = parseGridWithPoints(resultDiagram).second.filter { it.second == 'O' }.map { it.first }.toSet()
            val garden = Garden(sampleInput)

            val result = garden.findLegalDestinations(2)

            assert(result == expectedResult)
        }

        @Test
        fun `findDestinationSquares should return the squares the Elf can travel 2 in exactly 6 steps, given 6`() {
            val resultDiagram = """
                ...........
                .....###.#.
                .###.##.O#.
                .O#O#O.O#..
                O.O.#.#.O..
                .##O.O####.
                .##.O#O..#.
                .O.O.O.##..
                .##.#.####.
                .##O.##.##.
                ...........
            """.trimIndent().lines()

            val expectedResult = parseGridWithPoints(resultDiagram).second.filter { it.second == 'O' }.map { it.first }.toSet()
            val garden = Garden(sampleInput)

            val result = garden.findLegalDestinations(6)

            assert(result == expectedResult)
        }

        @Test
        fun `countLegalDestinationsOnInfiniteGrid should return the number of squares the elf can visit in N steps`() {
            val garden = Garden(sampleInput)

            assert(garden.countLegalDestinationsOnInfiniteGrid(6) == 16L)
            assert(garden.countLegalDestinationsOnInfiniteGrid(10) == 50L)
            assert(garden.countLegalDestinationsOnInfiniteGrid(50) == 1594L)
            assert(garden.countLegalDestinationsOnInfiniteGrid(100) == 6536L)
        }

        @Test
        fun `neighboursMappingInfiniteGrid should return proper values outside of the garden bounds`() {
            val garden = Garden(sampleInput)

            assert(garden.neighboursMappingInfiniteGrid(Point(-3,-3)).isEmpty())
            assert(garden.neighboursMappingInfiniteGrid(Point(19,19)).isEmpty())
            assert(garden.neighboursMappingInfiniteGrid(Point(-8,-4)).size == 4)
        }


    }
}