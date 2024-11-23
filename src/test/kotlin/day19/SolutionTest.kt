package day19

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SolutionTest {
    private val sampleInput = """
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}

        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """.trimIndent().lines()

    private val solution = Solution()

    @Test
    fun `part1 should analyse each part and return the sum of the total rating for each accepted part`() {
        assert(solution.part1(sampleInput) == 19114L)
    }

    @Test
    fun `part2 should calculate how many distinct combinations of ratings will be accepted`() {
        assert(solution.part2(sampleInput) == 167409079868000L)
    }

    @Test
    fun `parseParts should return a list of parts`() {
        val parts = parseParts(sampleInput)

        assert(parts.size == 5)
        assert(parts[0] == Part(787, 2655, 1222, 2876))
        assert(parts[1] == Part(1679, 44, 2067, 496))
        assert(parts[2] == Part(2036, 264, 79, 2244))
        assert(parts[3] == Part(2461, 1339, 466, 291))
        assert(parts[4] == Part(2127, 1623, 2188, 1013))
    }
    
    @Test
    fun `parseWorkflows should return a map of labels to workflows given workflow descriptions`() {
        val workflowSystem: Map<String, Workflow> = parseWorkflows(sampleInput)

        assert(workflowSystem.size == 11)
        val px = workflowSystem["px"]!!
        val expectedPx = listOf(
            ConditionalAssignmentRule('a', LT, 2006, "qkq"),
            ConditionalAssignmentRule('m', GT, 2090, "A"),
            AssignmentRule("rfg")
        )

        assert(px == expectedPx)
    }

    @Nested
    inner class ConditionalAssignmentRuleTest {
        @Test
        fun `nextLabel should compare the selected field and return a label if the test passes`() {
            val rule = ConditionalAssignmentRule('a', GT, 9000, "qkq")
            val goku = Part(1, 2, 9001, 4)
            assert(rule.nextLabel(goku) == "qkq")
        }

        @Test
        fun `nextLabel should compare the selected field and return null if the test fails`() {
            val rule = ConditionalAssignmentRule('a', GT, 9000, "qkq")
            val vegeta = Part(1, 2, 8000, 4)
            assert(rule.nextLabel(vegeta) == null)
        }
    }

    @Nested
    inner class PartTest {
        @Test
        fun `get should return the x value given 'x'`() {
            val part = Part(1,2,3,4)
            assert(part['x'] == 1)
        }

        @Test
        fun `get should return the m value given 'm'`() {
            val part = Part(1,2,3,4)
            assert(part['m'] == 2)
        }

        @Test
        fun `get should return the a value given 'a'`() {
            val part = Part(1,2,3,4)
            assert(part['a'] == 3)
        }

        @Test
        fun `get should return the s value given 's'`() {
            val part = Part(1,2,3,4)
            assert(part['s'] == 4)
        }
    }

    @Test
    fun `testPart should return true given the same workflows and the first sample part`() {
        val workflows = parseWorkflows(sampleInput)
        val part = parseParts(sampleInput)[0]

        assert(testPart(part, workflows))
    }

    @Test
    fun `testPart should return false given the same workflows and the second sample part`() {
        val workflows = parseWorkflows(sampleInput)
        val part = parseParts(sampleInput)[1]

        assert(!testPart(part, workflows))
    }

    @Test
    fun `testPart should return true given the same workflows and the third sample part`() {
        val workflows = parseWorkflows(sampleInput)
        val part = parseParts(sampleInput)[2]

        assert(testPart(part, workflows))
    }

    @Test
    fun `testPart should return false given the same workflows and the fourth sample part`() {
        val workflows = parseWorkflows(sampleInput)
        val part = parseParts(sampleInput)[3]

        assert(!testPart(part, workflows))
    }

    @Test
    fun `testPart should return true given the same workflows and the fifth sample part`() {
        val workflows = parseWorkflows(sampleInput)
        val part = parseParts(sampleInput)[4]

        assert(testPart(part, workflows))
    }

    @Test
    fun `invertExpression should preserve the field name and add one to the value when inverting greater than`() {
        val expression = "a>9000"
        assert(invertExpression(expression) == "a<9001")
    }

    @Test
    fun `invertExpression should preserve the field name and subtract one from the value when inverting less than`() {
        val expression = "s<1000"
        assert(invertExpression(expression) == "s>999")
    }
}