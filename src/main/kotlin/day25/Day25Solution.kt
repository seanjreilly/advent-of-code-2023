package day25

import org.jgrapht.Graph
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import utils.IntSolution

fun main() = Day25Solution().run()
class Day25Solution : IntSolution() {
    override fun part1(input: List<String>) = cut(parse(input)).reduce(Int::times)
    override fun part2(input: List<String>) = 0
}

internal fun parse(input: List<String>): Graph<String, DefaultEdge> {
    val graph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    input
        .map { line -> line.substringBefore(":") to line.substringAfter(": ").split(" ")}
        .flatMap { it.second.map { v -> it.first to v } }
        .filter { it.second.isNotBlank() }
        .forEach { (source, destination) ->
            graph.addVertex(source)
            graph.addVertex(destination)
            graph.addEdge(source, destination)
        }
    return graph
}

internal fun cut(graph: Graph<String, DefaultEdge>): Set<Int> {
    val cutAlgorithm = StoerWagnerMinimumCut(graph)
    val minCut = cutAlgorithm.minCut()
    val componentGraphs = setOf(minCut, (graph.vertexSet() - minCut))
    return componentGraphs.map { it.size }.toSet()
}