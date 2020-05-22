package net.evilblock.prisonaio.module.shop.receipt

enum class ShopReceiptType(
    val displayName: String,
    val context: String
) {

    BUY("Bought", "from"),
    SELL("Sold", "to")

}