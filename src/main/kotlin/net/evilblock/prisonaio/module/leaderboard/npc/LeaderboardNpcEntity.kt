package net.evilblock.prisonaio.module.leaderboard.npc

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import org.bukkit.Location
import java.lang.reflect.Type

class LeaderboardNpcEntity(@JsonAdapter(LeaderboardReferenceSerializer::class) internal val leaderboard: Leaderboard, location: Location) : NpcEntity(lines = listOf(""), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateLines(leaderboard.getDisplayLines())
    }

    class LeaderboardReferenceSerializer : JsonSerializer<Leaderboard>, JsonDeserializer<Leaderboard> {
        override fun serialize(leaderboard: Leaderboard, type: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(leaderboard.id)
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext?): Leaderboard? {
            return LeaderboardsModule.getLeaderboardById(json.asString)
        }
    }

}