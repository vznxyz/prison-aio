/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.transaction

enum class TransactionResult(val defaultMessage: String) {
    SUCCESS("Successful transaction"),
    CANCELLED_PLUGIN("Cancelled by a plugin"),
    SHOP_EMPTY("Shop has no items"),
    NO_ITEMS("No items could be transacted"),
    FREE_BUY("Can't buy for free or negative amount"),
    FREE_SELL("Can't sell for free or negative amount")
}