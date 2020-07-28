/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.shop.command.*
import net.evilblock.prisonaio.module.shop.command.parameter.ShopParameterType
import net.evilblock.prisonaio.module.shop.command.parameter.ShopReceiptParameterType
import net.evilblock.prisonaio.module.shop.listener.ShopReceiptListeners
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.task.ShopReceiptExpireTask
import org.bukkit.event.Listener

object ShopsModule : PluginModule() {

    override fun getName(): String {
        return "Shops"
    }

    override fun getConfigFileName(): String {
        return "shops"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        ShopHandler.initialLoad()

        // instead of registering the factory, we let the shop system handle creation/management of templates
        // MenuTemplateHandler.registerFactory(ShopMenuTemplate.ShopMenuTemplateFactory)

        Tasks.asyncTimer(ShopReceiptExpireTask, 2L, 2L)
    }

    override fun onAutoSave() {
        ShopHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            OpenShopCommand.javaClass,
            SellAllCommand.javaClass,
            ShopEditorCommand.javaClass,
            ShopReceiptCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Shop::class.java to ShopParameterType,
            ShopReceipt::class.java to ShopReceiptParameterType
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            ShopReceiptListeners
        )
    }

}