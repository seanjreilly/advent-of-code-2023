package day08

import org.junit.jupiter.api.Test

class Day08Test {
    private val sampleInput = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().lines()

    private val sampleInput2 = """
        LLR

        AAA = (BBB, BBB)
        BBB = (AAA, ZZZ)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().lines()
    
    @Test
    fun `parseNodes should return a map of ids to LeftRight instances given input`() {
        val nodes: Map<String, LeftRight> = parse(sampleInput)

        assert(nodes.size == 7)
        assert(nodes["AAA"] == LeftRight("BBB","CCC"))
        assert(nodes["BBB"] == LeftRight("DDD","EEE"))
        assert(nodes["CCC"] == LeftRight("ZZZ","GGG"))
        assert(nodes["DDD"] == LeftRight("DDD","DDD"))
        assert(nodes["EEE"] == LeftRight("EEE","EEE"))
        assert(nodes["GGG"] == LeftRight("GGG","GGG"))
        assert(nodes["ZZZ"] == LeftRight("ZZZ","ZZZ"))

        val otherNodes = parse(sampleInput2)
        assert(otherNodes.size == 3)
        assert(otherNodes["AAA"] == LeftRight("BBB", "BBB"))
        assert(otherNodes["BBB"] == LeftRight("AAA", "ZZZ"))
        assert(otherNodes["ZZZ"] == LeftRight("ZZZ", "ZZZ"))
    }

    @Test
    fun `part1 should count how many steps are needed following the directions to reach ZZZ from AAA`() {
        assert(part1(sampleInput) == 2L)
        assert(part1(sampleInput2) == 6L)
    }
}
