package day07

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day07Test {
    private val sampleInput = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent().lines()
    
    @Test
    fun `parseLine() should return a HandAndBidInstance`() {
        val result: HandAndBid = parse(sampleInput.first())
        assert(result == HandAndBid(Hand("32T3K"), 765L))
    }

    @Test
    fun `part1 should order every card by strength, multiply the hand's bid by it's rank for each card, and return the sum for all cards`() {
        assert(part1(sampleInput) == 6440L)
    }

    @Nested
    inner class HandTest {
        @Test
        fun `constructor should take a string and expose a list of cards`() {
            val hand = Hand("32T3K")

            assert(hand.cards.size == 5)
            assert(hand.cards[0] == Card.THREE)
            assert(hand.cards[1] == Card.TWO)
            assert(hand.cards[2] == Card.TEN)
            assert(hand.cards[3] == Card.THREE)
            assert(hand.cards[4] == Card.KING)
        }
        @Test
        fun `type should return FIVE_OF_A_KIND for a five of a kind hand`() {
            assert(Hand("AAAAA").type == HandType.FIVE_OF_A_KIND)
            assert(Hand("33333").type == HandType.FIVE_OF_A_KIND)
        }

        @Test
        fun `type should return FOUR_OF_A_KIND for a four of a kind hand`() {
            assert(Hand("AAAAK").type == HandType.FOUR_OF_A_KIND)
            assert(Hand("AAAQA").type == HandType.FOUR_OF_A_KIND)
            assert(Hand("AAJAA").type == HandType.FOUR_OF_A_KIND)
            assert(Hand("ATAAA").type == HandType.FOUR_OF_A_KIND)
            assert(Hand("9AAAA").type == HandType.FOUR_OF_A_KIND)
        }

        @Test
        fun `type should return FULL_HOUSE for a full house hand`() {
            assert(Hand("33322").type == HandType.FULL_HOUSE)
            assert(Hand("33444").type == HandType.FULL_HOUSE)
        }

        @Test
        fun `type should return THREE_OF_A_KIND for a three of a kind hand`() {
            assert(Hand("23444").type == HandType.THREE_OF_A_KIND)
            assert(Hand("AAAKQ").type == HandType.THREE_OF_A_KIND)
        }

        @Test
        fun `type should return TWO_PAIR for a hand with two pairs`() {
            assert(Hand("A2233").type == HandType.TWO_PAIR)
            assert(Hand("24233").type == HandType.TWO_PAIR)
            assert(Hand("22433").type == HandType.TWO_PAIR)
            assert(Hand("22343").type == HandType.TWO_PAIR)
            assert(Hand("2233A").type == HandType.TWO_PAIR)
            assert(Hand("A2323").type == HandType.TWO_PAIR)
        }

        @Test
        fun `type should return ONE_PAIR for a hand with one pair`() {
            assert(Hand("23455").type == HandType.ONE_PAIR)
        }

        @Test
        fun `type should return HIGH_CARD given a hand where every card is a different type`() {
            assert(Hand("23456").type == HandType.HIGH_CARD)
        }
    }

    @Test
    fun `Part1HandStrengthComparator should compare by hand type and then by card value in order`() {
        assert(Part1HandStrengthComparator.compare(Hand("22222"), Hand("AAAAK")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("2AAAA"), Hand("AAAKK")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("22233"), Hand("AAAKQ")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("22234"), Hand("AAKKQ")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("22334"), Hand("AAKQJ")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("22345"), Hand("AKQJT")) > 0)

        assert(Part1HandStrengthComparator.compare(Hand("33332"), Hand("2AAAA")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("77888"), Hand("77788")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("AAAAK"), Hand("AAAAQ")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("KKKKK"), Hand("QQQQQ")) > 0)
        assert(Part1HandStrengthComparator.compare(Hand("23457"), Hand("23456")) > 0)
    }
}