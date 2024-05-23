package dev.nexterias.folia_plugin.player_tracker

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

enum class CompassDirection(val abbreviation: String) {
    S("南"),
    SSW("南南西"),
    SW("南西"),
    WSW("西南西"),
    W("西"),
    WNW("西北西"),
    NW("北西"),
    NNW("北北西"),
    N("北"),
    NNE("北北東"),
    NE("北東"),
    ENE("東北東"),
    E("東"),
    ESE("東南東"),
    SE("南東"),
    SSE("南南東"),
}

class PlayerTracer(val manager: PlayerTracerManager, val tracker: Player, target: Player) {
    var target: Player = target
        private set

    var task: ScheduledTask? = null
        private set

    val running: Boolean
        get() = task != null

    val bossBar: BossBar = BossBar.bossBar(
        Component.text("準備中...", NamedTextColor.GRAY),
        0.0F,
        BossBar.Color.RED,
        BossBar.Overlay.PROGRESS
    )

    fun setTarget(target: Player) {
        this.target = target
    }

    fun calculateDirection(): CompassDirection {
        val directionVector = target.location.clone().subtract(tracker.location)
        val yaw = ((directionVector.yaw % 360) + 360) % 360
        val directionIndex = (Math.round(yaw / 22.5) % 16).toInt()

        return CompassDirection.entries[directionIndex]
    }

    fun start() {
        if (running) return
        if (!tracker.isOnline) throw Exception("Tracker is offline.")
        if (!target.isOnline) throw Exception("Target is offline.")

        tracker.showBossBar(bossBar)
        task = tracker.scheduler.runAtFixedRate(manager.plugin, {
            if (!target.isOnline) {
                manager.remove(tracker)
                tracker.sendMessage(
                    Component.text(
                        "ターゲットがオフラインになったため追跡を終了しました。",
                        NamedTextColor.RED
                    )
                )

                return@runAtFixedRate
            }

            if (target.location.world.name != tracker.location.world.name) {
                val textColor =
                    if (bossBar.name().color() == NamedTextColor.GRAY) NamedTextColor.RED else NamedTextColor.GRAY
                val message = Component.text()
                    .color(textColor)
                    .append(Component.text("現在"))
                    .append(Component.text(target.name, NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text("は"))
                    .append(Component.text(target.location.world.name, NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text("に居ます。"))

                bossBar.progress(0.0F)
                bossBar.name(message)
            } else {
                val distance = tracker.location.distance(target.location).toInt()
                val direction = calculateDirection()
                val message = Component.text()
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(target.name, NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text("を追跡しています..."))
                    .appendSpace()
                    .append(Component.text("距離:"))
                    .appendSpace()
                    .append(Component.text("${distance}m", NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .appendSpace()
                    .append(
                        Component.text(
                            "方角: ${direction.abbreviation}",
                            NamedTextColor.YELLOW,
                            TextDecoration.BOLD
                        )
                    )

                bossBar.name(message)
                bossBar.progress(Math.exp(-0.01 * distance).toFloat())
            }
        }, {
            manager.remove(tracker)
        }, 20L, 20L) ?: throw Exception("Failed to start task.")
    }

    fun cancel() {
        task?.cancel()
        task = null

        if (tracker.isOnline) {
            tracker.hideBossBar(bossBar)
        }

        bossBar.name(Component.text("準備中...", NamedTextColor.GRAY))
        bossBar.progress(0.0F)
    }
}