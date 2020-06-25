/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.receipt

enum class ShopReceiptType(
    val displayName: String,
    val context: String
) {

    BUY("Bought", "from"),
    SELL("Sold", "to")

}