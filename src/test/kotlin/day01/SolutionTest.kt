package day01

import org.junit.jupiter.api.Test

class SolutionTest {
    private val sampleInput = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent().lines()

    private val sampleInputPart2 = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent().lines()

    private val solution = Solution()

    @Test
    fun `firstDigit should return the first digit in the string`() {
        assert(solution.firstDigit(sampleInput[0]) == '1')
        assert(solution.firstDigit(sampleInput[1]) == '3')
        assert(solution.firstDigit(sampleInput[2]) == '1')
        assert(solution.firstDigit(sampleInput[3]) == '7')
    }

    @Test
    fun `lastDigit should return the last digit in the string`() {
        assert(solution.lastDigit(sampleInput[0]) == '2')
        assert(solution.lastDigit(sampleInput[1]) == '8')
        assert(solution.lastDigit(sampleInput[2]) == '5')
        assert(solution.lastDigit(sampleInput[3]) == '7')
    }

    @Test
    fun `firstAndLastDigits should return the first and last digit in the string concatenated together`() {
        assert(solution.firstAndLastDigits(sampleInput[0]) == "12")
        assert(solution.firstAndLastDigits(sampleInput[1]) == "38")
        assert(solution.firstAndLastDigits(sampleInput[2]) == "15")
        assert(solution.firstAndLastDigits(sampleInput[3]) == "77")
    }

    @Test
    fun `firstDigitPart2 should take spelled out digits into account`() {
        assert(solution.firstDigitPart2(sampleInputPart2[0]) == '2')
        assert(solution.firstDigitPart2(sampleInputPart2[1]) == '8')
        assert(solution.firstDigitPart2(sampleInputPart2[2]) == '1')
        assert(solution.firstDigitPart2(sampleInputPart2[3]) == '2')
        assert(solution.firstDigitPart2(sampleInputPart2[4]) == '4')
        assert(solution.firstDigitPart2(sampleInputPart2[5]) == '1')
        assert(solution.firstDigitPart2(sampleInputPart2[6]) == '7')
    }

    @Test
    fun `lastDigitPart2 should take spelled out digits into account`() {
        assert(solution.lastDigitPart2(sampleInputPart2[0]) == '9')
        assert(solution.lastDigitPart2(sampleInputPart2[1]) == '3')
        assert(solution.lastDigitPart2(sampleInputPart2[2]) == '3')
        assert(solution.lastDigitPart2(sampleInputPart2[3]) == '4')
        assert(solution.lastDigitPart2(sampleInputPart2[4]) == '2')
        assert(solution.lastDigitPart2(sampleInputPart2[5]) == '4')
        assert(solution.lastDigitPart2(sampleInputPart2[6]) == '6')
    }

    @Test
    fun `part1 should return the sum of the first and last digits of every line`() {
        assert(solution.part1(sampleInput) == 142L)
    }

    @Test
    fun `part2 should transform the input, and then return the sum of the first and last digits of every line`() {
        assert(solution.part2(sampleInputPart2) == 281L)
    }

}