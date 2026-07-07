package net.tegulis.komputus.prefixes

abstract class PrefixGroup {
    abstract val multiplier: Int
    abstract val prefixes: List<Prefix>
    abstract val noScalingPrefix: Prefix

    override fun toString(): String = this::class.simpleName ?: super.toString()
}

object NotScalingPrefixGroup : PrefixGroup() {
    override val multiplier = 0
    override val prefixes = listOf(NotScalingPrefix)
    override val noScalingPrefix = NotScalingPrefix
}
