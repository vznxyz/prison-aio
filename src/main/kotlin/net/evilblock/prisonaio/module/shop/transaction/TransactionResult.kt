/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.transaction

import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType

enum class TransactionResult(private val defaultMessage: String) {

    SUCCESS("Successful transaction"),
    CANCELLED_PLUGIN("Cancelled by a plugin"),
    SHOP_EMPTY("Shop has no items"),
    NO_ITEMS("You didn't have any items to {context}"),
    CANNOT_AFFORD("You can't afford to purchase those items"),
    FREE_BUY("You can't buy for free or negative amount"),
    FREE_SELL("You can't sell for free or negative amount");

    fun getMessage(receiptType: ShopReceiptType): String {
        return defaultMessage.replace("{context}", if (receiptType == ShopReceiptType.BUY) { "buy" } else { "sell" })
    }

}