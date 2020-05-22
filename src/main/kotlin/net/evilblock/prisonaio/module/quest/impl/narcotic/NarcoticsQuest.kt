package net.evilblock.prisonaio.module.quest.impl.narcotic

import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.impl.narcotic.mission.DeliverDrugsMission
import net.evilblock.prisonaio.module.quest.impl.narcotic.mission.DeliverMoneyMission
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import net.evilblock.prisonaio.module.quest.impl.narcotic.mission.MeetPabloEscobarMission
import net.evilblock.prisonaio.module.quest.impl.narcotic.mission.MeetLexLuthorMission
import net.evilblock.prisonaio.module.quest.impl.narcotic.progression.NarcoticsQuestProgression
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object NarcoticsQuest : Quest<NarcoticsQuest> {

    override fun getId(): String {
        return "nacrotics"
    }

    override fun getName(): String {
        return "Drugs"
    }

    override fun getSortedMissions(): List<QuestMission<NarcoticsQuest>> {
        return listOf<QuestMission<NarcoticsQuest>>(
            MeetLexLuthorMission,
            MeetPabloEscobarMission,
            DeliverDrugsMission,
            DeliverMoneyMission
        ).sortedBy { it.getOrder() }
    }

    override fun getCompletionText(): String {
        return "Pablo is very grateful for you delivering his drugs for him. He has granted you access to his seed shop, which can be used to grow and manufacture narcotics to distribute to dealers for profit."
    }

    override fun getStartText(): String {
        return "You can start this quest by talking to Lex Luthor located near the Outdoors Tavern (78, 51, -29)."
    }

    override fun hasRewards(): Boolean {
        return true
    }

    override fun getRewardsText(): List<String> {
        return listOf(
            "${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}6,500,000",
            "${ChatColor.GRAY}Access to ${ChatColor.YELLOW}${ChatColor.BOLD}Pablo's Drug Shop",
            "${ChatColor.GRAY}Access to ${ChatColor.YELLOW}${ChatColor.BOLD}Pablo's Drug Dealers"
        )
    }

    override fun startProgress(): NarcoticsQuestProgression {
        return NarcoticsQuestProgression()
    }

    override fun getProgress(player: Player): NarcoticsQuestProgression {
        return super.getProgress(player) as NarcoticsQuestProgression
    }

}