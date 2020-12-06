/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.impl.statistic

import com.google.common.collect.HashBasedTable
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.font.MinecraftFontCharacter
import org.apache.commons.lang3.StringUtils
import org.bukkit.ChatColor

class GraphicalTable {

    companion object {
        val MARGIN = "${ChatColor.RESET}  "
        val BORDER = "${ChatColor.GRAY}${Constants.THIN_VERTICAL_LINE}"

        val SPACE_WIDTH = MinecraftFontCharacter.getWidth(" ")
        val HYPHEN_WIDTH = (MinecraftFontCharacter.getWidth("-") * 1.52).toInt()
    }

    private var title: String? = null
    private val table = HashBasedTable.create<Int, Int, String>()
    private var borders: Boolean = false

    fun title(useTitle: String): GraphicalTable {
        title = useTitle
        return this
    }

    fun borders(useBorders: Boolean): GraphicalTable {
        borders = useBorders
        return this
    }

    fun addEntry(row: Int, column: Int, text: String): GraphicalTable {
        table.put(row, column, text)
        return this
    }

    // render top down
    fun render(): List<String> {
        return arrayListOf<String>().also { desc ->
            var firstColWidth = 0
            for (line in table.rowKeySet()) {
                if (line == 1) {
                    val firstLineWidth = MinecraftFontCharacter.getWidth(desc.first())

                    var hyphensNeeded = (firstLineWidth / HYPHEN_WIDTH)
                    if (firstLineWidth % HYPHEN_WIDTH >= (HYPHEN_WIDTH / 2.0)) {
                        hyphensNeeded++
                    }

                    desc.add("${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}${StringUtils.repeat('-', hyphensNeeded)}")
                }

                desc.add(buildString {
                    var index = 0
                    val row = table.row(line)
                    for ((column, text) in row) {
                        index++

                        if (text.isEmpty()) {
                            append(" ")
                        } else {
                            append(text)
                        }

                        val colMinWidth = findMinWidth(column)
                        if (column == 0) {
                            firstColWidth = colMinWidth
                        }

                        val rawTextWidth = MinecraftFontCharacter.getWidth(ChatColor.stripColor(text))
                        if (rawTextWidth < colMinWidth) {
                            val widthNeeded = colMinWidth - rawTextWidth
                            if (widthNeeded > 0) {
                                var spacesNeeded = widthNeeded / SPACE_WIDTH
                                if (widthNeeded % SPACE_WIDTH >= (SPACE_WIDTH / 2.0)) {
                                    spacesNeeded++
                                }

                                append(StringUtils.repeat(' ', spacesNeeded))
                            }
                        }

                        append(MARGIN)

                        if (index != 0 && index != row.size && borders) {
                            append(BORDER)
                            append(MARGIN)
                        }
                    }
                })
            }

            if (title != null) {
                var spacesNeeded = (firstColWidth / SPACE_WIDTH)
                if (spacesNeeded % SPACE_WIDTH >= (SPACE_WIDTH / 2.0)) {
                    spacesNeeded++
                }

                desc.add(0, "")
                desc.add(0, "${StringUtils.repeat(' ', spacesNeeded)}$MARGIN $title")
            }
        }
    }

    /**
     * Find a column's minimum width, which is the cell in the column with the most width.
     */
    private fun findMinWidth(column: Int): Int {
        var minWidth = 0
        for (row in table.column(column)) {
            val lineWidth = MinecraftFontCharacter.getWidth(ChatColor.stripColor(row.value))
            if (lineWidth > minWidth) {
                minWidth = lineWidth
            }
        }
        return minWidth
    }

}