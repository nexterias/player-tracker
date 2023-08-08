package dev.nexterias.folia_plugin.player_tracker.command

import dev.nexterias.folia_plugin.player_tracker.Plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class SetTargetCommand(val plugin: Plugin) : RegistrableCommand, TabExecutor {
    override fun register() {
        val command = plugin.getCommand("set-target") ?: throw Exception("Command not found: set-target")

        command.setExecutor(this)
        command.tabCompleter = this
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        commandLabel: String,
        args: Array<out String>?
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("プレイヤーのみ実行できます。", NamedTextColor.RED))

            return true
        }

        val targetName = args?.getOrNull(0) ?: return false
        val target = sender.server.getPlayer(targetName)

        if (target == null) {
            sender.sendMessage(Component.text("ターゲットがサーバに居ません。", NamedTextColor.RED))

            return true
        }

        if (target.uniqueId == sender.uniqueId) {
            sender.sendMessage(Component.text("自分自身を追跡することはできません。", NamedTextColor.RED))

            return true
        }

        val tracer = plugin.tracerManager.getOrPut(sender, target)

        tracer.setTarget(target)
        if (!tracer.running) tracer.start()

        sender.sendMessage(Component.text("${target.name}の追跡を開始します。", NamedTextColor.GREEN))

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if (sender !is Player) return null

        val players = sender.server.onlinePlayers
            .map { it.name }
            .filter { it != sender.name }
            .toMutableList()
        val input = args?.getOrNull(0) ?: return players

        return players
            .filter { it.startsWith(input, ignoreCase = true) }
            .toMutableList()
    }
}