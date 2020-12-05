/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.schematic.rotate

import net.evilblock.prisonaio.module.generator.schematic.rotate.material.Anvil
import net.evilblock.prisonaio.module.generator.schematic.rotate.material.Log
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.material.*
import org.bukkit.material.Comparator
import org.bukkit.material.Observer
import java.util.*
import kotlin.math.roundToInt

object RotateUtil {

    private val rotatables: EnumMap<Material, MaterialData> = EnumMap<Material, MaterialData>(Material::class.java).also { map ->
        map[Material.LADDER] = Ladder()
        map[Material.STONE_BUTTON] = Button()
        map[Material.WOOD_BUTTON] = Button()
        map[Material.CHEST] = Chest()
        map[Material.ENDER_CHEST] = EnderChest()
        map[Material.TRAPPED_CHEST] = Chest()
        map[Material.FENCE_GATE] = Gate()
        map[Material.ANVIL] = Anvil()
        map[Material.HOPPER] = Hopper()
        map[Material.LOG] = Log()
        map[Material.IRON_DOOR_BLOCK] = Door()
        map[Material.ACACIA_DOOR] = Door()
        map[Material.BIRCH_DOOR] = Door()
        map[Material.DARK_OAK_DOOR] = Door()
        map[Material.IRON_DOOR] = Door()
        map[Material.JUNGLE_DOOR] = Door()
        map[Material.SPRUCE_DOOR] = Door()
        map[Material.WOOD_DOOR] = Door()
        map[Material.WOODEN_DOOR] = Door()
        map[Material.TRAP_DOOR] = TrapDoor()
        map[Material.IRON_TRAPDOOR] = TrapDoor()
        map[Material.BANNER] = Banner()
        map[Material.BED] = Bed()
        map[Material.COCOA] = CocoaPlant()
        map[Material.REDSTONE_COMPARATOR] = Comparator()
        map[Material.REDSTONE_COMPARATOR_OFF] = Comparator()
        map[Material.REDSTONE_COMPARATOR_ON] = Comparator()
        map[Material.DIODE] = Diode()
        map[Material.DIODE_BLOCK_OFF] = Diode()
        map[Material.DIODE_BLOCK_ON] = Diode()
        map[Material.DISPENSER] = Dispenser()
        map[Material.FURNACE] = Furnace()
        map[Material.LEVER] = Lever()
        map[Material.OBSERVER] = Observer()
        map[Material.PISTON_BASE] = PistonBaseMaterial(Material.PISTON_BASE)
        map[Material.PISTON_STICKY_BASE] = PistonBaseMaterial(Material.PISTON_STICKY_BASE)

        // Piston extension rotation will act like a regular piston block, unsure if this affects anything
        map[Material.PISTON_EXTENSION] = PistonExtensionMaterial(Material.PISTON_BASE)

        map[Material.PUMPKIN] = Pumpkin()
        map[Material.REDSTONE_TORCH_OFF] = RedstoneTorch()
        map[Material.REDSTONE_TORCH_ON] = RedstoneTorch()
        map[Material.SIGN] = Sign()
        map[Material.SKULL] = Skull()
        map[Material.TORCH] = Torch()
        map[Material.TRIPWIRE_HOOK] = TripwireHook()
        map[Material.ACACIA_STAIRS] = Stairs(Material.ACACIA_STAIRS)
        map[Material.BIRCH_WOOD_STAIRS] = Stairs(Material.BIRCH_WOOD_STAIRS)
        map[Material.BRICK_STAIRS] = Stairs(Material.BRICK_STAIRS)
        map[Material.COBBLESTONE_STAIRS] = Stairs(Material.COBBLESTONE_STAIRS)
        map[Material.DARK_OAK_STAIRS] = Stairs(Material.DARK_OAK_STAIRS)
        map[Material.JUNGLE_WOOD_STAIRS] = Stairs(Material.JUNGLE_WOOD_STAIRS)
        map[Material.NETHER_BRICK_STAIRS] = Stairs(Material.NETHER_BRICK_STAIRS)
        map[Material.PURPUR_STAIRS] = Stairs(Material.PURPUR_STAIRS) //wtf are these
        map[Material.QUARTZ_STAIRS] = Stairs(Material.QUARTZ_STAIRS)
        map[Material.RED_SANDSTONE_STAIRS] = Stairs(Material.RED_SANDSTONE_STAIRS)
        map[Material.SANDSTONE_STAIRS] = Stairs(Material.SANDSTONE_STAIRS)
        map[Material.SMOOTH_STAIRS] = Stairs(Material.SMOOTH_STAIRS)
        map[Material.SPRUCE_WOOD_STAIRS] = Stairs(Material.SPRUCE_WOOD_STAIRS)
        map[Material.WOOD_STAIRS] = Stairs(Material.WOOD_STAIRS)
    }

    fun isRotatable(material: Material?): Boolean {
        return rotatables.containsKey(material)
    }

    fun getDirectionalMaterialData(material: Material?, data: Byte): MaterialData? {
        val mData = rotatables[material] ?: return null
        mData.data = data
        return mData
    }

