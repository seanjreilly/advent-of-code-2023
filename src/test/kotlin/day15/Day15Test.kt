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
}
