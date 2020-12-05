package net.evilblock.prisonaio.util.particle

import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer

/**
 * A class to simplify sending particles to players.
 *
 * Also created to fix the issue of bukkit sometimes
 * not sending the particle or sending the particle
 * in varying speeds / sizes.
 */
object ParticleUtil {

    // name x y z offX offY offZ speed count
    fun sendsParticleToAll(vararg particleMetas: ParticleMeta) {
        val packets = arrayListOf<Pair<Location, PacketPlayOutWorldParticles>>()

        for (meta in particleMetas) {
            packets.add(meta.location to PacketPlayOutWorldParticles(
                    meta.particle,
                    true,
                    meta.location.x.toFloat(),
                    meta.location.y.toFloat(),
                    meta.location.z.toFloat(),
                    meta.offsetX,
                    meta.offsetY,
                    meta.offsetZ,
                    meta.speed,
                    meta.amount))
        }

        packets.forEach { pair ->
            pair.first.world.players.forEach { showTo ->
                if (showTo.location.distance(pair.first) <= 64.0) {
                    (showTo as CraftPlayer).handle.playerConnection.sendPacket(pair.second)
                }
            }
        }
    }

}