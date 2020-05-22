package net.evilblock.prisonaio.module.shop.exception

class ShopTransactionInterruptedException(reason: InterruptReason, customMessage: String = reason.defaultMessage) : Exception(customMessage) {

    enum class InterruptReason(val defaultMessage: String) {
        CANCELLED_PLUGIN("Cancelled by a plugin"),
        SHOP_EMPTY("Shop has no items"),
        NO_ITEMS("No items could be transacted"),
        FREE_BUY("Can't buy for free or negative amount"),
        FREE_SELL("Can't sell for free or negative amount"),
    }

}