    fun getFacing(data: MaterialData): BlockFace {
        if (data !is Directional) {
            return BlockFace.NORTH
        }

        val dir = data as Directional

        return if (data is Stairs || data is Ladder) {
            dir.facing.oppositeFace
        } else {
            dir.facing
        }
    }

    fun rotate(rotation: Rotation, face: BlockFace): BlockFace? {
        return if (face.ordinal >= 4 || rotation === Rotation.NORTH) {
            face
        } else when (rotation) {
            Rotation.EAST -> {
                when (face) {
                    BlockFace.NORTH -> return BlockFace.EAST
                    BlockFace.EAST -> return BlockFace.SOUTH
                    BlockFace.SOUTH -> return BlockFace.WEST
                    BlockFace.WEST -> return BlockFace.NORTH
                    else -> BlockFace.NORTH
                }
            }
            Rotation.SOUTH -> {
                when (face) {
                    BlockFace.NORTH -> return BlockFace.SOUTH
                    BlockFace.EAST -> return BlockFace.WEST
                    BlockFace.SOUTH -> return BlockFace.NORTH
                    BlockFace.WEST -> return BlockFace.EAST
                    else -> BlockFace.NORTH
                }
            }
            Rotation.WEST -> {
                when (face) {
                    BlockFace.NORTH -> BlockFace.WEST
                    BlockFace.EAST -> BlockFace.NORTH
                    BlockFace.SOUTH -> BlockFace.EAST
                    BlockFace.WEST -> BlockFace.SOUTH
                    else -> BlockFace.NORTH
                }
            }
            else -> face
        }
    }

    fun getDirectionalByte(material: Material, data: Byte, rotation: Rotation): Byte {
        val materialData = getDirectionalMaterialData(material, data)
        if (materialData is Directional) {
            val dir = materialData as Directional
            dir.setFacingDirection(rotate(rotation, getFacing(materialData)))
            return materialData.data
        }
        return data
    }

    fun isInteractable(material: Material): Boolean {
        return when (material) {
            Material.BEACON,
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.ENDER_PORTAL,
            Material.BED,
            Material.BED_BLOCK,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.DISPENSER,
            Material.ANVIL,
            Material.ITEM_FRAME,
            Material.HOPPER,
            Material.JUKEBOX,
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.ENCHANTMENT_TABLE,
            Material.WORKBENCH -> true
            else -> false
        }
    }

    fun getPlaceable(material: Material): Material? {
        return when (material) {
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR -> Material.WOOD_DOOR
            Material.BED_BLOCK -> Material.BED
            Material.BEETROOT_BLOCK, Material.CARROT, Material.COCOA, Material.CROPS, Material.POTATO, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.SUGAR_CANE_BLOCK, Material.NETHER_WARTS -> Material.SEEDS
            Material.WATER, Material.STATIONARY_WATER -> Material.WATER_BUCKET
            Material.LAVA, Material.STATIONARY_LAVA -> Material.LAVA_BUCKET
            Material.FROSTED_ICE -> Material.ICE
            Material.FIRE, Material.PORTAL -> Material.FLINT_AND_STEEL
            Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE -> Material.PISTON_BASE
            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_LAMP_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_WIRE -> Material.REDSTONE
            Material.GLOWING_REDSTONE_ORE -> Material.REDSTONE_ORE
            Material.SIGN_POST, Material.WALL_SIGN -> Material.SIGN
            Material.STANDING_BANNER, Material.WALL_BANNER -> Material.BANNER
            Material.TRIPWIRE -> Material.TRIPWIRE_HOOK
            Material.WOOD_DOUBLE_STEP, Material.DOUBLE_STEP -> Material.WOOD_STAIRS
            Material.FLOWER_POT -> Material.FLOWER_POT_ITEM
            Material.BREWING_STAND -> Material.BREWING_STAND_ITEM
            Material.BURNING_FURNACE -> Material.FURNACE
            Material.CAKE_BLOCK -> Material.CAKE
            Material.CAULDRON -> Material.CAULDRON_ITEM
            Material.DAYLIGHT_DETECTOR_INVERTED -> Material.DAYLIGHT_DETECTOR
            Material.PURPUR_DOUBLE_SLAB -> Material.PURPUR_BLOCK
            Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON -> Material.DIODE
            Material.DOUBLE_STONE_SLAB2 -> Material.STONE_SLAB2
            Material.ENDER_PORTAL, Material.END_GATEWAY -> Material.EYE_OF_ENDER
            Material.SKULL -> Material.SKULL_ITEM
            Material.SOIL -> Material.DIRT
            else -> material
        }
    }

    fun isBlockSideDependant(material: Material): Boolean {
        return when (material) {
            Material.DARK_OAK_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.TRAP_DOOR, Material.RAILS, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL, Material.WHEAT, Material.PUMPKIN, Material.SUGAR_CANE, Material.SUGAR_CANE_BLOCK, Material.BED, Material.BED_BLOCK, Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN, Material.FLOWER_POT, Material.CHORUS_FLOWER, Material.YELLOW_FLOWER -> true
            else -> false
        }
    }

    fun getFacing(player: Player): Rotation {
        var yaw = player.location.yaw
        while (yaw < 0) {
            yaw += 360f
        }
        return Rotation.values()[(yaw / 90f).roundToInt() % 4]
    }

}