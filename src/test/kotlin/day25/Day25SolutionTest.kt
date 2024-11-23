package day25

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.junit.jupiter.api.Test

class Day25SolutionTest {
    private val sampleInput = """
        jqt: rhn xhk nvd
        rsh: frs pzl lsr
        xhk: hfx
        cmg: qnr nvd lhk bvb
        rhn: xhk bvb hfx
        bvb: xhk hfx
        pzl: lsr hfx nvd
        qnr: nvd
        ntq: jqt hfx bvb xhk
        nvd: lhk
        lsr: lhk
        rzs: qnr cmg lsr rsh
        frs: qnr lhk lsr
    """.trimIndent().lines()

    private val solution = Day25Solution()

    @Test
    fun `part1 should parse the graph, make a min cut, and return the product of the component graph sizes`() {
        assert(solution.part1(sampleInput) == 54)
    }

    @Test
    fun `parse should return a graph containing all vertices specified in the input`() {
        val expectedVertices = sampleInput.flatMap { it.split(':', ' ') }.filter { it.isNotBlank() }.toSet()

        val graph: Graph<String, DefaultEdge> = parse(sampleInput)

        assert(graph.vertexSet() == expectedVertices)
    }

    @Test
    fun `parse should return a graph containing all the edges specified in the input`() {
        val graph = parse(sampleInput)

        assert(graph.containsEdge("qnr", "nvd"))
        sampleInput.forEach { line ->
            val sourceVertex = line.substringBefore(':')
            val destinationVertices = line.substringAfter(": ").split(' ')
            destinationVertices.forEach { destination -> assert(graph.containsEdge(sourceVertex, destination)) }
        }
    }

    @Test
    fun `parse should return an undirected graph`() {
        val graph = parse(sampleInput)

        assert(graph.containsEdge("qnr", "nvd"))
        assert(graph.containsEdge("nvd", "qnr"))
        assert(graph.containsEdge("lsr", "rzs"))
    }

    @Test
    fun `cut should return the size of the two component graphs created by the min cut, given a graph`() {
        val graph = parse(sampleInput)

        val componentSizes = cut(graph)

        assert(componentSizes == setOf(6,9))
    }
}