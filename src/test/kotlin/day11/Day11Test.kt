package day11

import org.junit.jupiter.api.Test

class Day11Test {
    private val sampleInput = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent().lines()

    private val sampleInputAfterExpansion = """
        ....#........
        .........#...
        #............
        .............
        .............
        ........#....
        .#...........
        ............#
        .............
        .............
        .........#...
        #....#.......
    """.trimIndent().lines()
    
    @Test
    fun `findGalaxies should return the coordinates of the # characters `() {
        val result: Set<Galaxy> = findGalaxies(sampleInput)

        val expectedResult = setOf(
            Galaxy(3,0),
            Galaxy(7,1),
            Galaxy(0, 2),
            Galaxy(6,4),
            Galaxy(1,5),
            Galaxy(9, 6),
            Galaxy(7, 8),
            Galaxy(0, 9),
            Galaxy(4, 9),
        )

        assert(result.size == expectedResult.size)
        expectedResult.forEach { expectedGalaxy ->
            assert(expectedGalaxy in result)
        }
    }

    @Test
    fun `expandUniverse should double every blank row and blank column`() {
        //From the description: The first image of the universe should look like the second picture after expansion
        val galaxies = findGalaxies(sampleInput)
        val expandedGalaxies = expandUniverse(galaxies, sampleInput)

        assert(expandedGalaxies != galaxies)
        assert(expandedGalaxies == findGalaxies(sampleInputAfterExpansion))
    }

    @Test
    fun `expandUniverse should work as expected with expansion factors larger than one`() {
        val galaxies = findGalaxies(sampleInput)
        val expandedGalaxiesFactor10 = expandUniverse(galaxies, sampleInput, 10)
        val expandedGalaxiesFactor100 = expandUniverse(galaxies, sampleInput, 100)
        assert(findTotalDistanceBetweenGalaxies(expandedGalaxiesFactor10) == 1030L)
        assert(findTotalDistanceBetweenGalaxies(expandedGalaxiesFactor100) == 8410L)
    }

    @Test
    fun `part1 should expand the universe, calculate the shortest distance between every unique pair of galaxies, and return the sum`() {
        assert(part1(sampleInput) == 374L)
    }

    @Test
    fun `part2 should expand the universe by a factor of 1 million, calculate the shortest distance between every unique pair of galaxies, and return the sum`() {
        assert(part2(sampleInput) == 82000210L)
    }
}