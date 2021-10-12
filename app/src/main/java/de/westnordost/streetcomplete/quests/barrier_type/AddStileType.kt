package de.westnordost.streetcomplete.quests.barrier_type

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataWithGeometry
import de.westnordost.streetcomplete.data.osm.osmquests.OsmElementQuestType
import de.westnordost.streetcomplete.data.user.achievements.QuestTypeAchievement.OUTDOORS

class AddStileType : OsmElementQuestType<BarrierType> {

    private val stileNodeFilter by lazy { """
        nodes with barrier = stile and !stile
        or stile older today -8 years
    """.toElementFilterExpression() }

    private val excludedWaysFilter by lazy { """
        ways with
          access ~ private|no
          and foot !~ permissive|yes|designated
    """.toElementFilterExpression() }

    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> {
        val excludedWayNodeIds = mutableSetOf<Long>()
        mapData.ways
            .filter { excludedWaysFilter.matches(it) }
            .flatMapTo(excludedWayNodeIds) { it.nodeIds }

        return mapData.nodes
            .filter { stileNodeFilter.matches(it) && it.id !in excludedWayNodeIds }
    }

    override fun isApplicableTo(element: Element): Boolean? =
        if (!stileNodeFilter.matches(element)) false else null

    override val commitMessage = "Add specific stile type"
    override val wikiLink = "Key:stile"
    override val icon = R.drawable.ic_quest_cow
    override val isDeleteElementEnabled = true

    override val questTypeAchievements = listOf(OUTDOORS)

    override fun getTitle(tags: Map<String, String>) = R.string.quest_stile_type_title

    override fun createForm() = AddStileTypeForm()

    private fun deleteDetailedTags(changes: StringMapChangesBuilder){
        // result of looking through sample of stiles and
        // noting which tags can be assumed to become invalid on stile type change
        changes.deleteIfExists("step_count")
        changes.deleteIfExists("wheelchair")
        changes.deleteIfExists("bicycle")
        changes.deleteIfExists("dog_gate")
        changes.deleteIfExists("material")
        changes.deleteIfExists("height")
        changes.deleteIfExists("width")
        changes.deleteIfExists("stroller")
        changes.deleteIfExists("steps")
    }

    override fun applyAnswerTo(answer: BarrierType, changes: StringMapChangesBuilder) {
        when (answer) {
            BarrierType.STILE_SQUEEZER -> {
                val newStileType = "squeezer"
                if(changes.getPreviousValue("stile") != newStileType) {
                    deleteDetailedTags(changes)
                }
                changes.updateWithCheckDate("stile", newStileType)
            }
            BarrierType.STILE_LADDER -> {
                val newStileType = "ladder"
                if(changes.getPreviousValue("stile") != newStileType) {
                    deleteDetailedTags(changes)
                }
                changes.updateWithCheckDate("stile", newStileType)
            }
            BarrierType.STILE_STEPOVER_WOODEN -> {
                val newStileType = "stepover"
                val newStileMaterial = "wood"
                if(changes.getPreviousValue("stile") != newStileType) {
                    deleteDetailedTags(changes)
                }
                if(changes.getPreviousValue("material") != newStileMaterial) {
                    deleteDetailedTags(changes)
                }
                changes.updateWithCheckDate("stile", newStileType)
                changes.add("material", newStileMaterial)
            }
            BarrierType.STILE_STEPOVER_STONE -> {
                val newStileType = "stepover"
                val newStileMaterial = "stone"
                if(changes.getPreviousValue("stile") != newStileType) {
                    deleteDetailedTags(changes)
                }
                if(changes.getPreviousValue("material") != newStileMaterial) {
                    deleteDetailedTags(changes)
                }
                changes.updateWithCheckDate("stile", newStileType)
                changes.add("material", newStileMaterial)
            }
            BarrierType.KISSING_GATE -> {
                deleteDetailedTags(changes)
                changes.deleteIfExists("stile")
                changes.modify("barrier", "kissing_gate")
            }
        }

    }
}
