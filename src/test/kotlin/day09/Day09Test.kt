package day09

import org.junit.jupiter.api.Test

class Day09Test {
    private val sampleInput = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent().lines()

    @Test
    fun `parseLongs should return a list of Longs given an input line`() {
        val result = parseLongs(sampleInput.first())

        assert(result == listOf(0L, 3L, 6L, 9L, 12L, 15L))
    }
    
    @Test
    fun `predictNextValue should recursively calculate differences until it produces a list of all zeroes, and then return the sum of the previous result and the last list item`() {
        assert(predictNextValue(parseLongs(sampleInput[0])) == 18L)
        assert(predictNextValue(parseLongs(sampleInput[1])) == 28L)
        assert(predictNextValue(parseLongs(sampleInput[2])) == 68L)
    }

    @Test
    fun `part1 should predict the next value for each line and return their sum`() {
        assert(part1(sampleInput) == 114L)
    }

    @Test
    fun `part2 should predict the previous value for each line and return their sum`() {
        assert(part2(sampleInput) == 2L)
    }
}