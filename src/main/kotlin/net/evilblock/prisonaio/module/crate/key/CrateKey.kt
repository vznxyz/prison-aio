package net.evilblock.prisonaio.module.crate.key

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.serialize.CrateReferenceSerializer
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.util.*

data class CrateKey(
    @JsonAdapter(CrateReferenceSerializer::class)
    val crate: Crate,
    val uuid: UUID = UUID.randomUUID(),
    var issuedTo: UUID,
    var issuedAt: Date = Date.from(Instant.now()),
    var issuedBy: UUID? = null,
    var reason: String = "",
    var uses: Int = 0,
    var maxUses: Int,
    var dupedUseAttempts: Int = 0
) {

    fun hasUsesRemaining(): Boolean {
        return uses < maxUses
    }

    fun toItemStack(amount: Int): ItemStack {
        val item = ItemBuilder
            .copyOf(crate.keyItemStack)
            .name("${crate.name} Key")
            .setLore(listOf(
                "",
                "${ChatColor.GRAY}Right-click on a ${crate.name} ${ChatColor.GRAY}crate",
                "${ChatColor.GRAY}to use this key."
            ))
            .amount(amount)
            .build()

        val nmsCopy = CraftItemStack.asNMSCopy(item)

        val keyCompound = if (nmsCopy.hasTag()) nmsCopy.tag!! else NBTTagCompound()
        keyCompound.setString("CrateKey", uuid.toString())

        nmsCopy.tag = keyCompound

        return CraftItemStack.asBukkitCopy(nmsCopy)
    }

}