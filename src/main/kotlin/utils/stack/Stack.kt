package utils.stack

/**
 * A type alias and some extension functions to give ArrayDeque "stack-like" method names
 * This file lives in its own package so that import utils.stack.* provides all the
 * extension functions as well
 */
typealias Stack<T> = ArrayDeque<T>

fun <T> Stack<T>.push(element: T) = addLast(element)
fun <T> Stack<T>.pop() = removeLast()
fun <T> Stack<T>.peek() = this.last()