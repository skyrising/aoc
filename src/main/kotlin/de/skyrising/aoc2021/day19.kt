package de.skyrising.aoc2021

import de.skyrising.aoc.Vec3i

class BenchmarkDay19 : BenchmarkDayV1(19)

fun registerDay19() {
    val test = listOf(
        "--- scanner 0 ---",
        "404,-588,-901",
        "528,-643,409",
        "-838,591,734",
        "390,-675,-793",
        "-537,-823,-458",
        "-485,-357,347",
        "-345,-311,381",
        "-661,-816,-575",
        "-876,649,763",
        "-618,-824,-621",
        "553,345,-567",
        "474,580,667",
        "-447,-329,318",
        "-584,868,-557",
        "544,-627,-890",
        "564,392,-477",
        "455,729,728",
        "-892,524,684",
        "-689,845,-530",
        "423,-701,434",
        "7,-33,-71",
        "630,319,-379",
        "443,580,662",
        "-789,900,-551",
        "459,-707,401",
        "",
        "--- scanner 1 ---",
        "686,422,578",
        "605,423,415",
        "515,917,-361",
        "-336,658,858",
        "95,138,22",
        "-476,619,847",
        "-340,-569,-846",
        "567,-361,727",
        "-460,603,-452",
        "669,-402,600",
        "729,430,532",
        "-500,-761,534",
        "-322,571,750",
        "-466,-666,-811",
        "-429,-592,574",
        "-355,545,-477",
        "703,-491,-529",
        "-328,-685,520",
        "413,935,-424",
        "-391,539,-444",
        "586,-435,557",
        "-364,-763,-893",
        "807,-499,-711",
        "755,-354,-619",
        "553,889,-390",
        "",
        "--- scanner 2 ---",
        "649,640,665",
        "682,-795,504",
        "-784,533,-524",
        "-644,584,-595",
        "-588,-843,648",
        "-30,6,44",
        "-674,560,763",
        "500,723,-460",
        "609,671,-379",
        "-555,-800,653",
        "-675,-892,-343",
        "697,-426,-610",
        "578,704,681",
        "493,664,-388",
        "-671,-858,530",
        "-667,343,800",
        "571,-461,-707",
        "-138,-166,112",
        "-889,563,-600",
        "646,-828,498",
        "640,759,510",
        "-630,509,768",
        "-681,-892,-333",
        "673,-379,-804",
        "-742,-814,-386",
        "577,-820,562",
        "",
        "--- scanner 3 ---",
        "-589,542,597",
        "605,-692,669",
        "-500,565,-823",
        "-660,373,557",
        "-458,-679,-417",
        "-488,449,543",
        "-626,468,-788",
        "338,-750,-386",
        "528,-832,-391",
        "562,-778,733",
        "-938,-730,414",
        "543,643,-506",
        "-524,371,-870",
        "407,773,750",
        "-104,29,83",
        "378,-903,-323",
        "-778,-728,485",
        "426,699,580",
        "-438,-605,-362",
        "-469,-447,-387",
        "509,732,623",
        "647,635,-688",
        "-868,-804,481",
        "614,-800,639",
        "595,780,-596",
        "",
        "--- scanner 4 ---",
        "727,592,562",
        "-293,-554,779",
        "441,611,-461",
        "-714,465,-776",
        "-743,427,-804",
        "-660,-479,-426",
        "832,-632,460",
        "927,-485,-438",
        "408,393,-506",
        "466,436,-512",
        "110,16,151",
        "-258,-428,682",
        "-393,719,612",
        "-211,-452,876",
        "808,-476,-593",
        "-575,615,604",
        "-485,667,467",
        "-680,325,-822",
        "-627,-443,-432",
        "872,-547,-609",
        "833,512,582",
        "807,604,487",
        "839,-516,451",
        "891,-625,532",
        "-652,-548,-490",
        "30,-46,-14"
    )
    puzzleLS(19, "Beacon Scanner") {
        solve(parseInput19(it)).first.size
    }
    puzzleLS(19, "Part Two") {
        val (_, scanners) = solve(parseInput19(it))
        scanners.toList().pairs().maxByOrNull { (a, b) -> a.manhattanDistance(b) }?.also(::println)?.let { (a, b) -> a.manhattanDistance(b) }
    }
}

