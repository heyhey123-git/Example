package io.github.username.project

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.submit
import taboolib.platform.util.bukkitPlugin
import taboolib.platform.util.title


object ExamplePlugin : Plugin() {

    override fun onEnable() {
        submit(period = 20 * 5) {
            for (player in bukkitPlugin.server.onlinePlayers) {
                player.title(
                    API.instance.getBiome(player.location).toString(),
                    ""
                )
            }
        }
    }
}
