package utils

fun String.permutations(): List<String> {
    val permutations = mutableSetOf<String>()
    generatePermutationsHelper(this.toCharArray(), 0, permutations)
    return permutations.toList()
}

private fun generatePermutationsHelper(input: CharArray, index: Int, permutations: MutableSet<String>) {
    if (index == input.size - 1) {
        permutations.add(String(input))
        return
    }

    for (i in index until input.size) {
        swap(input, index, i)
        generatePermutationsHelper(input, index + 1, permutations)
        swap(input, index, i) // Backtracking step
    }
}

private fun swap(input: CharArray, i: Int, j: Int) {
    val temp = input[i]
    input[i] = input[j]
    input[j] = temp
}