private fun solve(scanners: List<PointCloud>): Pair<PointCloud, Array<Vec3i>> {
    val beacons = mutableSetOf<Vec3i>()
    beacons.addAll(scanners[0])
    val p = PointCloud(beacons)
    val undetermined = ArrayDeque((1 until scanners.size).toList())
    var steps = scanners.size * 3
    val offsets = Array(scanners.size) { Vec3i(0, 0, 0) }
    while (undetermined.isNotEmpty()) {
        if (steps-- == 0) break
        val next = undetermined.removeFirst()
        val m = match(p, scanners[next])
        if (m == null) {
            undetermined.add(next)
            continue
        }
        //println("$next: ${m.first}")
        offsets[next] = m.first
        beacons.addAll(m.second)
    }
    //println("${beacons.size}, $undetermined")
    return Pair(PointCloud(beacons), offsets)
}

fun match(a: PointCloud, b: PointCloud, min: Int = 12): Pair<Vec3i, PointCloud>? {
    for (r in b.rotations) {
        for (pb in r) {
            for (pa in a) {
                val offset = pa - pb
                val c = r.offset(offset)
                val o = a.overlap(c)
                if (o >= min) return Pair(offset, c)
            }
        }
    }
    return null
}

private fun parseInput19(input: List<String>): List<PointCloud> {
    val result = mutableListOf<PointCloud>()
    var points = mutableSetOf<Vec3i>()
    var i = 1
    while (i < input.size) {
        val line = input[i++]
        if (line.isEmpty()) {
            result.add(PointCloud(points))
            points = mutableSetOf()
            i++
            continue
        }
        val (x, y, z) = line.split(',').map(String::toInt)
        points.add(Vec3i(x, y, z))
    }
    result.add(PointCloud(points))
    return result
}

data class PointCloud(val points: Set<Vec3i>) : Set<Vec3i> by points {
    val rotations: List<PointCloud> get() {
        return Rotation.ROTATIONS.map { it.apply(this) }
    }

    fun map(fn: (Vec3i) -> Vec3i) = PointCloud(mapTo(HashSet(size), fn))

    fun offset(off: Vec3i) = map { it + off }

    operator fun plus(v: Vec3i) = map { it + v }

    fun closest(to: Vec3i) = minByOrNull { it.distanceSq(to) } ?: throw NoSuchElementException()

    fun overlap(other: PointCloud) = count { it in other }
    fun overlapping(other: PointCloud) = PointCloud(filterTo(mutableSetOf()) { it in other })

    override fun toString() = "PointCloud(${points.joinToString()})"
}
data class Rotation(val x: Int, val y: Int, val z: Int) {
    fun apply(v: Vec3i) = Vec3i(getComponent(v, x), getComponent(v, y), getComponent(v, z))
    fun apply(p: PointCloud): PointCloud = p.map(this::apply)

    companion object {
        private const val X = 1
        private const val Y = 2
        private const val Z = 3
        val ROTATIONS = arrayOf(
            Rotation(+X, +Y, +Z), Rotation(+X, -Y, -Z), Rotation(+X, +Z, -Y), Rotation(+X, -Z, +Y),
            Rotation(-X, +Y, -Z), Rotation(-X, -Y, +Z), Rotation(-X, +Z, +Y), Rotation(-X, -Z, -Y),
            Rotation(+Y, +X, -Z), Rotation(+Y, -X, +Z), Rotation(+Y, +Z, +X), Rotation(+Y, -Z, -X),
            Rotation(-Y, +X, +Z), Rotation(-Y, -X, -Z), Rotation(-Y, +Z, -X), Rotation(-Y, -Z, +X),
            Rotation(+Z, +X, +Y), Rotation(+Z, -X, -Y), Rotation(+Z, +Y, -X), Rotation(+Z, -Y, +X),
            Rotation(-Z, +X, -Y), Rotation(-Z, -X, +Y), Rotation(-Z, +Y, +X), Rotation(-Z, -Y, -X),
        )

        private fun getComponent(v: Vec3i, c: Int) = if (c < 0) -v[-c -1] else v[c - 1]
    }
}