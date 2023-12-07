package day07

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day07")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input
        .map { parse(it) }
        .sortedWith(compareBy(Part1HandStrengthComparator) { it.hand })
        .withIndex()
        .sumOf { (it.index + 1) * it.value.bid }
}

fun part2(input: List<String>): Long {
    return 0
}

internal enum class Card(val symbol: Char) {
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('T'),
    JACK('J'),
    QUEEN('Q'),
    KING('K'),
    ACE('A');

    companion object {
        fun fromSymbol(raw: Char): Card = entries.first { it.symbol == raw }
    }
}

internal enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND
}

internal data class Hand(val cards: List<Card>) {
    val type: HandType
    init {
        val cardTypeCount = cards.groupBy { it }.mapValues { it.value.size }
        type = if (cardTypeCount.values.max() == 5) {
            HandType.FIVE_OF_A_KIND
        } else if (cardTypeCount.values.max() == 4) {
            HandType.FOUR_OF_A_KIND
        } else if (cardTypeCount.size == 2 && cardTypeCount.values.max() == 3 && cardTypeCount.values.min() == 2) {
            HandType.FULL_HOUSE
        } else if (cardTypeCount.size == 3 && cardTypeCount.values.max() == 3 && cardTypeCount.values.min() == 1) {
            HandType.THREE_OF_A_KIND
        } else if (cardTypeCount.filter { it.value == 2 }.count() == 2) {
            HandType.TWO_PAIR
        } else if (cardTypeCount.size == 4 && cardTypeCount.values.max() == 2) {
            HandType.ONE_PAIR
        } else {
            HandType.HIGH_CARD
        }
    }

    companion object {
        internal operator fun invoke(rawCards: String) : Hand {
            return Hand(rawCards.toCharArray().map { Card.fromSymbol(it) })
        }
    }
}

internal object Part1HandStrengthComparator : Comparator<Hand> {
    override fun compare(o1: Hand?, o2: Hand?): Int {
        if (o1 == null || o2 == null) {
            throw IllegalArgumentException("comparator doesn't work with null instances")
        }
        var result: Int = compareValues(o1.type, o2.type)
        val mutableCards = o1.cards.toMutableList()
        val otherMutableCards = o2.cards.toMutableList()
        while (result == 0 && mutableCards.isNotEmpty()) {
            result = compareValues(mutableCards.removeFirst(), otherMutableCards.removeFirst())
        }
        return result
    }
}

internal data class HandAndBid(val hand: Hand, val bid: Long)

internal fun parse(line: String): HandAndBid {
    val hand = Hand(line.substringBefore(' '))
    val bid = line.substringAfter(' ').toLong()
    return HandAndBid(hand, bid)
}