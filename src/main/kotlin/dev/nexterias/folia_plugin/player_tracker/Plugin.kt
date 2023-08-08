package dev.nexterias.folia_plugin.player_tracker

import dev.nexterias.folia_plugin.player_tracker.command.ClearTargetCommand
import dev.nexterias.folia_plugin.player_tracker.command.SetTargetCommand
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Plugin : JavaPlugin() {
    val tracerManager = PlayerTracerManager(this)

    override fun onEnable() {
        SetTargetCommand(this).register()
        ClearTargetCommand(this).register()
    }

    override fun onDisable() {
        tracerManager.tracers.keys.forEach { tracerManager.remove(it) }
    }
}