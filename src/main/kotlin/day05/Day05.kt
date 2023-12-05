package day05

import utils.readInput
import kotlin.streams.asStream
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day05")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val maps = parseSparseMaps(input)
    return parseSeeds(input).minOf { maps.fold(it) { accumulator, map -> map[accumulator] } }
}

fun part2(input: List<String>): Long {
    val maps = parseSparseMaps(input)

    //super naive implementation — takes forever
    //    return parseSeedRanges(input)
    //        .asSequence()
    //        .flatten()
    //        .minOf { maps.fold(it) { accumulator, map -> map[accumulator] } }

    // ever-so-slightly less naive implementation that runs in parallel
    // super slow but actually returns a result in just over 2 minutes on my mac
    return parseSeedRanges(input)
        .asSequence()
        .asStream()
        .parallel()
        .flatMap { it.asSequence().asStream().parallel() }
        .map { maps.fold(it) { accumulator, map -> map[accumulator] } }
        .min(Comparator.comparingLong { it }).get()
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