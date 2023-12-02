package day02

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day02Test {
    private val sampleInput = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent().lines()

    @Test
    fun `parse should return the expected gameDescription given a line`() {
        val result: GameDescription = parse(sampleInput.first())

        assert(result.id == 1)
        assert(result.maxRed == 4)
        assert(result.maxGreen == 2)
        assert(result.maxBlue == 6)
    }

    @Test
    fun `parse should handle a multi-digit game id`() {
        //line from prod input file
        val input = "Game 23: 10 green, 1 blue, 5 red; 2 red, 4 green; 9 green, 2 red; 10 green, 1 blue, 5 red"

        val result = parse(input)

        assert(result.id == 23)
    }

    @Test
    fun `parse should handle a multi-digit number of cubes`() {
        val result = parse(sampleInput[2])
        assert(result.maxRed == 20)
        assert(result.maxGreen == 13)

        assert(parse(sampleInput[3]).maxBlue == 15) //need a different input line for blue
    }

    @Nested
    inner class GameDescriptionTest {
        @Test
        fun `possible should return true iff it can be played with 12 red, 13 green, and 14 blue cubes`() {
            assert(parse(sampleInput[0]).possible)
            assert(parse(sampleInput[1]).possible)
            assert(!parse(sampleInput[2]).possible)
            assert(!parse(sampleInput[3]).possible)
            assert(parse(sampleInput[4]).possible)
        }
    }

    @Test
    fun `part1 should find the possible games and return the sum of their ids`() {
        assert(part1(sampleInput) == 8L)
    }
}