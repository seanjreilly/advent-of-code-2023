package day20

import day20.PulseType.*
import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day20")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val (lowPulses, highPulses) = parse(input).pushButton(1000)
    return lowPulses * highPulses
}

fun part2(input: List<String>): Long {
    return 0
}

internal enum class PulseType { HIGH, LOW }
internal data class Pulse(val source: String, val type: PulseType, val destination: String)

/*
Called ElfModule to avoid a clash with java.lang.Module
 */
internal sealed interface ElfModule {
    fun handlePulse(pulse: Pulse): List<Pulse>
    val name: String
    val recipients: List<String>
}

internal data class FlipFlopModule(override val name: String, override val recipients: List<String>) : ElfModule {
    var isOn: Boolean = false
    override fun handlePulse(pulse: Pulse): List<Pulse> {
        return when (pulse.type) {
            HIGH -> emptyList()
            LOW -> {
                isOn = !isOn
                val outputPulseType = if (isOn) { HIGH } else { LOW }
                recipients.map { Pulse(name, outputPulseType, it) }
            }
        }
    }
}

internal data class ConjunctionModule(val senders: Set<String>, override val name: String, override val recipients: List<String>) :
    ElfModule {
    var state: Map<String, PulseType> = senders.associateWith { LOW }
    override fun handlePulse(pulse: Pulse): List<Pulse> {
        state += pulse.source to  pulse.type
        val newPulseType = if (state.values.all { it == HIGH }) { LOW } else { HIGH }
        return recipients.map { Pulse(name, newPulseType, it) }
    }
}

internal data class BroadcasterModule(override val recipients: List<String>) : ElfModule {
    override fun handlePulse(pulse: Pulse): List<Pulse> {
        return recipients.map { Pulse(name, pulse.type, it) }
    }
    override val name = "broadcaster"
}

internal class CommunicationSystem(val modules: Map<String, ElfModule>) {
    fun pushButton(pushes: Int): PulseCounts {
        var lowPulses = 0L
        var highPulses = 0L
        val queue = ArrayDeque<Pulse>()
        repeat(pushes) {
            queue.addLast(Pulse("", LOW, "broadcaster")) //the pulse sent by the button push itself
            while (queue.isNotEmpty()) {
                val pulse = queue.removeFirst()
                when(pulse.type) {
                    LOW -> lowPulses++
                    else -> highPulses++
                }
                val outputPulses = modules[pulse.destination]?.handlePulse(pulse) ?: emptyList()
                queue.addAll(outputPulses)
            }
        }
        return PulseCounts(lowPulses, highPulses)
    }

}

data class PulseCounts(val lowPulses: Long, val highPulses: Long)

internal fun parse(input: List<String>): CommunicationSystem {
    //build a reverse map to build inputs for conjunction modules
    val reverseInputMap = input
        .map { it.replace("&", "").replace("%", "") }
        .map { it.substringAfter("-> ").split(", ") to it.substringBefore(" ->") }
        .flatMap { it.first.map { v -> v to it.second } }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.toSet() }

    val modules = input
        .map { it.substringBefore(" -> ") to it.substringAfter(" -> ") }
        .map { it.first to it.second.split(", ") }
        .map {
            val (type, name) = if (it.first.first() in "%&") { it.first.first() to it.first.drop(1) } else { 'B' to it.first }
            Triple(type, name, it.second)
        }
        .map {(type, name, recipients) ->
            name to when(type) {
                '%' -> FlipFlopModule(name, recipients)
                '&' -> ConjunctionModule(reverseInputMap[name]!!, name, recipients)
                else -> BroadcasterModule(recipients)
            }
        }
        .toMap()

    return CommunicationSystem(modules)
}