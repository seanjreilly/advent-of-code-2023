package utils

fun parseGridWithPoints(input: List<String>) : Pair<Bounds, Sequence<Pair<Point, Char>>> {
    val bounds = Bounds(input.first().indices, input.indices)
    val sequence = input
        .asSequence()
        .flatMapIndexed { y, line ->
            require(line.length == input.first().length) { "input must be rectangular" }
            line.mapIndexed { x, char -> Point(x,y) to char }
        }
    return bounds to sequence
}