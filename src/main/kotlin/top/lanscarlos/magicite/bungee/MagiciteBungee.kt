package top.lanscarlos.magicite.bungee

import com.google.common.io.ByteStreams
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PluginMessageEvent
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import java.util.*

object MagiciteBungee : Plugin() {

    override fun onEnable() {
        info("Successfully running MagiciteBungee!")
    }

    @SubscribeEvent
    fun e(e: PluginMessageEvent) {
        if (e.tag != "BungeeCord") return
        val data = ByteStreams.newDataInput(e.data)
        val sub = data.readUTF()
        when (sub) {
            "magicite:warp" -> {
                val serverName = data.readUTF()
                val uuid = UUID.fromString(data.readUTF())
                val id = data.readUTF()

                // 将玩家传送至目标服务器
                val player = BungeeCord.getInstance().getPlayer(uuid)
                if (player.server.info.name != serverName) {
                    val server = ProxyServer.getInstance().getServerInfo(serverName) ?: error("warp: 未知的目标服务器！")
                    player.connect(server)
                }

                // 向目标服务器发送数据
                val output = ByteStreams.newDataOutput().also {
                    it.writeUTF("magicite:warp")
                    it.writeUTF(uuid.toString())
                    it.writeUTF(id)
                }
                ProxyServer.getInstance().getServerInfo(serverName)?.sendData("BungeeCord", output.toByteArray())
            }
        }
    }
}