package day05

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class Day05SolutionTest {
    private val sampleInput = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent().lines()

    private val solution = Day05Solution()

    @Test
    fun `parseSeeds() should return a list of seed ids given an input file`() {
        val result: Set<Long> = parseSeeds(sampleInput)

        assert(result == setOf(79L, 14L, 55L, 13L))
    }

    @Test
    fun `parseSeedRanges should return a list of seed ranges given an input file`() {
        val result: Set<LongRange> = parseSeedRanges(sampleInput)

        assert(result == setOf(79L until 93L, 55L until 68L))
    }

    @Test
    fun `part1 should parse the seed numbers and the appropriate maps, run each seed number through every map in order, and return the lowest overall result`() {
        assert(solution.part1(sampleInput) == 35L)
    }

    @Test
    fun `part2 should consider every seed in every range, run each seed number through every map in order, and return the lowest overall result`() {
        assert(solution.part2(sampleInput) == 46L)
    }

    @Nested
    inner class SparseMapTest {
        @Test
        fun `constructor should return a SparseMap with the appropriate entries sorted by sourceRangeStart, given input and a map label`() {
            val result = SparseMap(sampleInput, "fertilizer-to-water")

            val expectedMappings = listOf(
                SparseMap.Mapping(42L, 0L, 7L),
                SparseMap.Mapping(57L, 7L, 4L),
                SparseMap.Mapping(0L, 11L, 42L),
                SparseMap.Mapping(49L, 53L, 8L),
            )
            assert(result.mappings == expectedMappings)
        }

        @Test
        fun `constructor should return different maps given different labels`() {
            val fertilizerToWater = SparseMap(sampleInput, "fertilizer-to-water")
            val seedToSoil = SparseMap(sampleInput, "seed-to-soil")

            assert(fertilizerToWater.mappings.isNotEmpty())
            assert(seedToSoil.mappings.isNotEmpty())
            assert(fertilizerToWater.mappings != seedToSoil.mappings)
        }

        @Test
        fun `constructor should work with the final section`() {
            val result = SparseMap(sampleInput, "humidity-to-location")
            assert(result.mappings.isNotEmpty())
        }

        @Test
        fun `get(sourceValue) should find the mapping entry that contains the source value and produce the correct destination value`() {
            val map = SparseMap(sampleInput, "seed-to-soil")
            assert(map[50L] == 52L)

            (1 until 48).forEach {
                assert(map[it + 50L] == 52L + it)
            }

            assert(map[98L] == 50L)
            assert(map[99L] == 51L)
        }

        @Test
        fun `get(sourceValue) should return its input given a source value that doesn't correspond to a mapping`() {
            val map = SparseMap(sampleInput, "seed-to-soil map:")
            assert(map[100L] == 100L)
        }
    }

    @Nested
    inner class LongRangeTest {
        @Test
        fun `split should return a list of ranges, each of the appropriate size`() {
            val source: LongRange = 1L..27L
            val result: List<LongRange> = source.split(10)

            assert(result == listOf(1L..10L, 11L..20L, 21L..27L))
        }

        @Test
        fun `split should work when the split is an exact divisor of the range`() {
            val source: LongRange = 1L .. 20L
            val result = source.split(5)

            assert(result == listOf(1L..5L, 6L..10L, 11L..15L, 16L..20L))

        }

        @Test
        fun `split should throw an exception when the range can't be split`() {
            val source: LongRange = 1L .. 2L
            val exception = assertThrows<IllegalArgumentException> { source.split(2) }

            assert(exception.message == "Cannot split range into chunks of size 2")
        }
    }
}
