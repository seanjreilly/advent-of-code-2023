package day12

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day12")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input
        .map(ConditionRecord::parse).sumOf { it.countLegalArrangements() }
}

fun part2(input: List<String>): Long {
    return 0
}

internal data class ConditionRecord(val damagedRecord: String, val alternateFormat: List<Int>) {
    fun isLegal(proposedRecordPattern: CharSequence): Boolean {
        return proposedRecordPattern
            .split('.')
            .filter { it.isNotEmpty() }
            .map { it.length } == alternateFormat
    }

    fun countLegalArrangements(partialRecord: String = this.damagedRecord): Long {
        val questionMarkIndex = partialRecord.indexOf('?')
        if (questionMarkIndex == -1) {
            return if (isLegal(partialRecord)) { 1 } else { 0 }
        }

        return countLegalArrangements(partialRecord.replaceFirst('?', '.')) +
            countLegalArrangements(partialRecord.replaceFirst('?', '#'))
    }

    companion object {
        internal fun parse(line: String): ConditionRecord {
            return ConditionRecord(line.substringBefore(' '), line.substringAfter(' ').split(',').map { it.toInt() })
        }
    }
}

internal fun unfold(line: String): String {
    val parts = line.split(' ')
    return (List(5) { parts.first() }).joinToString("?") + " " + (List(5) { parts.last() }).joinToString(",")
}