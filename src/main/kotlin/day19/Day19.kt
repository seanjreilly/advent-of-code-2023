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
    val ruleMappings = parseRawWorkflowRules(input)

    // find paths through the workflow to acceptance with a recursive DFS
    fun recursiveSearch(rules: List<String>, remainingRange: XmasRange): List<XmasRange> {
        val firstRule = rules.first()
        val ruleTail = rules.drop(1)

        return when (firstRule) {
            "R" -> emptyList() //base case
            "A" -> listOf(remainingRange) //base case
            in Regex(""".+?:.+?""") -> {
                //conditional expressions create 2 possibilities
                val expression = firstRule.substringBefore(":")
                //first case: the condition is matched
                val newLabel = firstRule.substringAfter(':')
                val theBranchIsTaken = recursiveSearch(listOf(newLabel), remainingRange.apply(expression))

                //second case: the condition is not matched
                val theBranchIsNotTaken = recursiveSearch(ruleTail, remainingRange.apply(invertExpression(expression)))

                theBranchIsTaken + theBranchIsNotTaken
            }
            else -> recursiveSearch(ruleMappings[firstRule]!!, remainingRange) //non-conditional match
        }
    }

    return recursiveSearch(ruleMappings["in"]!!, XmasRange()).sumOf { it.product() }
}

private data class XmasRange(val ranges: Map<Char, IntRange>) {
    constructor() : this("xmas".toCharArray().associateWith { 1..4000 })
    fun product() = ranges.values.map { it.count().toLong() }.reduce(Long::times)
    fun apply(expression: String): XmasRange {
        val key = expression[0]
        val operation = expression[1]
        val newEndpoint = expression.drop(2).toInt()
        val oldRange = this.ranges[key]!!
        val newRange = when (operation) {
            '<' -> oldRange.first until newEndpoint
            else -> newEndpoint + 1..oldRange.last
        }
        return XmasRange(this.ranges + (key to newRange))
    }
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
    return parseRawWorkflowRules(input).mapValues { entry -> entry.value.map { parseClause(it) } }
}

internal fun parseRawWorkflowRules(input: List<String>) : Map<String, List<String>> {
    return input
        .asSequence()
        .takeWhile { it.isNotBlank() }
        .map { line -> line.substringBefore('{') to line }
        .map { it.first to it.second.substringAfter('{').substringBefore('}') }
        .map { it.first to it.second.split(',') }
        .associate { it }
}

private val conditionalAssignmentRegex = """(.)([<>])(\d+):(.+)""".toRegex()
private fun parseClause(clause: String) = when (val match = conditionalAssignmentRegex.matchEntire(clause)) {
    null -> AssignmentRule(clause)
    else -> {
        val (field, rawPredicate, testValue, matchDestination) = match.destructured
        val predicate = when (rawPredicate) {
            ">" -> GT
            "<" -> LT
            else -> throw IllegalStateException("Unexpected operator '$rawPredicate'")
        }
        ConditionalAssignmentRule(field.first(), predicate, testValue.toInt(), matchDestination)
    }
}

internal fun invertExpression(expression: String): String {
    val field = expression[0]
    val op = expression[1]
    val amount = expression.drop(2).toInt()
    return when (op) {
        '>' -> "$field<${amount + 1}"
        else -> "$field>${amount - 1}"
    }
}

private operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text) //lets us use in with a regex in a where statement