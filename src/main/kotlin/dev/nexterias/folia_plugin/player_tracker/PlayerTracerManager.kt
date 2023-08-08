package dev.nexterias.folia_plugin.player_tracker

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class PlayerTracerManager(val plugin: Plugin) {
    private val _tracers = mutableMapOf<UUID, PlayerTracer>()
    val tracers: Map<UUID, PlayerTracer>
        get() = _tracers.toMap()

    fun getOrPut(tracker: Player, target: Player): PlayerTracer {
        return _tracers.getOrPut(tracker.uniqueId) { PlayerTracer(plugin, tracker, target) }
    }

    fun get(tracker: Player): PlayerTracer? {
        return get(tracker.uniqueId)
    }

    fun get(trackerId: UUID): PlayerTracer? {
        return _tracers[trackerId]
    }

    fun remove(trackerId: UUID) {
        val tracer = _tracers.remove(trackerId)

        tracer?.cancel()
    }

    fun remove(tracker: OfflinePlayer) {
        val tracer = _tracers.remove(tracker.uniqueId)

        tracer?.cancel()
    }
}