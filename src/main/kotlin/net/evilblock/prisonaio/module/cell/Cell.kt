package net.evilblock.prisonaio.module.cell

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.cell.entity.JerryNpcEntity
import net.evilblock.prisonaio.module.cell.permission.CellPermission
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.util.Constants
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.max

class Cell(
    val gridIndex: Int,
    var name: String,
    val owner: UUID,
    var homeLocation: Location,
    guideLocation: Location,
    internal var cuboid: Cuboid
) : Region {

    /**
     * The unique ID of this cell.
     */
    val uuid: UUID = UUID.randomUUID()

    /**
     * The members of this cell.
     */
    private val members: HashSet<UUID> = hashSetOf(owner)

    @Transient
    private var visitors: HashSet<UUID> = hashSetOf()

    /**
     * Players that have been invited to this cell.
     */
    private val invited: HashMap<UUID, Long> = hashMapOf()

    /**
     * The permissions structure of this cell.
     */
    private var permissions = hashMapOf<CellPermission, CellPermission.PermissionValue>()

    /**
     * The announcement, which is displayed in the "Jerry" menu and can be updated by the cell owner.
     */
    var announcement: String = "This is the default announcement."

    /**
     * The "Jerry The Prison Guard" NPC, otherwise known as the Guide.
     */
    internal var guideNpc: JerryNpcEntity = JerryNpcEntity(guideLocation)

    /**
     * The cell's cached cell value.
     */
    internal var cachedCellValue: Long = 0L

    override fun getRegionName(): String {
        return "${getOwnerUsername()}'s Cell"
    }

    override fun getBreakableRegion(): Cuboid? {
        return cuboid
    }

    fun initializeData() {
        visitors = hashSetOf()

        guideNpc.initializeData()
        guideNpc.persistent = false
        guideNpc.cell = this

        guideNpc.updateTexture(
            CellsModule.getJerryTextureValue(),
            CellsModule.getJerryTextureSignature()
        )

        guideNpc.updateLines(CellsModule.getJerryHologramLines())

        EntityManager.trackEntity(guideNpc)
    }

    override fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {
        if (Constants.CONTAINER_TYPES.contains(clickedBlock.type)) {
            if (!testPermission(player, CellPermission.ACCESS_CONTAINERS)) {
                cancellable.isCancelled = true
            }
        }

        if (Constants.INTERACTIVE_TYPES.contains(clickedBlock.type)) {
            if (!testPermission(player, CellPermission.INTERACT_WITH_BLOCKS)) {
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

        if (!testPermission(player, CellPermission.BUILD_AND_BREAK)) {
            return
        }

        if (!cuboid.contains(block.location)) {
            player.sendMessage("${ChatColor.RED}You can't build or break outside of the cell's borders.")
            return
        }

        cancellable.isCancelled = false
    }

    override fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        if (entity is Player) {
            cancellable.isCancelled = true
        }
    }

    override fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        if (victim is Player) {
            cancellable.isCancelled = true
        }
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

    fun getPermissionValue(permission: CellPermission): CellPermission.PermissionValue {
        return permissions.getOrDefault(permission, permission.getDefaultValue())
    }

    fun setPermissionValue(permission: CellPermission, value: CellPermission.PermissionValue) {
        permissions[permission] = value
    }

    fun testPermission(player: Player, permission: CellPermission, sendMessage: Boolean = true): Boolean {
        when (getPermissionValue(permission)) {
            CellPermission.PermissionValue.OWNER -> {
                if (player.uniqueId != owner) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }

                    return false
                }
            }
            CellPermission.PermissionValue.MEMBERS -> {
                if (!members.contains(player.uniqueId)) {
                    if (sendMessage) {
                        player.sendMessage("${ChatColor.RED}${permission.error}.")
                    }

                    return false
                }
            }
            CellPermission.PermissionValue.VISITORS -> {
                return true
            }
        }

        return true
    }

    fun getActiveMembers(): Set<Player> {
        return members.mapNotNull { Bukkit.getPlayer(it) }.filter { CellHandler.getVisitingCell(it) == this }.toSet()
    }

    fun isActiveMember(player: Player): Boolean {
        return getActiveMembers().contains(player)
    }

    fun getActiveVisitors(): Set<Player> {
        return visitors.mapNotNull { Bukkit.getPlayer(it) }.filter { CellHandler.getVisitingCell(it) == this }.toSet()
    }

    fun getActivePlayers(): Set<Player> {
        val players = hashSetOf<Player>()
        players.addAll(getActiveMembers())
        players.addAll(getActiveVisitors())
        return players
    }

    fun isActivePlayer(player: Player): Boolean {
        return getActivePlayers().contains(player)
    }

    fun isOwner(uuid: UUID): Boolean {
        return owner == uuid
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
        sendMessages("${ChatColor.YELLOW}$playerInvitedName has been invited to the cell by $invitedByName.")

        val playerInvited = Bukkit.getPlayer(uuid)
        if (playerInvited != null) {
            playerInvited.sendMessage("")
            playerInvited.sendMessage(" ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}New Invitation to Join Cell")
            playerInvited.sendMessage(" ${ChatColor.GRAY}You've been invited to join ${ChatColor.GREEN}${invitedByName}${ChatColor.GRAY}'s cell.")

            FancyMessage(" ")
                .then("${ChatColor.GRAY}[${ChatColor.GREEN}${ChatColor.BOLD}ACCEPT${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to accept the invitation."))
                .command("/cell join ${this.uuid}")
                .then(" ")
                .then("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}DECLINE${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to decline the invitation."))
                .command("/cell decline ${this.uuid}")
                .send(playerInvited)

            playerInvited.sendMessage("")
        }
    }

    fun revokeInvite(uuid: UUID) {
        invited.remove(uuid)

        val playerInvitedName = Cubed.instance.uuidCache.name(uuid)
        sendMessages("${ChatColor.YELLOW}$playerInvitedName's invitation to join the cell has been revoked.")
    }

    fun memberJoin(uuid: UUID) {
        invited.remove(uuid)
        members.add(uuid)

        CellHandler.updateJoinableCache(uuid = uuid, cell = this, joinable = true)

        val memberName = Cubed.instance.uuidCache.name(uuid)
        sendMessages("${ChatColor.YELLOW}$memberName has joined the cell.")
    }

    fun memberLeave(uuid: UUID) {
        if (members.remove(uuid)) {
            val memberName = Cubed.instance.uuidCache.name(uuid)
            sendMessages("${ChatColor.YELLOW}$memberName has left the cell.")

            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                // teleport player out of the mine
                if (isActiveMember(player)) {
                    player.teleport(Bukkit.getWorlds()[0].spawnLocation)
                }
            }
        }

        CellHandler.updateJoinableCache(uuid = uuid, cell = this, joinable = false)
    }

    fun kickMember(uuid: UUID) {
        if (members.remove(uuid)) {
            val memberName = Cubed.instance.uuidCache.name(uuid)
            sendMessages("${ChatColor.YELLOW}$memberName has been kicked from the cell.")

            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                if (isActiveMember(player)) {
                    CellHandler.updateVisitingCell(player, null)

                    Tasks.sync {
                        player.teleport(Bukkit.getWorlds()[0].spawnLocation)
                    }
                }

                player.sendMessage("${ChatColor.YELLOW}You've been kicked from ${getOwnerUsername()}'s cell.")
            }
        }

        CellHandler.updateJoinableCache(uuid = uuid, cell = this, joinable = false)
    }

    fun kickVisitors() {
        for (activePlayer in getActivePlayers()) {
            if (!testPermission(activePlayer, CellPermission.ALLOW_VISITORS, false)) {
                activePlayer.teleport(Bukkit.getWorlds()[0].spawnLocation)
                activePlayer.sendMessage("${ChatColor.YELLOW}${getOwnerUsername()}'s is no longer allowing visitors in their cell.")
            }
        }
    }

    fun joinSession(player: Player) {
        if (!members.contains(player.uniqueId)) {
            visitors.add(player.uniqueId)
        }

        // send message AFTER player is added
        sendMessages("${ChatColor.YELLOW}${player.name} has entered the cell.")
    }

    fun leaveSession(player: Player) {
        // send message BEFORE player is removed
        sendMessages("${ChatColor.YELLOW}${player.name} has left the cell.")

        visitors.remove(player.uniqueId)
    }

    fun updateName(sender: Player, name: String) {
        this.name = name

        sendMessages("${ChatColor.YELLOW}${sender.name} has updated the cell's name.")
    }

    fun updateAnnouncement(sender: Player, announcement: String) {
        this.announcement = announcement

        sendMessages("${ChatColor.YELLOW}${sender.name} has updated the cell's announcement.")
    }

    fun sendMessages(vararg messages: String) {
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

        VaultHook.useEconomy { economy ->
            for (member in members) {
                totalBalance += economy.getBalance(Bukkit.getOfflinePlayer(member)).toLong()
            }
        }

        cachedCellValue = totalBalance
    }

}