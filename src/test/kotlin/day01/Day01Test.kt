package day01

import org.junit.jupiter.api.Test

class Day01Test {
    private val sampleInput = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent().lines()

    @Test
    fun `firstDigit should return the first digit in the string`() {
        assert(firstDigit(sampleInput[0]) == '1')
        assert(firstDigit(sampleInput[1]) == '3')
        assert(firstDigit(sampleInput[2]) == '1')
        assert(firstDigit(sampleInput[3]) == '7')
    }

    @Test
    fun `lastDigit should return the last digit in the string`() {
        assert(lastDigit(sampleInput[0]) == '2')
        assert(lastDigit(sampleInput[1]) == '8')
        assert(lastDigit(sampleInput[2]) == '5')
        assert(lastDigit(sampleInput[3]) == '7')
    }

    @Test
    fun `firstAndLastDigits should return the first and last digit in the string concatenated together`() {
        assert(firstAndLastDigits(sampleInput[0]) == "12")
        assert(firstAndLastDigits(sampleInput[1]) == "38")
        assert(firstAndLastDigits(sampleInput[2]) == "15")
        assert(firstAndLastDigits(sampleInput[3]) == "77")
    }

    @Test
    fun `part1 should return the sum of the first and last digits of every line`() {
        assert(part1(sampleInput) == 142L)
    }
}