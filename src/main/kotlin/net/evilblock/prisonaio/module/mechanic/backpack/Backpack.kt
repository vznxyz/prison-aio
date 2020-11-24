/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.nms.NBTUtil
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.BackpackUpgrade
import net.evilblock.prisonaio.module.mechanic.backpack.menu.BackpackMenu
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.impl.CapacityUpgrade
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.util.*

class Backpack(val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 13)) {

    internal val contents: MutableList<ItemStack> = arrayListOf()

    @JsonAdapter(BackpackUpgradesMapSerializer::class)
    internal val upgrades: MutableMap<BackpackUpgrade, Int> = hashMapOf()

    fun addItem(add: ItemStack): ItemStack? {
        var remainingAmount = add.amount
        var backpackItemsSize = getItemsSize()
        val backpackMaxItemsSize = getMaxItemsSize()

        for (backpackItem in contents) {
            if (backpackItem.amount >= backpackItem.maxStackSize) {
                continue
            }

            if (!ItemUtils.isSimilar(backpackItem, add) || !ItemUtils.hasSameLore(backpackItem, add) || !ItemUtils.hasSameEnchantments(backpackItem, add)) {
                continue
            }

            var maxInsert = backpackItem.maxStackSize - backpackItem.amount
            if (maxInsert <= 0) {
                continue
            }

            if (backpackItemsSize + remainingAmount >= backpackMaxItemsSize) {
                maxInsert = maxInsert.coerceAtMost(backpackMaxItemsSize - backpackItemsSize)
            }

            if (remainingAmount <= maxInsert) {
                backpackItem.amount = backpackItem.amount + remainingAmount
                remainingAmount = 0
                backpackItemsSize += remainingAmount
            } else {
                backpackItem.amount = backpackItem.amount + maxInsert
                remainingAmount -= maxInsert
                backpackItemsSize += maxInsert
            }

            if (remainingAmount <= 0 || backpackItemsSize >= backpackMaxItemsSize) {
                break
            }
        }

        if (remainingAmount > 0 && backpackItemsSize < backpackMaxItemsSize) {
            while (remainingAmount > 0) {
                var insertAmount = remainingAmount.coerceAtMost(add.maxStackSize)

                if (backpackItemsSize + insertAmount >= backpackMaxItemsSize) {
                    insertAmount = insertAmount.coerceAtMost(backpackMaxItemsSize - backpackItemsSize)
                }

                if (insertAmount <= 0) {
                    break
                }

                contents.add(ItemBuilder.copyOf(add).amount(insertAmount).build())
                remainingAmount -= insertAmount
                backpackItemsSize += insertAmount

                if (remainingAmount <= 0 || backpackItemsSize >= backpackMaxItemsSize) {
                    break
                }
            }
        }

        return if (remainingAmount > 0) {
            return ItemBuilder.copyOf(add).amount(remainingAmount).build()
        } else {
            null
        }
    }

    fun removeItem(remove: ItemStack) {
        var remainingAmount = remove.amount

        val toRemove = arrayListOf<ItemStack>()
        for (item in contents) {
            if (item.amount <= 0) {
                continue
            }

            if (!ItemUtils.isSimilar(item, remove) || !ItemUtils.hasSameLore(item, remove) || !ItemUtils.hasSameEnchantments(item, remove)) {
                continue
            }

            val maxTake = item.amount.coerceAtMost(remainingAmount)
            if (maxTake <= 0) {
                continue
            }

            if (remainingAmount >= maxTake) {
                toRemove.add(item)
                remainingAmount -= maxTake
            } else {
                item.amount = item.amount - maxTake
                remainingAmount = 0
            }

            if (remainingAmount <= 0) {
                break
            }
        }

        for (item in toRemove) {
            contents.remove(item)
        }
    }

    fun clearItems() {
        contents.clear()
    }

    fun hasUpgrade(upgrade: BackpackUpgrade): Boolean {
        return upgrades.containsKey(upgrade)
    }

    fun addUpgrade(upgrade: BackpackUpgrade) {
        upgrades[upgrade] = 1
    }

    fun getUpgradeLevel(upgrade: BackpackUpgrade): Int {
        return upgrades.getOrDefault(upgrade, 0)
    }

    fun addUpgradeLevel(upgrade: BackpackUpgrade) {
        upgrades[upgrade] = getUpgradeLevel(upgrade) + 1
    }

    fun getItemsSize(): Int {
        return contents.sumBy { it.amount }
    }

    fun getMaxItemsSize(): Int {
        return if (hasUpgrade(CapacityUpgrade)) {
            1440 + getUpgradeLevel(CapacityUpgrade)
        } else {
            1440
        }
    }

    fun open(player: Player) {
        BackpackMenu(this).openMenu(player)
    }

    fun toBackpackItem(): ItemStack {
        val item = ItemBuilder.of(Material.CHEST)
            .name("${ChatColor.RED}${ChatColor.BOLD}Backpack")
            .build()

        updateLore(item)

        val nmsCopy = ItemUtils.getNmsCopy(item)

        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        NBTUtil.setString(tag, "BackpackID", id)
        NBTUtil.setTag(nmsCopy, tag)

        return ItemUtils.getBukkitCopy(nmsCopy)
    }

    fun updateLore(itemStack: ItemStack) {
        if (itemStack.hasItemMeta()) {
            val lore = arrayListOf<String>()
            lore.add("${ChatColor.GRAY}Items: ${ChatColor.RED}${NumberUtils.format(getItemsSize())}${ChatColor.GRAY}/${ChatColor.RED}${NumberUtils.format(getMaxItemsSize())}")
            lore.add("")
            lore.add("${ChatColor.RED}${ChatColor.BOLD}Upgrades")

            if (upgrades.isEmpty()) {
                lore.add("${ChatColor.GRAY}None")
            } else {
                for (enchant in upgrades) {
                    lore.add("${enchant.key.lorified()} ${enchant.value}")
                }
            }

            lore.add("")
            lore.add("${ChatColor.GRAY}Right-click while holding this")
            lore.add("${ChatColor.GRAY}backpack in your hand to access")
            lore.add("${ChatColor.GRAY}its upgrades and statistics!")

            val meta = itemStack.itemMeta
            meta.lore = lore

            itemStack.itemMeta = meta
        }
    }

    object BackpackUpgradesMapSerializer : JsonSerializer<Map<BackpackUpgrade, Int>>, JsonDeserializer<Map<BackpackUpgrade, Int>> {
        override fun serialize(map: Map<BackpackUpgrade, Int>, type: Type, context: JsonSerializationContext): JsonElement {
            return JsonObject().also {
                for ((upgrade, level) in map) {
                    it.addProperty(upgrade.getId(), level)
                }
            }
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Map<BackpackUpgrade, Int> {
            return hashMapOf<BackpackUpgrade, Int>().also {
                for ((key, value) in json.asJsonObject.entrySet()) {
                    val upgrade = BackpackHandler.getUpgradeById(key)
                    if (upgrade != null) {
                        it[upgrade] = value.asInt
                    }
                }
            }
        }
    }

}