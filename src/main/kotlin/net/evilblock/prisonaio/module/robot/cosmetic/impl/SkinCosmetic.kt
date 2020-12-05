package net.evilblock.prisonaio.module.robot.cosmetic.impl

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SkinCosmetic(private val uniqueId: String) : Cosmetic {

    override fun getUniqueId(): String {
        return uniqueId
    }

    /**
     * Fetches the name from the configuration file.
     */
    override fun getName(): String {
        return ChatColor.translateAlternateColorCodes('&', RobotsModule.config.getString("cosmetics.skins.$uniqueId.name")) + " Skin"
    }

    /**
     * Fetches the description from the configuration file.
     */
    override fun getDescription(): List<String> {
        return RobotsModule.config.getStringList("cosmetics.skins.$uniqueId.description").map { ChatColor.translateAlternateColorCodes('&', it) }.toList()
    }

    /**
     * Skin cosmetics are never compatible with other skin cosmetics.
     */
    override fun isCompatible(other: Cosmetic): Boolean {
        return other !is SkinCosmetic
    }

    /**
     * Sets the robot's helmet to a player head with the [getTextureValue] texture.
     */
    override fun onEnable(robot: MinerRobot) {
        robot.updateHelmet(ItemUtils.applySkullTexture(ItemBuilder(Material.SKULL_ITEM).data(3).build(), getTextureValue()))
        robot.updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), getArmorColor()))
        robot.updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), getArmorColor()))
        robot.updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), getArmorColor()))
    }

    /**
     * Sets the robot's helmet to its default helmet (a player head with the owner's texture).
     */
    override fun onDisable(robot: MinerRobot) {
        robot.restoreDefaultEquipment()
    }

    /**
     * Fetches the armor color from the configuration file.
     */
    fun getArmorColor(): Color {
        return Color.fromRGB(RobotsModule.config.getInt("cosmetics.skins.$uniqueId.armor-color"))
    }

    /**
     * Fetches the texture value from the configuration file.
     */
    fun getTextureValue(): String {
        return RobotsModule.config.getString("cosmetics.skins.$uniqueId.texture")
    }

}