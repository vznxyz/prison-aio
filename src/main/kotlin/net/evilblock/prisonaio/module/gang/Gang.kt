/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.gang.entity.JerryNpcEntity
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.util.economy.Economy
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.max

class Gang(
    val gridIndex: Int,
    var name: String,
    var owner: UUID,
    var homeLocation: Location,
    guideLocation: Location,
    internal var cuboid: Cuboid
) : Region {

    val uuid: UUID = UUID.randomUUID()
    var announcement: String = "This is the default announcement."

    private val members: HashSet<UUID> = hashSetOf(owner)
    private val invited: HashMap<UUID, Long> = hashMapOf()
    private var permissions = hashMapOf<GangPermission, GangPermission.PermissionValue>()

    @Transient
    private var visitors: HashSet<UUID> = hashSetOf()

    internal var guideNpc: JerryNpcEntity = JerryNpcEntity(guideLocation)
    internal var cachedValue: Long = 0L

    override fun getRegionName(): String {
        return "${getOwnerUsername()}'s Gang"
    }

    override fun getCuboid(): Cuboid {
        return cuboid
    }

    override fun is3D(): Boolean {
        return false
    }

    override fun getBreakableCuboid(): Cuboid? {
        return cuboid
    }

    override fun resetBreakableCuboid() {

    }

    override fun supportsAbilityEnchants(): Boolean {
        return false
    }

    override fun supportsPassiveEnchants(): Boolean {
        return true
    }

    override fun supportsRewards(): Boolean {
        return false
    }

    override fun supportsAutoSell(): Boolean {
        return false
    }

    fun initializeData() {
        visitors = hashSetOf()

        guideNpc.initializeData()
        guideNpc.persistent = false
        guideNpc.gang = this

        guideNpc.updateTexture(
            GangModule.getJerryTextureValue(),
            GangModule.getJerryTextureSignature()
        )

        guideNpc.updateLines(GangModule.getJerryHologramLines())

        EntityManager.trackEntity(guideNpc)
    }

    override fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {
        if (Constants.CONTAINER_TYPES.contains(clickedBlock.type)) {
            if (!testPermission(player, GangPermission.ACCESS_CONTAINERS)) {
                cancellable.isCancelled = true
            }
        }

        if (Constants.INTERACTIVE_TYPES.contains(clickedBlock.type)) {
            if (!testPermission(player, GangPermission.INTERACT_WITH_BLOCKS)) {
                cancellable.isCancelled = true
            }
        }
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        handleBuild(player, block, cancellable)
    }

    override fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        handleBuild(player, block, cancellable)
    }

    override fun onBucketEmpty(player: Player, emptiedAt: Block, cancellable: Cancellable) {
        handleBuild(player, emptiedAt, cancellable)
    }

    override fun onBucketFill(player: Player, filledFrom: Block, cancellable: Cancellable) {
        handleBuild(player, filledFrom, cancellable)
    }

    private fun handleBuild(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true

        if (!isActivePlayer(player)) {
            player.sendMessage("${ChatColor.RED}You aren't allowed to build or break inside of ${this.getRegionName()}.")
            return
        }

        if (!testPermission(player, GangPermission.BUILD_AND_BREAK)) {
            return
        }

        if (!cuboid.contains(block.location)) {
            player.sendMessage("${ChatColor.RED}You can't build or break outside of the gang's headquarters.")
            return
        }

        cancellable.isCancelled = false
    }

    fun getOwnerUsername(): String {
        return Cubed.instance.uuidCache.name(owner)
    }

    /**
     * Replaces variables in the given [string].
     */
    fun translateVariables(string: String): String {
        return string.replace("{owner}", getOwnerUsername())
            .replace("{announcement}", announcement)
            .replace("{memberCount}", members.size.toString())
            .replace("{onlineMemberCount}", getActiveMembers().size.toString())
            .replace("{visitorCount}", getActiveVisitors().size.toString())
    }

    fun getPermissionValue(permission: GangPermission): GangPermission.PermissionValue {
        return permissions.getOrDefault(permission, permission.getDefaultValue())
    }

    fun setPermissionValue(permission: GangPermission, value: GangPermission.PermissionValue) {
        permissions[permission] = value
    }

    fun testPermission(player: Player, permission: GangPermission, sendMessage: Boolean = true): Boolean {
        when (getPermissionValue(permission)) {
            GangPermission.PermissionValue.OWNER -> {
                if (player.uniqueId != owner) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }

                    return false
                }
            }
            GangPermission.PermissionValue.MEMBERS -> {
                if (!members.contains(player.uniqueId)) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }

                    return false
                }
            }
            GangPermission.PermissionValue.VISITORS -> {
                return true
            }
        }

        return true
    }

    fun getActiveMembers(): Set<Player> {
        return members.mapNotNull { Bukkit.getPlayer(it) }.filter { GangHandler.getVisitingGang(it) == this }.toSet()
    }

    fun isActiveMember(player: Player): Boolean {
        return getActiveMembers().contains(player)
    }

    fun getActiveVisitors(): Set<Player> {
        return visitors.mapNotNull { Bukkit.getPlayer(it) }.filter { GangHandler.getVisitingGang(it) == this }.toSet()
    }

    fun getActivePlayers(): Set<Player> {
        val players = hashSetOf<Player>()
        players.addAll(getActiveMembers())
        players.addAll(getActiveVisitors())
        return players
    }

    fun isActivePlayer(player: Player): Boolean {
        return members.contains(player.uniqueId) || visitors.contains(player.uniqueId)
    }

    fun isOwner(uuid: UUID): Boolean {
        return owner == uuid
    }

    fun updateOwner(owner: UUID) {
        this.owner = owner

        val newOwnerUsername = Cubed.instance.uuidCache.name(owner)
        for (player in getActiveMembers()) {
            player.sendMessage("${ChatColor.YELLOW}The ownership of the gang has been relinquished to $newOwnerUsername.")
        }
    }

    fun getMembers(): Set<UUID> {
        return HashSet(members)
    }

    fun isMember(uuid: UUID): Boolean {
        return owner == uuid || members.contains(uuid)
    }

    internal fun expireInvitations() {
        val expired = hashSetOf<UUID>()

        for ((uuid, time) in invited.entries) {
            if (System.currentTimeMillis() - time > 30 * 60 * 1000) {
                expired.add(uuid)
            }
        }

        for (uuid in expired) {
            invited.remove(uuid)
        }
    }

    fun isInvited(uuid: UUID): Boolean {
        return invited.containsKey(uuid) && (System.currentTimeMillis() - invited[uuid]!!) < 30 * 60 * 1000
    }

    fun invitePlayer(uuid: UUID, invitedBy: UUID) {
        invited[uuid] = System.currentTimeMillis()

        val playerInvitedName = Cubed.instance.uuidCache.name(uuid)
        val invitedByName = Cubed.instance.uuidCache.name(invitedBy)
        sendMessagesToMembers("${ChatColor.YELLOW}$playerInvitedName has been invited to the gang by $invitedByName.")

        val playerInvited = Bukkit.getPlayer(uuid)
        if (playerInvited != null) {
            playerInvited.sendMessage("")
            playerInvited.sendMessage(" ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}New Invitation to Join Gang")
            playerInvited.sendMessage(" ${ChatColor.GRAY}You've been invited to join ${ChatColor.GREEN}${invitedByName}${ChatColor.GRAY}'s gang.")

            FancyMessage(" ")
                .then("${ChatColor.GRAY}[${ChatColor.GREEN}${ChatColor.BOLD}ACCEPT${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to accept the invitation."))
                .command("/gang join ${this.uuid}")
                .then(" ")
                .then("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}DECLINE${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to decline the invitation."))
                .command("/gang decline ${this.uuid}")
                .send(playerInvited)

            playerInvited.sendMessage("")
        }
    }

    fun revokeInvite(uuid: UUID) {
        invited.remove(uuid)

        val playerInvitedName = Cubed.instance.uuidCache.name(uuid)
        sendMessagesToMembers("${ChatColor.YELLOW}$playerInvitedName's invitation to join the gang has been revoked.")
    }

    fun memberJoin(uuid: UUID) {
        invited.remove(uuid)
        members.add(uuid)

        GangHandler.updateGangAccess(uuid = uuid, gang = this, joinable = true)

        val memberName = Cubed.instance.uuidCache.name(uuid)
        sendMessagesToMembers("${ChatColor.YELLOW}$memberName has joined the gang.")
    }

    fun memberLeave(uuid: UUID) {
        val memberName = Cubed.instance.uuidCache.name(uuid)
        sendMessagesToMembers("${ChatColor.YELLOW}$memberName has left the gang.")

        members.remove(uuid)

        val player = Bukkit.getPlayer(uuid)
        if (player != null) {
            // teleport player out of the mine
            if (isActiveMember(player)) {
                player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            }
        }

        GangHandler.updateGangAccess(uuid = uuid, gang = this, joinable = false)
    }

    fun kickMember(uuid: UUID) {
        if (members.remove(uuid)) {
            val memberName = Cubed.instance.uuidCache.name(uuid)
            sendMessagesToMembers("${ChatColor.YELLOW}$memberName has been kicked from the gang.")

            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                if (isActiveMember(player)) {
                    GangHandler.updateVisitingGang(player, null)

                    Tasks.sync {
                        player.teleport(Bukkit.getWorlds()[0].spawnLocation)
                    }
                }

                player.sendMessage("${ChatColor.YELLOW}You've been kicked from ${getOwnerUsername()}'s gang.")
            }
        }

        GangHandler.updateGangAccess(uuid = uuid, gang = this, joinable = false)
    }

    fun kickVisitors() {
        for (activePlayer in getActivePlayers()) {
            if (!testPermission(activePlayer, GangPermission.ALLOW_VISITORS, false)) {
                activePlayer.teleport(Bukkit.getWorlds()[0].spawnLocation)
                activePlayer.sendMessage("${ChatColor.YELLOW}${getOwnerUsername()}'s is no longer allowing visitors at their gang headquarters.")
            }
        }
    }

    fun joinSession(player: Player) {
        if (!members.contains(player.uniqueId)) {
            visitors.add(player.uniqueId)
        }

        // send message AFTER player is added
        sendMessagesToAll("${ChatColor.YELLOW}${player.name} has entered the gang headquarters.")
    }

    fun leaveSession(player: Player) {
        // send message BEFORE player is removed
        sendMessagesToAll("${ChatColor.YELLOW}${player.name} has left the gang headquarters.")

        visitors.remove(player.uniqueId)
    }

    fun updateName(sender: Player, name: String) {
        this.name = name

        sendMessagesToMembers("${ChatColor.YELLOW}${sender.name} has updated the gang's name.")
    }

    fun updateAnnouncement(sender: Player, announcement: String) {
        this.announcement = announcement

        sendMessagesToMembers("${ChatColor.YELLOW}${sender.name} has updated the gang's announcement.")
    }

    fun sendMessagesToMembers(vararg messages: String) {
        for (player in getActiveMembers()) {
            for (message in messages) {
                player.sendMessage(message)
            }
        }
    }

    fun sendMessagesToAll(vararg messages: String) {
        for (player in getActivePlayers()) {
            for (message in messages) {
                player.sendMessage(message)
            }
        }
    }

    fun sendBorderUpdate(player: Player) {
        val worldBorderPacket = PacketPlayOutWorldBorder()

        Reflection.setDeclaredFieldValue(worldBorderPacket, "a", PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "b", 29999984)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "c", cuboid.center.x)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "d", cuboid.center.z)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "g", 0L)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "i", 2)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "h", 20)

        val borderSize = max(cuboid.sizeX, cuboid.sizeZ).toDouble()

        Reflection.setDeclaredFieldValue(worldBorderPacket, "f", borderSize)
        Reflection.setDeclaredFieldValue(worldBorderPacket, "e", borderSize)

        MinecraftProtocol.send(player, worldBorderPacket)
    }

    fun updateCachedCellValue() {
        var totalBalance = 0L

        for (member in members) {
            try {
                totalBalance += Economy.getBalance(member)
            } catch (e: Exception) {

            }
        }

        cachedValue = totalBalance
    }

}