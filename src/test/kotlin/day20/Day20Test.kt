package day20

import day20.PulseType.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day20Test {
    private val sampleInput = """
        broadcaster -> a, b, c
        %a -> b
        %b -> c
        %c -> inv
        &inv -> a
    """.trimIndent().lines()

    private val moreInterestingSampleInput = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent().lines()

    @Test
    fun `part1 should construct a CommunicationSystem, press the button 1000 times, and return the product of low and high pulses counted`() {
        assert(part1(sampleInput) == 32000000L)
    }

    @Nested
    inner class FlipFlopModuleTest {

        @Test
        fun `constructor should create a module with off state`() {
            val module = FlipFlopModule(IRRELEVANT_MODULE_NAME, irrelevantRecipients())

            assert(!module.isOn)
        }

        @Test
        @Suppress("USELESS_IS_CHECK")
        fun `class should implement Module`() {
            val module:ElfModule = FlipFlopModule(IRRELEVANT_MODULE_NAME, irrelevantRecipients())
            assert(module is ElfModule )
        }

        @Test
        fun `handlePulse should do nothing when a high pulse is received`() {
            val module = FlipFlopModule(IRRELEVANT_MODULE_NAME, irrelevantRecipients())
            val startingModuleState = module.isOn

            val outputPulses: List<Pulse> = module.handlePulse(pulse(HIGH))

            assert(outputPulses.isEmpty())
            assert(module.isOn == startingModuleState)
        }

        @Test
        fun `handlePulse should turn state on and send a high pulse to each recipient when a low pulse is received and state is off`() {
            val module = FlipFlopModule("theModule", listOf("a", "b"))
            val expectedPulses = listOf(
                pulse(module.name, HIGH, "a"),
                pulse(module.name, HIGH, "b")
            )
            assert(!module.isOn) { "precondition: state must be off before pulse is received" }

            val outputPulses: List<Pulse> = module.handlePulse(pulse(LOW))

            assert(outputPulses == expectedPulses)
            assert(module.isOn)
        }

        @Test
        fun `handlePulse should turn state off and send a low pulse when a low pulse is received and state is on`() {
            val module = FlipFlopModule("theModule", listOf("b", "c"))
            module.isOn = true
            val expectedPulses = listOf(
                pulse(module.name, LOW, "b"),
                pulse(module.name, LOW, "c")
            )

            val outputPulses: List<Pulse> = module.handlePulse(pulse(LOW))
            assert(outputPulses == expectedPulses)
            assert(!module.isOn)
        }
    }

    @Nested
    inner class ConjunctionModuleTest {

        @Test
        fun `constructor should default state to having received a low pulse from each input connector `() {
            val senders = irrelevantSenders()
            assert(senders.isNotEmpty()) { "precondition: at least one sender" }
            val expectedModuleState = mutableMapOf<String, PulseType>()
            senders.forEach { expectedModuleState[it] = LOW }

            val module = ConjunctionModule(senders, IRRELEVANT_MODULE_NAME, irrelevantRecipients())

            assert(module.state == (expectedModuleState as Map<String, PulseType>))
        }

        @Test
        @Suppress("USELESS_IS_CHECK")
        fun `class should implement Module`() {
            val module:ElfModule = ConjunctionModule(irrelevantSenders(), IRRELEVANT_MODULE_NAME, irrelevantRecipients())
            assert(module is ElfModule )
        }

        @Test
        fun `handlePulse should send a low pulse to each receiver when it remembers high pulses for all inputs`() {
            val sender = "sA"
            val senders = setOf(sender, "sB", "sC")
            val receivers = listOf("rA", "rB")
            val module = ConjunctionModule(senders, "theModule", receivers)
            module.state = senders.associateWith { HIGH }

            val expectedPulses = listOf(
                pulse(module.name, LOW, "rA"),
                pulse(module.name, LOW, "rB")
            )

            val outputPulses = module.handlePulse(pulse(sender, HIGH, module.name))

            assert(outputPulses == expectedPulses)
            assert(module.state == senders.associateWith { HIGH })
        }

        @Test
        fun `handlePulse should send a high pulse when it remembers a low pulse for any input`() {
            val sender = "sA"
            val senders = setOf(sender, "sB", "sC")
            val receivers = listOf("rA", "rB")
            val module = ConjunctionModule(senders, "theModule", receivers)
            module.state = mapOf("sA" to HIGH, "sB" to LOW, "sC" to LOW)

            val expectedPulses = listOf(
                pulse(module.name, HIGH, "rA"),
                pulse(module.name, HIGH, "rB")
            )

            val outputPulses = module.handlePulse(pulse(sender, LOW, module.name))

            assert(outputPulses == expectedPulses)
            assert(module.state.all { it.value == LOW })
        }

        @Test
        fun `handlePulse should update state when receiving a pulse before sending output`() {
            val sender = "sA"
            val senders = setOf(sender, "sB", "sC")
            val receivers = listOf("rA", "rB")
            val module = ConjunctionModule(senders, "theModule", receivers)
            module.state = senders.associateWith { LOW }

            val expectedPulses = listOf(
                pulse(module.name, HIGH, "rA"),
                pulse(module.name, HIGH, "rB")
            )

            val outputPulses = module.handlePulse(pulse(sender, HIGH, module.name))

            assert(outputPulses == expectedPulses)
            assert(module.state == mapOf("sA" to HIGH, "sB" to LOW, "sC" to LOW))
        }
    }

    @Nested
    inner class BroadcasterModuleTest {
        @Test
        @Suppress("USELESS_IS_CHECK")
        fun `class should implement ElfModule`() {
            val module:ElfModule = BroadcasterModule(irrelevantRecipients())
            assert(module is ElfModule )
        }

        @Test
        fun `handlePulse should send a low pulse to every recipieint given a low pulse`() {
            val module = BroadcasterModule(listOf("rA", "rB", "rC"))
            val expectedOutput = listOf(
                pulse("broadcaster", LOW, "rA"),
                pulse("broadcaster", LOW, "rB"),
                pulse("broadcaster", LOW, "rC")
            )

            val output = module.handlePulse(pulse(LOW))

            assert(output == expectedOutput)
        }

        @Test
        fun `handlePulse should send a high pulse to every recipient given a high pulse`() {
            val module = BroadcasterModule(listOf("rA", "rB", "rC"))
            val expectedOutput = listOf(
                pulse("broadcaster", HIGH, "rA"),
                pulse("broadcaster", HIGH, "rB"),
                pulse("broadcaster", HIGH, "rC")
            )

            val output = module.handlePulse(pulse(HIGH))

            assert(output == expectedOutput)
        }
    }

    @Test
    fun `parse should return a configured CommunicationSystem given input`() {
        val communicationSystem: CommunicationSystem = parse(sampleInput)

        assert(communicationSystem.modules.size == 5)

        val broadcaster = communicationSystem.modules["broadcaster"]!!
        assert(broadcaster == BroadcasterModule(listOf("a", "b", "c")))

        val flipFlopA = communicationSystem.modules["a"]!!
        assert(flipFlopA == FlipFlopModule("a", listOf("b")))

        val flipFlopB = communicationSystem.modules["b"]!!
        assert(flipFlopB == FlipFlopModule("b", listOf("c")))

        val flipFlopC = communicationSystem.modules["c"]!!
        assert(flipFlopC == FlipFlopModule("c", listOf("inv")))

        val conjunctionInv = communicationSystem.modules["inv"]!!
        assert(conjunctionInv == ConjunctionModule(setOf("c"), "inv", listOf("a")))
    }

    @Nested
    inner class CommunicationSystemTest {
        @Test
        fun `pushButton should track the number of high and low pulses sent`() {
            val communicationSystem = parse(sampleInput)

            val pulseCounts = communicationSystem.pushButton(1)
            assert(pulseCounts.lowPulses == 8L)
            assert(pulseCounts.highPulses == 4L)
        }

        @Test
        fun `pushButton should send a low pulse to the broadcaster module when the button is pressed`() {
            val modules = mapOf("broadcaster" to BroadcasterModule(emptyList()))
            val communicationSystem = CommunicationSystem(modules)

            val result = communicationSystem.pushButton(1)

            assert(result.lowPulses == 1L)
            assert(result.highPulses == 0L)
        }

        @Test
        fun `pushButton should count pulses sent to non-existent modules but otherwise ignore them`() {
            val modules = mapOf("broadcaster" to BroadcasterModule(listOf("a", "b", "c")))
            val communicationSystem = CommunicationSystem(modules)

            val result = communicationSystem.pushButton(1)

            assert(result.lowPulses == 4L)
            assert(result.highPulses == 0L)
        }

        @Test
        fun `pushButton should handle multiple button presses correctly`() {
            val communicationSystem = parse(sampleInput)

            val pulseCounts = communicationSystem.pushButton(1000)
            assert(pulseCounts.lowPulses == 8000L)
            assert(pulseCounts.highPulses == 4000L)
        }

        @Test
        fun `second example integration test`() {
            val communicationSystem = parse(moreInterestingSampleInput)

            val pulseCounts = communicationSystem.pushButton(1000)
            assert(pulseCounts.lowPulses == 4250L)
            assert(pulseCounts.highPulses == 2750L)
        }
    }

    companion object {
        private const val IRRELEVANT_SENDER = "foo"
        private const val IRRELEVANT_MODULE_NAME = "someRandomModule"
        private const val IRRELEVANT_RECIPIENT = "bar"
        private fun irrelevantRecipients() = listOf("rA", "rB", "rC")
        private fun irrelevantSenders() =  setOf("sA", "sB", "sC")
        private fun pulse(type: PulseType) = Pulse(IRRELEVANT_SENDER, type, IRRELEVANT_RECIPIENT)
        private fun pulse(source: String, type: PulseType, destination: String): Pulse = Pulse(source, type, destination)
    }
}