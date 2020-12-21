/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.normal

import net.evilblock.cubed.lite.LiteEdit
import net.evilblock.cubed.lite.LiteRegion
import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.block.BlockType
import net.evilblock.prisonaio.module.mine.config.MineBlocksConfig
import net.evilblock.prisonaio.module.mine.config.MineResetConfig
import net.evilblock.prisonaio.module.mine.menu.button.ManageBlocksButton
import net.evilblock.prisonaio.module.mine.menu.button.ManageResetButton
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.minecraft.server.v1_12_R1.IBlockData
import org.bukkit.Location
import java.lang.reflect.Type

open class NormalMine(id: String) : Mine(id = id) {

    val blocksConfig: MineBlocksConfig = MineBlocksConfig()
    val resetConfig: MineResetConfig = MineResetConfig()

    @Transient
    var lastResetCheck: Long = System.currentTimeMillis()

    override fun getAbstractType(): Type {
        return NormalMine::class.java
    }

    override fun getEditorButtons(): List<Button> {
        return super.getEditorButtons().toMutableList().also {
            it.add(ManageBlocksButton(this))
            it.add(ManageResetButton(this))
        }
    }

    override fun resetRegion() {
        if (region == null) {
            throw IllegalStateException("Cannot reset mine if its region is not set")
        }

        if (blocksConfig.blockTypes.isEmpty()) {
            throw IllegalStateException("Cannot reset mine if the blocks config contains no block types")
        }

        val blockList = arrayListOf<BlockType>()
        for (i in 0 until (region!!.sizeX * region!!.sizeY * region!!.sizeZ)) {
            blockList.add(blocksConfig.pickRandomBlockType())
        }

        var index = 0

        val liteRegion = LiteRegion(region!!)
        LiteEdit.fill(liteRegion, object : LiteEdit.FillHandler {
            override fun getBlock(x: Int, y: Int, z: Int): IBlockData? {
                if (RewardsModule.isEnabled()) {
                    if (MineCrateHandler.isAttached(Location(region!!.world, x.toDouble(), y.toDouble(), z.toDouble()))) {
                        return null
                    }
                }

                val blockType = blockList[index++]
                return getData(blockType.material, blockType.data.toInt())
            }
        }, LiteEdit.VoidProgressCallBack)
    }

}