package de.skyrising.aoc

class RopeString(val left: CharSequence, val right: CharSequence): CharSequence {
    override val length = left.length + right.length
    private val hashCode by lazy {
        var result = left.hashCode()
        repeat(right.length) {
            result *= 31
        }
        result += right.hashCode()
        result
    }

    override fun get(index: Int) = if (index < left.length) left[index] else right[index - left.length]
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharSequence) return false
        if (length != other.length) return false
        if (other is RopeString && other.left.length == left.length && left == other.left && right == other.right) return true
        for (i in indices) {
            if (get(i) != other[i]) return false
        }
        return true
    }

    override fun hashCode() = hashCode

    override fun toString(): String {
        val sb = StringBuilder(length)
        sb.append(left)
        sb.append(right)
        return sb.toString()
    }
    companion object {
        fun concat(left: CharSequence, right: CharSequence): CharSequence {
            if (left.isEmpty()) return right
            if (right.isEmpty()) return left
            return RopeString(left, right)
        }
    }
}
