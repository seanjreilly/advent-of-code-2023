package utils

import java.util.*

fun <N> djikstras(vararg startNodes: N, costLimit: Int = Int.MAX_VALUE, neighboursMapping: (N) -> Collection<Pair<N, Int>>) : Map<N, Int> {

    val tentativeDistances = mutableMapOf<N, Int>()
    val unvisitedNodes = PriorityQueue<Pair<N, Int>>(compareBy { it.second })

    /*
        Djikstra's usually features just one start node, but you can occasionally have more,
        such as when modelling nodes as point + direction and you're allowed to enter the maze
        facing whichever way you want.
     */
    startNodes.forEach { tentativeDistances[it] = 0 }
    unvisitedNodes += startNodes.map{ node -> node to 0 } //add the start nodes to the priority queue to kick things off

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
                if (altDistance < currentCostToNode && altDistance <= costLimit) { //filter out more expensive paths
                    tentativeDistances[newNode] = altDistance
                    unvisitedNodes.add(newNode to altDistance) //don't remove the old point (slow), just leave a duplicate entry
                }
            }
    }
    return tentativeDistances
}