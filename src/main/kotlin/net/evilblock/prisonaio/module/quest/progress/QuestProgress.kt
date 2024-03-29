/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.progress

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.serialize.QuestReferenceSerializer
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import java.lang.reflect.Type

open class QuestProgress(@JsonAdapter(QuestReferenceSerializer::class) internal var quest: Quest) {

    @Transient internal var requiresSave: Boolean = false

    protected var started: Boolean = false
    protected var completed: Boolean = false

    @JsonAdapter(QuestMissionSetSerializer::class)
    protected val completedMissions: MutableSet<QuestMission> = hashSetOf()

    open fun initializeData() {

    }

    fun hasStarted(): Boolean {
        return started
    }

    fun start() {
        completedMissions.clear()
        completed = false
        started = true
        requiresSave = true
    }

    fun isCompleted(): Boolean {
        return completed
    }

    fun complete() {
        completed = true
        requiresSave = true
    }

    fun hasCurrentMission(): Boolean {
        if (completedMissions.isEmpty()) {
            return quest.getSortedMissions().isNotEmpty()
        }

        val lastCompletedMission = completedMissions.maxBy { it.getOrder() }!!
        val currentMissionIndex = quest.getSortedMissions().indexOf(lastCompletedMission) + 1
        return currentMissionIndex < quest.getSortedMissions().size
    }

    fun getCurrentMission(): QuestMission {
        if (!started) {
            throw IllegalStateException("Can't find current mission because quest hasn't been started")
        }

        if (isCompleted()) {
            throw IllegalStateException("Can't find current mission because quest is complete")
        }

        if (completedMissions.isEmpty()) {
            return quest.getSortedMissions().first()
        }

        val lastCompletedMission = completedMissions.maxBy { it.getOrder() }!!
        val currentMissionIndex = quest.getSortedMissions().indexOf(lastCompletedMission) + 1

        if (currentMissionIndex >= quest.getSortedMissions().size) {
            throw IllegalStateException("No next mission in quest")
        }

        return quest.getSortedMissions()[currentMissionIndex]
    }

    fun hasCompletedMission(mission: QuestMission): Boolean {
        return completedMissions.contains(mission)
    }

    fun markMissionCompleted(mission: QuestMission) {
        completedMissions.add(mission)
        requiresSave = true
    }

    class Serializer : JsonSerializer<QuestProgress>, JsonDeserializer<QuestProgress> {
        override fun serialize(src: QuestProgress, typeOf: Type, context: JsonSerializationContext): JsonElement {
            val json = JsonObject()
            json.addProperty("quest", src.quest.getId())
            json.addProperty("type", src::class.java.name)
            json.add("properties", context.serialize(src, src::class.java))
            return json
        }

        override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): QuestProgress {
            val jsonObject = json.asJsonObject
            val questId = jsonObject.get("quest").asString
            val type = jsonObject.get("type").asString
            val properties = jsonObject.get("properties")

            try {
                val quest = QuestHandler.getQuestById(questId) ?: throw IllegalStateException("Quest doesn't exist")
                return context.deserialize<QuestProgress>(properties, Class.forName(type)).also { it.quest = quest }
            } catch (e: ClassNotFoundException) {
                throw JsonParseException("Unknown type: $type", e)
            }
        }
    }

}