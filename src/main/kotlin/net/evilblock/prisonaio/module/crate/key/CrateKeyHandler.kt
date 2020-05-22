package net.evilblock.prisonaio.module.crate.key

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import kotlin.math.min

object CrateKeyHandler : PluginHandler {

    private val keys = hashMapOf<UUID, CrateKey>()

    override fun getModule(): PluginModule {
        return CratesModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "crate-keys.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<CrateKey>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<CrateKey>

                for (key in list) {
                    keys[key.uuid] = key
                }
            }
        }
    }

    override fun saveData() {
        Files.write(Cubed.gson.toJson(keys.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun findKey(uuid: UUID): CrateKey? {
        return keys[uuid]
    }

    fun trackKey(key: CrateKey) {
        keys[key.uuid] = key
    }

    fun extractKey(itemStack: ItemStack): CrateKey? {
        val nmsCopy = CraftItemStack.asNMSCopy(itemStack)
        val keyCompound = nmsCopy.tag!!
        return findKey(UUID.fromString(keyCompound.getString("CrateKey")))
    }

    fun isCrateKeyItemStack(itemStack: ItemStack?): Boolean {
        if (itemStack == null || itemStack.type == Material.AIR) {
            return false
        }

        if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName() || !itemStack.itemMeta.hasLore()) {
            return false
        }

        if (PlacedCrateHandler.isChestType(itemStack.type)) {
            return false
        }

        val nmsCopy = CraftItemStack.asNMSCopy(itemStack)
        if (!nmsCopy.hasTag()) {
            return false
        }

        val keyCompound = nmsCopy.tag!!
        if (!keyCompound.hasKey("CrateKey")) {
            return false
        }

        val trackedKeyId = UUID.fromString(keyCompound.getString("CrateKey"))
        val trackedKey = findKey(trackedKeyId) ?: return false

        val crateKeyItem = trackedKey.crate.keyItemStack
        if (crateKeyItem.type != itemStack.type || crateKeyItem.durability != itemStack.durability) {
            return false
        }

        return true
    }

    fun giveKey(player: Player, crate: Crate, amount: Int, issuedBy: UUID?, reason: String) {
        // lets scan for existing keys that we can add the amount to
        var amountRemaining = amount
        for (item in player.inventory.contents) {
            if (isCrateKeyItemStack(item)) {
                val key = extractKey(item) ?: continue
                if (key.crate == crate && key.hasUsesRemaining()) {
                    if (item.amount >= 64) {
                        continue
                    }

                    val adding = min(64 - item.amount, amountRemaining)
                    item.amount = item.amount + adding
                    key.maxUses += adding
                    amountRemaining -= adding
                }
            }

            if (amountRemaining == 0) {
                break
            }
        }

        if (amountRemaining != 0) {
            val key = CrateKey(
                crate = crate,
                issuedTo = player.uniqueId,
                issuedBy = issuedBy,
                reason = reason,
                maxUses = amountRemaining
            )

            trackKey(key)

            if (player.inventory.firstEmpty() == -1) {
                player.enderChest.addItem(key.toItemStack(amountRemaining))
            } else {
                player.inventory.addItem(key.toItemStack(amountRemaining))
            }
        }

        player.updateInventory()
    }

}