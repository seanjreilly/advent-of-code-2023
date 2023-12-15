package day15

import org.junit.jupiter.api.Test

class Day15Test {
    private val sampleInput = """rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7""".lines()
    
    @Test
    fun `hash should calculate a hash by adding the ASCII code of each character times 17 mod 256`() {
        assert("HASH".hash() == 52)
    }

    @Test
    fun `part1 should read the input line, split on commas, calculate the hash of each segment, and return the sum`() {
        assert(part1(sampleInput) == 1320L)
    }

    @Test
    fun `part2 should process every intialization step, calculate the focussing power for each lens, and return the sum`() {
        assert(part2(sampleInput) == 145L)
    }
    
    @Test
    fun `mutableList_removeAt() should move elements after the index forward by 1`() {
        val list = mutableListOf(1,2,3,4,5)
        list.removeAt(list.indexOf(3))
        assert(list == listOf(1,2,4,5))
    }
    
    @Test
    fun `processInitializationSequence should follow the initialization instructions and return a list of boxes, with LabelAndFocalLength instances in each box`() {
        val result:List<List<LabelAndFocalLength>> = processInitializationSequence(sampleInput.first().split(','))

        assert(result.size == 256)
        assert(result[0] == listOf(LabelAndFocalLength("rn", 1), LabelAndFocalLength("cm", 2)))
        assert(result[3] == listOf(LabelAndFocalLength("ot", 7), LabelAndFocalLength("ab", 5), LabelAndFocalLength("pc", 6)))
        val otherBoxes = result.filterIndexed { index, _ -> index !in listOf(0, 3) }
        assert(otherBoxes.all { it.isEmpty() }  )
    }


}