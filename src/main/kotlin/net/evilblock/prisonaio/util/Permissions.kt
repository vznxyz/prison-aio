/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util

object Permissions {

    const val SYSTEM_ADMIN = "prisonaio.system.admin"

    const val REGION_ADMIN = "prisonaio.regions.admin"
    const val REGION_BYPASS = "prisonaio.regions.bypass"

    const val USERS_ADMIN = "prisonaio.users.admin"
    const val USERS_ADMIN_REMOVE_COMMENT = "prisonaio.users.profile.comments.remove"

    const val NICKNAME_GRANT = "prisonaio.users.nickname.grant"

    const val ECONOMY_ADMIN = "prisonaio.economy.admin"

    const val WARPS_MANAGE = "prisonaio.warps.manage"
    const val WARPS_ACCESS_ALL = "prisonaio.warps.access.all"

    const val NEWS_EDITOR = "prisonaio.news.editor"
    const val NEWS_VIEW_STATS = "prisonaio.news.view-stats"

    const val MINES_ADMIN = "prisonaio.mines.admin"
    const val MINE_ACCESS = "prisonaio.mines.access."

    const val GANGS_ADMIN = "prisonaio.gangs.admin"
    const val GANGS_TROPHIES_GIVE = "prisonaio.gangs.trophies.give"
    const val GANGS_TROPHIES_SET = "prisonaio.gangs.trophies.set"
    const val GANGS_TROPHIES_TAKE = "prisonaio.gangs.trophies.take"
    const val GANGS_BOOSTERS_GRANT = "prisonaio.gangs.boosters.grant"

    const val GENERATORS_GIVE = "prisonaio.generators.give"

    const val QUESTS_ADMIN = "prisonaio.quests.admin"
    const val BANK_NOTES_ADMIN = "prisonaio.banknotes.admin"
    const val TOKENS_ADMIN = "prisonaio.tokens.admin"
    const val BATTLE_PASS_ADMIN = "prisonaio.battlepass.admin"

    const val SHOP_EDITOR = "prisonaio.shop.admin"

    const val SALVAGE_PREVENTION_EDITOR = "prisonaio.enchants.salvage.editor"

    const val PICKAXE_ADMIN = "prisonaio.enchants.prestige.editor"
    const val PICKAXE_PRESTIGE_EDITOR = "prisonaio.enchants.prestige.editor"

    const val PERK_AUTO_SELL = "user.perks.autosell"
    const val PERK_FLY = "user.perks.fly"

    const val LEADERBOARDS_ADMIN = "prisonaio.leaderboards.admin"
    const val LEADERBOARDS_SPAWN = "prisonaio.leaderboards.spawn"

    const val EVENTS_HOST = "prisonaio.events.host."
    const val EVENTS_HOST_COOLDOWN_BYPASS = "prisonaio.events.host.cooldown-bypass"
    const val EVENTS_HOST_CONTROLS = "prisonaio.events.control"
    const val EVENTS_TOGGLE = "prisonaio.events.toggle"
    const val EVENTS_EDIT = "prisonaio.events.edit"

    const val MINE_PARTY = "prisonaio.mineparty.admin"
    const val GLOBAL_MULTIPLIER = "prisonaio.rewards.global-multi"
    const val ABILITY_ARMOR_GIVE = "prisonaio.ability-armor.give"
    const val COINFLIP_MANAGE = "prisonaio.minigame.coinflip.admin.toggle"
    const val PMINE_CREATE = "prisonaio.pmine.create"
    const val AUCTION_HOUSE_MOD = "prisonaio.auctionhouse.mod"

}