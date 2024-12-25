@file:PuzzleName("Not Enough Minerals")

package de.skyrising.aoc2022.day19

import de.skyrising.aoc.*

private data class RobotState(val time: Int, val oreBots: Int, val ore: Int, val clayBots: Int, val clay: Int, val obsidianBots: Int, val obsidian: Int, val geodeBots: Int, val geode: Int) {
    val timeLeft get() = 24 - time

    fun canCraftOre(bp: RobotBlueprint) = ore >= bp.oreOre
    fun shouldCraftOre(bp: RobotBlueprint) = oreBots < bp.maxOre && timeLeft > bp.oreOre
    fun craftOre(bp: RobotBlueprint) = copy(ore = ore - bp.oreOre).step().copy(oreBots = oreBots + 1)

    fun canCraftClay(bp: RobotBlueprint) = ore >= bp.clayOre
    fun shouldCraftClay(bp: RobotBlueprint) = clayBots < bp.maxClay
    fun craftClay(bp: RobotBlueprint) = copy(ore = ore - bp.clayOre).step().copy(clayBots = clayBots + 1)

    fun canCraftObsidian(bp: RobotBlueprint) = ore >= bp.obsidianOre && clay >= bp.obsidianClay
    fun shouldCraftObsidian(bp: RobotBlueprint) = obsidianBots < bp.maxObsidian
    fun craftObsidian(bp: RobotBlueprint) = copy(ore = ore - bp.obsidianOre, clay = clay - bp.obsidianClay).step().copy(obsidianBots = obsidianBots + 1)

    fun canCraftGeode(bp: RobotBlueprint) = ore >= bp.geodeOre && obsidian >= bp.geodeObsidian
    fun craftGeode(bp: RobotBlueprint) = copy(ore = ore - bp.geodeOre, obsidian = obsidian - bp.geodeObsidian).step().copy(geodeBots = geodeBots + 1)

    fun step() = RobotState(time + 1, oreBots, ore + oreBots, clayBots, clay + clayBots, obsidianBots, obsidian + obsidianBots, geodeBots, geode + geodeBots)
}

private data class RobotBlueprint(val id: Int, val oreOre: Int, val clayOre: Int, val obsidianOre: Int, val obsidianClay: Int, val geodeOre: Int, val geodeObsidian: Int) {
    val maxOre get() = maxOf(oreOre, clayOre, obsidianOre, geodeOre)
    val maxClay get() = obsidianClay
    val maxObsidian get() = geodeObsidian

    fun getStates(time: Int): Set<RobotState> {
        var states = setOf(RobotState(0, 1, 0, 0, 0, 0, 0, 0, 0))
        for (t in 1..time) {
            val next = mutableSetOf<RobotState>()
            for (s in states) {
                if (s.canCraftOre(this) && s.shouldCraftOre(this)) next += s.craftOre(this)
                if (s.canCraftClay(this) && s.shouldCraftClay(this)) next += s.craftClay(this)
                if (s.canCraftObsidian(this) && s.shouldCraftObsidian(this)) next += s.craftObsidian(this)
                next += if (s.canCraftGeode(this)) s.craftGeode(this) else s.step()
            }
            states = next
            println("$t: ${states.size}")
        }
        return states
    }

    fun qualityLevel(): Int {
        val states = getStates(24)
        return states.maxOf(RobotState::geode) * id
    }
}

private fun parseInput(input: PuzzleInput) = input.lines.map { val (a, b, c, d, e, f, g) = it.ints(); RobotBlueprint(a, b, c, d, e, f, g) }

val test = TestInput("""
    Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
""")

fun PuzzleInput.part1() = parseInput(this).sumOf(RobotBlueprint::qualityLevel)
fun PuzzleInput.part2() = parseInput(this).take(3).map { it.getStates(32).maxOf(RobotState::geode) }.reduce(Int::times)
