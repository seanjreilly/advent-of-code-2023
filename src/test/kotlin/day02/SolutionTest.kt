package day02

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.readInput

class SolutionTest {
    private val sampleInput = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent().lines()

    private val solution = Solution()

    @Test
    fun `parse should return the expected gameDescription given a line`() {
        val result: GameDescription = solution.parse(sampleInput.first())

        assert(result.id == 1)
        assert(result.maxRed == 4)
        assert(result.maxGreen == 2)
        assert(result.maxBlue == 6)
    }

    @Test
    fun `parse should handle a multi-digit game id`() {
        //line from prod input file
        val input = "Game 23: 10 green, 1 blue, 5 red; 2 red, 4 green; 9 green, 2 red; 10 green, 1 blue, 5 red"

        val result = solution.parse(input)

        assert(result.id == 23)
    }

    @Test
    fun `parse should handle a multi-digit number of cubes`() {
        val result = solution.parse(sampleInput[2])
        assert(result.maxRed == 20)
        assert(result.maxGreen == 13)

        assert(solution.parse(sampleInput[3]).maxBlue == 15) //need a different input line for blue
    }

    @Nested
    inner class GameDescriptionTest {
        @Test
        fun `possible should return true iff it can be played with 12 red, 13 green, and 14 blue cubes`() {
            assert(solution.parse(sampleInput[0]).possible)
            assert(solution.parse(sampleInput[1]).possible)
            assert(!solution.parse(sampleInput[2]).possible)
            assert(!solution.parse(sampleInput[3]).possible)
            assert(solution.parse(sampleInput[4]).possible)
        }

        @Test
        fun `power should return minRed times minGreen times minBlue`() {
            assert(solution.parse(sampleInput[0]).power == 48)
            assert(solution.parse(sampleInput[1]).power == 12)
            assert(solution.parse(sampleInput[2]).power == 1560)
            assert(solution.parse(sampleInput[3]).power == 630)
            assert(solution.parse(sampleInput[4]).power == 36)
        }
    }

    @Test
    fun `sanity check -- the production file should not include any lines that don't mention every color at least once`() {
        val lines = readInput("Day2")
        lines.forEach {
            val gd = solution.parse(it)

            assert(gd.maxRed > 0) { "line ${gd.id} doesn't have any red cubes" }
            assert(gd.maxGreen > 0) { "line ${gd.id} doesn't have any green cubes" }
            assert(gd.maxBlue > 0) { "line ${gd.id} doesn't have any blue cubes" }
        }
    }

    @Test
    fun `part1 should find the possible games and return the sum of their ids`() {
        assert(solution.part1(sampleInput) == 8L)
    }

    @Test
    fun `part2 should return the sum of the games power scores`() {
        assert(solution.part2(sampleInput) == 2286L)
    }
}