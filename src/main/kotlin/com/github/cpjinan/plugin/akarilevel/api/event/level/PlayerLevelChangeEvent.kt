package com.github.cpjinan.plugin.akarilevel.api.event.level

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerLevelChangeEvent(val player: Player, var level: Int, var source: String) : BukkitProxyEvent()