package day04

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day04Test {
    private val sampleInput = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent().lines()

    @Test
    fun `part1 should parse all of the cards and return the sum of their points`() {
        assert(part1(sampleInput) == 13L)
    }

    @Test
    fun `parseCard should return a card instance with winning numbers and selected numbers given an input line`() {
        val result: Card = parseCard(sampleInput[0])

        assert(result.winningNumbers == setOf(41, 48, 83, 86, 17))
        assert(result.numbersYouHave == setOf(83, 86, 6, 31, 17, 9, 48, 53))
    }

    @Nested
    inner class CardTest {

        @Test
        fun `matchingNumberCount should return the number of matches between numbersYouHave and WinningNumbers`() {
            assert(parseCard(sampleInput.last()).matchingNumberCount == 0)
            assert(Card(setOf(1), setOf(1, 2, 3)).matchingNumberCount == 1)
            assert(Card(setOf(1, 2), setOf(1, 2, 3)).matchingNumberCount == 2)
            assert(Card(setOf(1, 2, 3), setOf(1, 2, 3)).matchingNumberCount == 3)
        }

        @Test
        fun `points() should return zero when there are no matches between numbersYouHave and winningNumbers`() {
            val cardWithNoMatchingNumbers = parseCard(sampleInput.last())

            assert(cardWithNoMatchingNumbers.points() == 0)
        }

        @Test
        fun `points() should return 1 point when there is one match between numbersYouHave and winningNumbers`() {
            val cardWithOneMatch = Card(setOf(1), setOf(1, 2, 3))

            assert(cardWithOneMatch.points() == 1)
        }

        @Test
        fun `points() should double the score for each additional match between numbersYouHave and winningNumbers`() {
            val cardWithFiveMatches = Card(setOf(1, 2, 3, 4, 5), setOf(1, 2, 3, 4, 5))

            assert(cardWithFiveMatches.points() == 16)
        }
    }
}