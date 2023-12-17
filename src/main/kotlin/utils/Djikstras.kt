package utils

import java.util.*

fun <N> djikstras(nodes: Set<N>, vararg startNodes: N, neighboursMapping: (N) -> Collection<Pair<N, Int>>) : Map<N, Int> {

    val tentativeDistances = mutableMapOf<N, Int>()
    startNodes.forEach { tentativeDistances[it] = 0 }

    val unvisitedNodes = PriorityQueue<Pair<N, Int>>(compareBy { it.second })
    unvisitedNodes += nodes.map { node -> node to (tentativeDistances[node] ?: Int.MAX_VALUE) }

    val visitedNodes = mutableSetOf<N>()

    while (unvisitedNodes.isNotEmpty()) {
        val currentNode = unvisitedNodes.remove().first

        //do an extra filter to remove the duplicate entries from the priority queue (see below)
        if (currentNode in visitedNodes) {
            continue
        }

        visitedNodes += currentNode

        val distanceToCurrentNode = tentativeDistances[currentNode] ?: break //stop if we've reached an unreachable point

        val neighbours = neighboursMapping(currentNode)
        neighbours
            .filter { it.first !in visitedNodes }
            .forEach { (newNode, transitionCost) ->
                val currentCostToNode = tentativeDistances[newNode] ?: Int.MAX_VALUE
                val altDistance = distanceToCurrentNode + transitionCost
                if (altDistance < currentCostToNode) { //filter out more expensive paths
                    tentativeDistances[newNode] = altDistance
                    unvisitedNodes.add(newNode to altDistance) //don't remove the old point (slow), just leave a duplicate entry
                }
            }
    }
    return tentativeDistances
}