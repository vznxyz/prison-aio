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
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.gang.challenge.GangChallengesData
import net.evilblock.prisonaio.module.gang.entity.JerryNpcEntity
import net.evilblock.prisonaio.module.gang.invite.GangInvite
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.user.UserHandler
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.lang.Exception
import java.math.BigInteger
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.max

class Gang(
    val gridIndex: Int,
    var name: String,
    var leader: UUID,
    var homeLocation: Location,
    guideLocation: Location,
    cuboid: Cuboid
) : Region("gang-grid-$gridIndex", cuboid) {

    val uuid: UUID = UUID.randomUUID()
    var announcement: String = "This is the default announcement."

    private val members: MutableMap<UUID, GangMember> = hashMapOf()
    private var invitations: HashMap<UUID, GangInvite> = hashMapOf()
    private var permissions = hashMapOf<GangPermission, GangPermission.PermissionValue>()

    var challengesData: GangChallengesData = GangChallengesData(this)
    private var trophies: Int = 0
    private var boosters: MutableSet<GangBooster> = hashSetOf()

    internal var cachedValue: BigInteger = BigInteger("0")

    internal var guideNpc: JerryNpcEntity = JerryNpcEntity(guideLocation)

    @Transient
    internal var visitors: HashSet<UUID> = hashSetOf()

    override fun getRegionName(): String {
        return "${getLeaderUsername()}'s Gang"
    }

    override fun getPriority(): Int {
        return 100
    }

    override fun getCuboid(): Cuboid {
        return cuboid!!
    }

    override fun getBreakableCuboid(): Cuboid {
        return cuboid!!
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
        challengesData.gang = this

        guideNpc.initializeData()
        guideNpc.persistent = false
        guideNpc.gang = this
        guideNpc.updateTexture(GangModule.getJerryTextureValue(), GangModule.getJerryTextureSignature())
        guideNpc.updateLines(GangModule.getJerryHologramLines())

        if (!EntityManager.isEntityTracked(guideNpc)) {
            EntityManager.trackEntity(guideNpc)
        }

        invitations = hashMapOf()
        visitors = hashSetOf()
        cachedValue = BigInteger("0")
    }

    fun getLeaderUsername(): String {
        return Cubed.instance.uuidCache.name(leader)
    }

    /**
     * Replaces variables in the given [string].
     */
    fun translateVariables(string: String): String {
        return string.replace("{leader}", getLeaderUsername())
            .replace("{owner}", getLeaderUsername())
            .replace("{announcement}", announcement)
            .replace("{memberCount}", members.size.toString())
            .replace("{onlineMemberCount}", getOnlineMembers().size.toString())
            .replace("{visitorCount}", getVisitingPlayers().size.toString())
    }

    fun getPermissionValue(permission: GangPermission): GangPermission.PermissionValue {
        return permissions.getOrDefault(permission, permission.getDefaultValue())
    }

    fun setPermissionValue(permission: GangPermission, value: GangPermission.PermissionValue) {
        permissions[permission] = value
    }

    fun testPermission(player: Player, permission: GangPermission, sendMessage: Boolean = true): Boolean {
        val memberInfo = getMemberInfo(player.uniqueId)
        when (getPermissionValue(permission)) {
            GangPermission.PermissionValue.OWNER -> {
                if (player.uniqueId != leader) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }
                    return false
                }
            }
            GangPermission.PermissionValue.CO_LEADERS -> {
                if (memberInfo?.role?.isAtLeast(GangMember.Role.CO_LEADER) == false) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }
                    return false
                }
            }
            GangPermission.PermissionValue.CAPTAINS -> {
                if (!isLeader(player.uniqueId) && memberInfo?.role?.isAtLeast(GangMember.Role.CAPTAIN) == false) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }
                    return false
                }
            }
            GangPermission.PermissionValue.MEMBERS -> {
                if (!members.containsKey(player.uniqueId)) {
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

    fun getOnlineMembers(): Set<Player> {
        return members.mapNotNull { Bukkit.getPlayer(it.key) }.toSet()
    }

    fun getVisitingPlayers(): Set<Player> {
        return visitors.mapNotNull { Bukkit.getPlayer(it) }.toSet()
    }

    fun getActivePlayers(): Set<Player> {
        val players = hashSetOf<Player>()
        players.addAll(getOnlineMembers())
        players.addAll(getVisitingPlayers())
        return players
    }

    fun isActivePlayer(player: Player): Boolean {
        return members.containsKey(player.uniqueId) || visitors.contains(player.uniqueId)
    }

    fun isLeader(uuid: UUID): Boolean {
        return leader == uuid
    }

    fun updateLeader(owner: UUID) {
        this.leader = owner

        val memberInfo = getMemberInfo(this.leader)
        if (memberInfo != null) {
            memberInfo.role = GangMember.Role.LEADER
        }

        val newLeaderUsername = Cubed.instance.uuidCache.name(owner)
        for (player in getOnlineMembers()) {
            player.sendMessage("${ChatColor.YELLOW}The leadership of the gang has been relinquished to $newLeaderUsername.")
        }
    }

    fun getMembers(): Map<UUID, GangMember> {
        return members
    }

    fun isMember(uuid: UUID): Boolean {
        return leader == uuid || members.containsKey(uuid)
    }

    fun getMemberInfo(uuid: UUID): GangMember? {
        return members[uuid]
    }

    internal fun expireInvitations() {
        val expired = hashSetOf<UUID>()

        for (invite in invitations.values) {
            if (System.currentTimeMillis() - invite.invitedAt > 30 * 60 * 1000) {
                expired.add(uuid)
            }
        }

        for (uuid in expired) {
            invitations.remove(uuid)
        }
    }

    fun isInvited(uuid: UUID): Boolean {
        return invitations.containsKey(uuid) && (System.currentTimeMillis() - invitations[uuid]!!.invitedAt) < 30 * 60 * 1000
    }

    fun invitePlayer(uuid: UUID, invitedBy: UUID) {
        invitations[uuid] = GangInvite(invitedBy = invitedBy, invitedAt = System.currentTimeMillis())

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
        invitations.remove(uuid)

        val playerInvitedName = Cubed.instance.uuidCache.name(uuid)
        sendMessagesToMembers("${ChatColor.YELLOW}$playerInvitedName's invitation to join the gang has been revoked.")
    }

    fun addMember(member: GangMember) {
        members[member.uuid] = member
    }

    fun memberJoin(uuid: UUID) {
        val invite = invitations.remove(uuid)

        val member = GangMember(uuid, invite?.invitedBy, invite?.invitedAt)
        addMember(member)

        GangHandler.updateGangAccess(uuid = uuid, gang = this, joinable = true)

        sendMessagesToMembers("${ChatColor.YELLOW}${member.getUsername()} has joined the gang.")
    }

    fun memberLeave(uuid: UUID) {
        val memberName = Cubed.instance.uuidCache.name(uuid)
        sendMessagesToMembers("${ChatColor.YELLOW}$memberName has left the gang.")

        members.remove(uuid)

        val player = Bukkit.getPlayer(uuid)
        if (player != null) {
            leaveSession(player)
        }

        GangHandler.updateGangAccess(uuid = uuid, gang = this, joinable = false)
    }

    fun kickMember(uuid: UUID) {
        val removedMember = members.remove(uuid)
        if (removedMember != null) {
            sendMessagesToMembers("${ChatColor.YELLOW}${removedMember.getUsername()} has been kicked from the gang.")

            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                leaveSession(player)
                player.sendMessage("${ChatColor.YELLOW}You've been kicked from ${getLeaderUsername()}'s gang.")
            }
        }

        GangHandler.updateGangAccess(uuid = uuid, gang = this, joinable = false)
    }

    fun kickVisitors(force: Boolean = false) {
        for (visitor in getVisitingPlayers()) {
            if (force || !testPermission(visitor, GangPermission.ALLOW_VISITORS, false)) {
                leaveSession(visitor)
            }
        }
    }

    fun joinSession(player: Player) {
        if (!visitors.contains(player.uniqueId)) {
            visitors.add(player.uniqueId)

            val memberInfo = getMemberInfo(player.uniqueId)
            if (memberInfo != null) {
                memberInfo.lastPlayed = System.currentTimeMillis()
            }

            GangHandler.updateVisitingGang(player, this)

            Tasks.sync {
                player.allowFlight = true
                player.isFlying = true
                player.teleport(homeLocation)
            }

            sendMessagesToAll("${ChatColor.YELLOW}${player.name} has entered the gang headquarters.")
        }
    }

    fun leaveSession(player: Player) {
        if (visitors.contains(player.uniqueId)) {
            visitors.remove(player.uniqueId)

            val memberInfo = getMemberInfo(player.uniqueId)
            if (memberInfo != null) {
                memberInfo.lastPlayed = System.currentTimeMillis()
            }

            sendBorderUpdate(player)

            GangHandler.updateVisitingGang(player, null)

            Tasks.sync {
                player.allowFlight = false
                player.isFlying = false
                player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            }

            sendMessagesToAll("${ChatColor.YELLOW}${player.name} has left the gang headquarters.")
        }
    }

    fun getTrophies(): Int {
        return trophies
    }

    fun hasTrophies(amount: Int): Boolean {
        return trophies >= amount
    }

    fun setTrophies(amount: Int) {
        trophies = amount.coerceAtLeast(0)
    }

    fun giveTrophies(amount: Int) {
        trophies += amount
    }

    fun takeTrophies(amount: Int) {
        trophies = (trophies - amount).coerceAtLeast(0)
    }

    fun getBoosters(): MutableSet<GangBooster> {
        return boosters
    }

    fun hasBooster(boosterType: GangBooster.BoosterType): Boolean {
        return boosters.any { it.boosterType == boosterType && System.currentTimeMillis() < it.expiration }
    }

    fun getBooster(boosterType: GangBooster.BoosterType): GangBooster? {
        return boosters.first { it.boosterType == boosterType }
    }

    fun grantBooster(booster: GangBooster) {
        boosters.add(booster)

        val messages = arrayListOf<String>()
        messages.add("")
        messages.add(" ${ChatColor.GOLD}${ChatColor.BOLD}${booster.boosterType.rendered} Booster Activated")
        messages.add(" ${ChatColor.GRAY}(Purchased by ${Cubed.instance.uuidCache.name(booster.purchasedBy)})")
        messages.add("")
        messages.addAll(TextSplitter.split(length = 60, text = booster.boosterType.description, linePrefix = ChatColor.GRAY.toString() + " "))
        messages.add("")
        messages.add(" ${ChatColor.GRAY}The booster will last for ${ChatColor.YELLOW}${ChatColor.BOLD}${booster.boosterType.getFormattedDuration()}${ChatColor.GRAY}.")
        messages.add("")

        sendMessagesToAll(*messages.toTypedArray())
    }

    fun removeBooster(booster: GangBooster) {
        boosters.remove(booster)
    }

    fun sendMessagesToMembers(vararg messages: String) {
        for (player in getOnlineMembers()) {
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

    private fun sendBorderUpdate(player: Player) {
        val cuboid = getCuboid()

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

        if (!members.containsKey(player.uniqueId) && !visitors.contains(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You aren't allowed to build or break inside of ${this.getRegionName()}.")
            return
        }

        if (!testPermission(player, GangPermission.BUILD_AND_BREAK)) {
            return
        }

        if (!getCuboid().contains(block.location)) {
            player.sendMessage("${ChatColor.RED}You can't build or break outside of the gang's headquarters.")
            return
        }

        cancellable.isCancelled = false
    }

    fun updateCachedValue() {
        var sum = BigInteger("0")

        for (member in members.keys) {
            try {
                val user = UserHandler.getOrLoadAndCacheUser(member, lookup = false, throws = true)
                sum += BigInteger(DECIMAL_FORMAT.format(user.getMoneyBalance()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        cachedValue = sum
    }

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("#")
    }

}