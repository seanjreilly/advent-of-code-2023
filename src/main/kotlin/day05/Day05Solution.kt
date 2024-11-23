package day05

import utils.LongSolution
import kotlin.math.min

fun main() = Day05Solution().run()
class Day05Solution : LongSolution() {

    override fun part1(input: List<String>): Long {
        val maps = parseSparseMaps(input)
        return parseSeeds(input).minOf { maps.fold(it) { accumulator, map -> map[accumulator] } }
    }

    override fun part2(input: List<String>): Long {
        val maps = parseSparseMaps(input)
        fun transform(seedValue: Long) = maps.fold(seedValue) { accumulator, map -> map[accumulator] }

        // complex solution for speed: for each range, check if it's continuous
        // if so, we just need to check the smallest value against our current best
        // if not, break it into chunks and try each chunk again
        // brute force small chunks

        fun isContinuous(range: LongRange): Boolean {
            if (range.size() == 1L) {
                return true //single value ranges are continuous by definition
            }
            val min = transform(range.first)
            val step = transform(range.first + 1) - min

            if (step < 0) {
                return false //no messing around with negative slops
            }

            return (step * range.size()) + min == transform(range.last)
        }

        val rangesToSplit: ArrayDeque<LongRange> = ArrayDeque(parseSeedRanges(input))
        var lowestValueSoFar = Long.MAX_VALUE

        while (rangesToSplit.isNotEmpty()) {
            val range = rangesToSplit.removeFirst()
            if (isContinuous(range)) {
                // the first part of a continuous range will yield the smallest value
                lowestValueSoFar = min(lowestValueSoFar, transform(range.first))
            } else if (range.size() < 10L) {
                // brute force small ranges
                // (necessary because small ranges can't be split properly)
                lowestValueSoFar = min(lowestValueSoFar, range.minOf { transform(it) })
            } else {
                rangesToSplit += range.split(range.size() / 2) //split it into chunks and add them all to the queue to test
            }
        }
        return lowestValueSoFar
    }
}
private fun parseSparseMaps(input: List<String>): List<SparseMap> {
    //every line containing a colon except the first is the start of a map definition
    return input
        .filter { ':' in it }
        .drop(1)
        .map { SparseMap(input, it) }
}

internal fun parseSeeds(input: List<String>): Set<Long> {
    return input.first()
        .substringAfter(": ") //avoids a leading empty string from split
        .split(" ")
        .map(String::toLong)
        .toSet()
}

internal fun parseSeedRanges(input: List<String>): Set<LongRange> {
    return input.first()
        .substringAfter(": ")
        .split(" ")
        .map { it.toLong() }
        .windowed(2, 2)
        .map { it.first() until (it.first() + it[1]) }
        .toSet()
}

internal class SparseMap(internal val mappings: List<Mapping>) {
    operator fun get(sourceValue: Long): Long {
        //if no mapping is found return the input by using an offset of zero
        val offset = mappings.firstOrNull { sourceValue in it.sourceRange }?.destinationOffset ?: 0L
        return sourceValue + offset
    }

    internal data class Mapping(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long) {
        val sourceRange = sourceRangeStart until (sourceRangeStart + rangeLength)
        val destinationOffset = destinationRangeStart - sourceRangeStart
    }

    companion object {
        internal operator fun invoke(input: List<String>, mapLabel: String) : SparseMap {
            val sortedMappings = input
                .asSequence()
                .dropWhile { !it.contains(mapLabel) }
                .drop(1)
                .takeWhile(String::isNotBlank)
                .map { it.split(' ') }
                .map { it.map { it.toLong() } }
                .map { Mapping(it[0], it[1], it[2]) }
                .sortedBy { it.sourceRangeStart }
                .toList()
            return SparseMap(sortedMappings)
        }
    }
}

internal fun LongRange.split(chunkSize: Long): List<LongRange> {
    val result = mutableListOf<LongRange>()
    var remainingRange = this

    while (remainingRange.size() > chunkSize) {
        val startOfNextChunk = remainingRange.first + chunkSize
        result += remainingRange.first until startOfNextChunk
        remainingRange = startOfNextChunk..remainingRange.last
    }
    result += remainingRange
    if (result.size < 2) {
        throw IllegalArgumentException("Cannot split range into chunks of size ${chunkSize}")
    }
    return result
}

internal fun LongRange.size(): Long = this.last - this.first