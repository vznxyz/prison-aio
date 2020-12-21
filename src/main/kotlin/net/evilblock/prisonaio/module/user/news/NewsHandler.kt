/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.user.UsersModule
import java.io.File
import java.util.*

object NewsHandler : PluginHandler() {

    private val news: MutableMap<UUID, News> = hashMapOf()

    override fun getModule(): PluginModule {
        return UsersModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "news.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<News>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<News>

                for (news in list) {
                    this.news[news.id] = news
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(news.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getAllNews(): Collection<News> {
        return news.values
    }

    fun getPublicNews(): Collection<News> {
        return news.values.filter { !it.hidden }.sortedBy { it.createdAt }
    }

    fun getLatestNews(): News? {
        return news.values.filter { !it.hidden }.maxBy { it.createdAt }
    }

    fun getNewsById(id: UUID): News? {
        return news[id]
    }

    fun trackNews(post: News) {
        news[post.id] = post
    }

    fun forgetNews(post: News) {
        news.remove(post.id)
    }

}