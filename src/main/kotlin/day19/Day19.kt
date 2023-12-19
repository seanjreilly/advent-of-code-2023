package day19

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day19")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val workflows = parseWorkflows(input)
    return parseParts(input)
        .filter { testPart(it, workflows) }
        .sumOf { it.totalRating.toLong() }
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun testPart(part: Part, workflows: Map<String, Workflow>): Boolean {
    fun moveToWorkflow(newLabel:String ) : MutableList<WorkflowRule>  {
        return workflows[newLabel]!!.toMutableList()
    }

    var remainingSteps: MutableList<WorkflowRule> = moveToWorkflow("in")
    while (true) {
        val workflowRule = remainingSteps.removeFirst()
        val newLabel = workflowRule.nextLabel(part)
        when (newLabel) {
            null -> continue
            "A" -> return true
            "R" -> return false
            else -> remainingSteps = moveToWorkflow(newLabel)
        }
    }
}

internal typealias Workflow = List<WorkflowRule>

internal sealed interface WorkflowRule {
    fun nextLabel(part: Part): String?
}

internal data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    operator fun get(field:Char) : Int {
        return when (field) {
            'x' -> x
            'm' -> m
            'a' -> a
            's' -> s
            else -> throw IllegalArgumentException("Unexpected field name '$field' ")
        }
    }

    val totalRating = x + m + a + s
}

private val partRegex = """[{]x=(\d+),m=(\d+),a=(\d+),s=(\d+)[}]""".toRegex()
internal fun parseParts(input: List<String>): List<Part> {
    return input
        .asSequence()
        .dropWhile { it.isNotBlank() }.drop(1)
        .map { partRegex.matchEntire(it)!!.destructured }
        .map { it -> it.toList().map { it.toInt() } }
        .map { (x,m,a,s) -> Part(x,m,a,s) }
        .toList()
}


internal typealias Predicate = (Int, Int) -> Boolean
internal val GT: Predicate = { a, b -> a > b }
internal val LT: Predicate = { a, b -> a < b }

internal data class AssignmentRule(val destination: String) : WorkflowRule {
    override fun nextLabel(part: Part): String = destination
}

internal data class ConditionalAssignmentRule(
    val fieldToTest: Char, val predicate: Predicate, val testValue: Int, val matchDestination: String
): WorkflowRule {
    override fun nextLabel(part: Part): String? {
        return if (predicate(part[fieldToTest], testValue)) { matchDestination } else { null }
    }
}

internal fun parseWorkflows(input: List<String>): Map<String, Workflow> {
    return input
        .takeWhile(String::isNotBlank)
        .associate { line -> line.substringBefore('{') to parseWorkflowRules(line) }
}

private val conditionalAssignmentRegex = """(.)([<>])(\d+):(.+)""".toRegex()
internal fun parseWorkflowRules(line: String): Workflow {
    val stepDefinitions = line.substringAfter('{').substringBefore('}')
    return stepDefinitions
        .split(',')
        .map { clause ->
            when(':' in clause) {
                false -> AssignmentRule(clause)
                true -> {
                    val (field, rawPredicate, testValue, matchDestination) = conditionalAssignmentRegex.matchEntire(clause)!!.destructured
                    val predicate = when (rawPredicate) {
                        ">" -> GT
                        "<" -> LT
                        else -> throw IllegalStateException("Unexpected operator '$rawPredicate'")
                    }
                    ConditionalAssignmentRule(field.first(), predicate, testValue.toInt(), matchDestination)
                }
            }
        }
}