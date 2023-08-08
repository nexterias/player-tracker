package dev.nexterias.folia_plugin.player_tracker.command

import dev.nexterias.folia_plugin.player_tracker.Plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ClearTargetCommand(val plugin: Plugin) : CommandExecutor, RegistrableCommand {
    override fun register() {
        val command = plugin.getCommand("clear-target") ?: throw Exception("Command not found: clear-target")

        command.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("プレイヤーのみ実行できます。", NamedTextColor.RED))

            return true
        }

        plugin.tracerManager.remove(sender)
        sender.sendMessage(Component.text("追跡を終了しました。", NamedTextColor.GREEN))

        return true
    }
}