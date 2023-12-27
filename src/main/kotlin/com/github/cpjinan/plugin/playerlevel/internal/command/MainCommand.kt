package com.github.cpjinan.plugin.playerlevel.internal.command

import com.github.cpjinan.plugin.playerlevel.internal.api.LevelAPI
import com.github.cpjinan.plugin.playerlevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.runActions
import com.github.cpjinan.plugin.playerlevel.util.KetherUtil.toKetherScript
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

@CommandHeader(
  name = "PlayerLevel", aliases = ["level", "exp"], permissionDefault = PermissionDefault.TRUE
)
object MainCommand {
  @CommandBody(
    permission = "playerlevel.admin", permissionDefault = PermissionDefault.OP
  )
  val main = mainCommand {
    createHelper()

    // 经验命令
    literal("exp") {
      // 添加经验
      literal("add") {
        player("player").int("amount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              LevelAPI.addPlayerExp(context.player("player").toBukkitPlayer(), context["amount"].toInt())
              sender.sendLang("add-exp", context["player"], context["amount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      // 移除经验
      literal("remove") {
        player("player").int("amount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              LevelAPI.removePlayerExp(context.player("player").toBukkitPlayer(), context["amount"].toInt())
              sender.sendLang("remove-exp", context["player"], context["amount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      // 设置经验
      literal("set") {
        player("player").int("amount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              LevelAPI.setPlayerExp(context.player("player").toBukkitPlayer(), context["amount"].toInt())
              sender.sendLang("set-exp", context["player"], context["amount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      // 查询经验
      literal("check") {
        player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
          if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
            sender.sendLang(
              "check-exp", context["player"], LevelAPI.getPlayerExp(context.player("player").toBukkitPlayer())
            )
          } else sender.sendLang("no-permission")
        }
      }
    }

    // 等级命令
    literal("level") {
      // 添加等级
      literal("add") {
        player("player").int("amount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              LevelAPI.addPlayerLevel(context.player("player").toBukkitPlayer(), context["amount"].toInt())
              sender.sendLang("add-level", context["player"], context["amount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      // 移除等级
      literal("remove") {
        player("player").int("amount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              LevelAPI.removePlayerLevel(context.player("player").toBukkitPlayer(), context["amount"].toInt())
              sender.sendLang("remove-level", context["player"], context["amount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      // 设置等级
      literal("set") {
        player("player").int("amount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              val player = context.player("player").toBukkitPlayer()
              val level = context["amount"].toInt()
              LevelAPI.setPlayerLevel(player, level)
              sender.sendLang("set-level", context["player"], context["amount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      // 查询等级
      literal("check") {
        player("player").execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
          if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
            sender.sendLang(
              "check-level", context["player"], LevelAPI.getPlayerLevel(context.player("player").toBukkitPlayer())
            )
          } else sender.sendLang("no-permission")
        }
      }
    }

  }

  @CommandBody(
    permission = "playerlevel.admin", permissionDefault = PermissionDefault.OP
  )
  val debug = subCommand {
    if (ConfigManager.options.getBoolean("debug")) {
      createHelper()
      // 依赖检查
      literal("dependency").literal("check") {
        execute<ProxyCommandSender> { sender, _, _ ->
          if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) sender.sendMessage(("&7软依赖 &aPlaceholderAPI &7已找到！").colored())
              else sender.sendMessage(("&7软依赖 &7PlaceholderAPI &7未找到！").colored())
            if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) sender.sendMessage(("&7软依赖 &aMythicMobs &7已找到！").colored())
              else sender.sendMessage(("&7软依赖 &7MythicMobs &7未找到！").colored())
          } else sender.sendLang("no-permission")
        }
      }
      // 数据修改
      literal("data").literal("set") {
        player("player").int("levelAmount").int("expAmount") {
          execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
            if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
              val player = context.player("player").toBukkitPlayer()
              val level = context["levelAmount"].toInt()
              val exp = context["expAmount"].toInt()
              LevelAPI.setPlayerLevel(player, level)
              LevelAPI.setPlayerExp(player, exp)
              sender.sendLang("set-level", context["player"], context["levelAmount"])
              sender.sendLang("set-exp", context["player"], context["expAmount"])
            } else sender.sendLang("no-permission")
          }
        }
      }
      literal("kether") {
        // 运行Kether脚本
        literal("run") {
          dynamic("action") {
            execute<CommandSender> { sender, _, content ->
              if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
                try {
                  val script = if (content.startsWith("def")) {
                    content
                  } else {
                    "def main = { $content }"
                  }
                  script.toKetherScript().runActions {
                    this.sender = adaptCommandSender(sender)
                    if (sender is Player) {
                      set("player", sender)
                    }
                  }
                } catch (e: Exception) {
                  e.printKetherErrorMessage()
                }
              } else sender.sendLang("no-permission")
            }
          }
        }
        // 运行Kether脚本并返回结果
        literal("eval") {
          dynamic("action") {
            execute<CommandSender> { sender, _, content ->
              if (sender.isOp || sender.hasPermission("playerlevel.admin")) {
                try {
                  val script = if (content.startsWith("def")) {
                    content
                  } else {
                    "def main = { $content }"
                  }

                  script.toKetherScript().runActions {
                    this.sender = adaptCommandSender(sender)
                    if (sender is Player) {
                      set("player", sender)
                    }
                  }.thenAccept {
                    sender.sendMessage(" §3§l‹ ›§r §bResult: §f$it")
                  }
                } catch (e: Exception) {
                  e.printKetherErrorMessage()
                }
              } else sender.sendLang("no-permission")
            }
          }
        }
      }
    } else execute<ProxyCommandSender> { sender, _, _ ->
      if(sender.isOp || sender.hasPermission("playerlevel.admin")) {
        sender.sendLang("debug-not-enabled")
      } else sender.sendLang("no-permission")
    }
  }

  @CommandBody(
    permission = "playerlevel.default", permissionDefault = PermissionDefault.TRUE
  )
  val levelUp = subCommand {
    createHelper()
    execute<ProxyCommandSender> { _: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
      LevelAPI.playerLevelUP(context.player().toBukkitPlayer())
    }
  }
}

fun ProxyPlayer.toBukkitPlayer(): Player = Bukkit.getPlayer(this.uniqueId)!!