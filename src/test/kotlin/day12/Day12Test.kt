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

    @Nested
    inner class ConditionRecordTest {
        @Test
        fun `parse should return a condition record containing a pattern string and a list of ints`() {
            val result = ConditionRecord.parse(sampleInput.first())

            assert(result.damagedRecord == "???.###")
            assert(result.alternateFormat == listOf(1,1,3))
        }
        
        @Test
        fun `countLegalArrangements should return the number of legal patterns which would satisfy the alternate format`() {
            assert(ConditionRecord.parse(sampleInput.first()).countLegalArrangements() == 1L)
            assert(ConditionRecord.parse(sampleInput[1]).countLegalArrangements() == 4L)
            assert(ConditionRecord.parse(sampleInput.last()).countLegalArrangements() == 10L)
        }

        @Test
        fun `fun isLegal should return true given a proposed record pattern that matches the alternateFormat`() {
            val record = ConditionRecord.parse(sampleInput.first())

            assert(record.isLegal("#.#.###"))
            assert(!record.isLegal("##..###"))
            assert(!record.isLegal(".##.###"))
        }
    }
}