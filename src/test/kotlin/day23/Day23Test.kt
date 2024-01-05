package day23

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Day23Test {
    private val sampleInput = """
        #.#####################
        #.......#########...###
        #######.#########.#.###
        ###.....#.>.>.###.#.###
        ###v#####.#v#.###.#.###
        ###.>...#.#.#.....#...#
        ###v###.#.#.#########.#
        ###...#.#.#.......#...#
        #####.#.#.#######.#.###
        #.....#.#.#.......#...#
        #.#####.#.#.#########v#
        #.#...#...#...###...>.#
        #.#.#v#######v###.###v#
        #...#.>.#...>.>.#.###.#
        #####v#.#.###v#.#.###.#
        #.....#...#...#.#.#...#
        #.#########.###.#.#.###
        #...###...#...#...#.###
        ###.###.#.###v#####v###
        #...#...#.#.>.>.#.>.###
        #.###.###.#.###.#.#v###
        #.....###...###...#...#
        #####################.#
    """.trimIndent().lines()

    @Test
    fun `part1 should return the longest path that doesn't visit any squares twice and travels the right way down steep slopes`() {
        assert(part1(sampleInput) == 94L)
    }

    @Test
    fun `part2 should return the longest path that doesn't visit any squares twice regardless of steep slopes`() {
        assert(part2(sampleInput) == 154L)
    }

    @Test
    fun `findLongestPath should find the longest path from the top row to the bottom row`() {
        val input = """
            #.###
            #...#
            ###.#
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 4)
    }

    @Test
    fun `findLongestPath should not cross a ^ symbol any direction other than north`() {
        val input = """
            #.#######
            #...#####
            #^#.....#
            #.##.##.#
            #.^^.##.#
            #######.#
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 11)
    }

    @Test
    fun `findLongestPath should cross a ^ symbol travelling north`() {
        val input = """
            #.#######
            #...#####
            #.#.....#
            #.##^##.#
            #....##.#
            #######.#
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 15)
    }

    @Test
    fun `findLongestPath should not cross a v symbol any direction other than south`() {
        val input = """
            #.#######
            #...#####
            #.#.....#
            #.##v##.#
            #.vv.##.#
            #######.#
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 11)
    }

    @Test
    fun `findLongestPath should cross a v symbol travelling south`() {
        val input = """
            #.#######
            #...#####
            #v#.....#
            #.##.##.#
            #....##.#
            #######.#
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 15)
    }

    @Test
    fun `findLongestPath should not cross a greater than symbol any direction other than east`() {
        val input = """
            #.#######
            #...#####
            #.#.....#
            #.##>##>#
            #....##.#
            #.#####.#
            #...>...#
            #.#######
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 7)
    }

    @Test
    fun `findLongestPath should cross a greater than symbol travelling east`() {
        val input = """
            #.#######
            #.#######
            #.##....#
            #.##.##.#
            #.>..##.#
            #.#####.#
            #.......#
            #.#######
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 23)
    }

    @Test
    fun `findLongestPath should not cross a less than symbol any direction other than west`() {
        val input = """
            #.#######
            #...#####
            #.#.....#
            #.##<##.#
            #.<..##<#
            #.#####.#
            #.......#
            #.#######
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 7)
    }

    @Test
    fun `findLongestPath should cross a less than symbol travelling west`() {
        val input = """
            #.#######
            #.#######
            #.##....#
            #.##.##.#
            #....##.#
            #.#####.#
            #.<.....#
            #.#######
        """.trimIndent().lines()

        val result = findLongestPath(input)

        assert(result == 23)
    }

    @Test
    fun `findLongestPath should return 94 given sample input`() {
        val result = findLongestPath(sampleInput)
        assert(result == 94)
    }

    @Test
    fun `findLongestPath should throw an IllegalArgumentException given more than 1 path cell in the top row`() {
        val input = """
            ...
            .#.
            ##.
        """.trimIndent().lines()

        assertThrows<IllegalArgumentException> { findLongestPath(input) }
    }

    @Test
    fun `findLongestPath should throw an IllegalArgumentException given no path cells in the top row`() {
        val input = """
            ###
            .#.
            ##.
        """.trimIndent().lines()

        assertThrows<IllegalArgumentException> { findLongestPath(input) }
    }

    @Test
    fun `findLongestPath should throw an IllegalArgumentException given more than 1 path cell in the bottom row`() {
        val input = """
            #.#
            #.#
            #..
        """.trimIndent().lines()

        assertThrows<IllegalArgumentException> { findLongestPath(input) }
    }

    @Test
    fun `findLongestPath should throw an IllegalArgumentException given no path cells in the bottom row`() {
        val input = """
            #.#
            #.#
            ###
        """.trimIndent().lines()

        assertThrows<IllegalArgumentException> { findLongestPath(input) }
    }

    @Test
    fun `findLongestPathPart2 should find the longest path from the top row to the bottom row and should treat steep slopes as paths`() {
        val input = """
            #.###
            #^<>#
            ###.#
        """.trimIndent().lines()

        val result = findLongestPathPart2(input)

        assert(result == 4)
    }

    @Test
    fun `findLongestPathPart2 should return 154 given sample input`() {
        val result = findLongestPathPart2(sampleInput)

        assert(result == 154)
    }
}
