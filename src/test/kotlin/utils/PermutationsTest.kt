package utils

import org.junit.jupiter.api.Test

class PermutationsTest {
    @Test
    fun `permutations() should generate all permutations of the string given a two letter string`() {
        val expectedTwoLetterPermutations = listOf("AB", "BA")
        assert("AB".permutations() == expectedTwoLetterPermutations)
    }

    @Test
    fun `permutations() should generate all permutations of the string given a three letter string`() {
        val expectedThreeLetterPermutations = listOf("ABC", "ACB", "BAC", "BCA", "CBA", "CAB")
        assert("ABC".permutations() == expectedThreeLetterPermutations)
    }

    @Test
    fun `permutations() should generate all permutations of the string given a four letter string`() {
        val expectedFourLetterPermutations = listOf(
        "ABCD",
        "ABDC",
        "ACBD",
        "ACDB",
        "ADCB",
        "ADBC",
        "BACD",
        "BADC",
        "BCAD",
        "BCDA",
        "BDCA",
        "BDAC",
        "CBAD",
        "CBDA",
        "CABD",
        "CADB",
        "CDAB",
        "CDBA",
        "DBCA",
        "DBAC",
        "DCBA",
        "DCAB",
        "DACB",
        "DABC",
        )

        assert("ABCD".permutations() == expectedFourLetterPermutations)
    }

    @Test
    fun `permutations should take duplicate letters into account`() {
        assert("ABA".permutations() == listOf("ABA", "AAB", "BAA"))
    }
}