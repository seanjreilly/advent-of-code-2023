package day12

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day12Test {
    private val sampleInput = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent().lines()

    @Test
    fun `part1 should calculate the number of legal arrangements for each line and return the sum`() {
        assert(part1(sampleInput) == 21L)
    }

    @Test
    fun `part2 should calculate the number of legal arrangements for each unfolded line and return the sum`() {
        assert(part2(sampleInput) == 525152L)
    }

    @Nested
    inner class ConditionRecordTest {
        @Test
        fun `parse should return a condition record containing a pattern string and a list of ints`() {
            val result = ConditionRecord.parse(sampleInput.first())

            assert(result.damagedRecord == "???.###")
            assert(result.alternateFormat == listOf(1,1,3))
        }
        
        @Test
        fun `countPossibleArrangements should return the number of legal patterns which would satisfy the alternate format`() {
            assert(ConditionRecord.parse(sampleInput[0]).countPossibleArrangements() == 1L)
            assert(ConditionRecord.parse(sampleInput[1]).countPossibleArrangements() == 4L)
            assert(ConditionRecord.parse(sampleInput[2]).countPossibleArrangements() == 1L)
            assert(ConditionRecord.parse(sampleInput[3]).countPossibleArrangements() == 1L)
            assert(ConditionRecord.parse(sampleInput[4]).countPossibleArrangements() == 4L)
            assert(ConditionRecord.parse(sampleInput[5]).countPossibleArrangements() == 10L)
        }
        
        @Test
        fun `countPossibleArrangements should calculate results with unfolded arguments`() {
            assert(ConditionRecord.parse(unfold(sampleInput[0])).countPossibleArrangements() == 1L)
            assert(ConditionRecord.parse(unfold(sampleInput[1])).countPossibleArrangements() == 16384L)
            assert(ConditionRecord.parse(unfold(sampleInput[2])).countPossibleArrangements() == 1L)
            assert(ConditionRecord.parse(unfold(sampleInput[3])).countPossibleArrangements() == 16L)
            assert(ConditionRecord.parse(unfold(sampleInput[4])).countPossibleArrangements() == 2500L)
            assert(ConditionRecord.parse(unfold(sampleInput[5])).countPossibleArrangements() == 506250L)
        }
    }


    @Test
    fun `unfold replaces the pattern with five copies of itself joined by a question mark, and the list of ints repeated five times`() {
        assert(unfold(".# 1") == ".#?.#?.#?.#?.# 1,1,1,1,1")
    }
}