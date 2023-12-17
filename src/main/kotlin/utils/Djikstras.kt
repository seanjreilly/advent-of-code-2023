package utils

import java.util.*

fun <N>  djikstras(nodes: Set<N>, vararg startNodes: N, neighboursMapping: (N) -> Collection<Pair<N, Int>>) : Map<N, Int> {

    val tentativeDistances = nodes.associateWith { Int.MAX_VALUE }.toMutableMap()

    startNodes.forEach { tentativeDistances[it] = 0 }

    val unvisitedNodes = PriorityQueue<Pair<N, Int>>(compareBy { it.second })
    unvisitedNodes += tentativeDistances.map { (node, distance) -> node to distance }
//    tentativeDistances.forEach { (node, distance) ->
//        unvisitedNodes.add(Pair(node, distance))
//    }

    val visitedNodes = mutableSetOf<N>()

    while (unvisitedNodes.isNotEmpty()) {
        val currentNode = unvisitedNodes.remove().first

        //do an extra filter to remove the duplicate entries from the priority queue (see below)
        if (currentNode in visitedNodes) {
            continue
        }

        visitedNodes += currentNode

        val distanceToCurrentNode = tentativeDistances[currentNode]!!
        if (distanceToCurrentNode == Int.MAX_VALUE) {
            break //we've reached an unreachable point
        }

        val neighbours = neighboursMapping(currentNode)
        neighbours
            .filter { it.first !in visitedNodes }
            .forEach { (newNode, transitionCost) ->
                val currentCostToNode = tentativeDistances[newNode]!!
                val altDistance = distanceToCurrentNode + transitionCost
                if (altDistance < currentCostToNode) { //filter out more expensive paths
                    tentativeDistances[newNode] = altDistance
                    unvisitedNodes.add(newNode to altDistance) //don't remove the old point (slow), just leave a duplicate entry
                }
            }
    }
    return tentativeDistances
}