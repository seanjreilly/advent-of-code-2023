package day06

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day06Test {
    private val sampleInput = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent().lines()

    @Test
    fun `parse should return a list of RaceDescription instances`() {
        val results: List<RaceDescription> = parse(sampleInput)
        val expectedResults = listOf(
            RaceDescription(time=7, recordDistance=9),
            RaceDescription(15, 40),
            RaceDescription(30, 200)
        )

        assert(results.size == expectedResults.size)
        results.forEachIndexed { index, result -> assert(result == expectedResults[index])  }
    }

    @Test
    fun `part1 should parse the races, calculate the number of ways to beat the record for each race, and return the product`() {
        assert(part1(sampleInput) == 288L)
    }

    @Test
    fun `part2 should parse the input as a single race, and calculate the number of ways to beat the record`() {
        assert(part2(sampleInput) == 71503L)
    }
    
    @Test
    fun `parsePart2 should return a single RaceDescription`() {
        val result = parsePart2(sampleInput)
        assert(result == RaceDescription(71530L, 940200L))
    }

    @Nested
    inner class RaceDescriptionTest {
        @Test
        fun `waysToBeatRecordDistance() should return the possible ways to beat the record distance for the race`() {
            assert(RaceDescription(7, 9).waysToBeatRecordDistance() == 4)
            assert(RaceDescription(15, 40).waysToBeatRecordDistance() == 8)
            assert(RaceDescription(30, 200).waysToBeatRecordDistance() == 9)
        }
    }

}