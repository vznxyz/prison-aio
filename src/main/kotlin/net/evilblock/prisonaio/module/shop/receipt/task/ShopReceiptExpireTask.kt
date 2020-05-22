package net.evilblock.prisonaio.module.shop.receipt.task

import net.evilblock.prisonaio.module.shop.ShopHandler

/**
 * Expires tracked receipts after 30 seconds from creation time.
 */
object ShopReceiptExpireTask : Runnable {

    override fun run() {
        for (receipts in ShopHandler.getReceipts().values) {
            if (receipts.isEmpty()) {
                continue
            }

            val iterator = receipts.iterator()
            while (iterator.hasNext()) {
                val receipt = iterator.next()
                if (System.currentTimeMillis() - receipt.createdAt > 30_000L) {
                    iterator.remove()
                }
            }
        }
    }

}