package day05

import utils.readInput
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

    val maps = listOf(
        "seed-to-soil", "soil-to-fertilizer", "fertilizer-to-water",
        "water-to-light", "light-to-temperature", "temperature-to-humidity",
        "humidity-to-location"
    ).map { SparseMap(input, it) }

    return parseSeeds(input).minOf { maps.fold(it) { accumulator, map -> map[accumulator] } }
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun parseSeeds(input: List<String>): Set<Long> {
    return input.first()
        .substringAfter(": ") //avoids a leading empty string from split
        .split(" ")
        .map(String::toLong)
        .toSet()
}

internal class SparseMap(internal val mappings: List<Mapping>) {
    operator fun get(sourceValue: Long): Long {
        return sourceValue + (mappings.firstOrNull { sourceValue in it.sourceRange }?.destinationOffset ?: 0L)
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