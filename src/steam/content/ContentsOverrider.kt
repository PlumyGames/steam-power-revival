package steam.content

import mindustry.content.Blocks.*
import mindustry.content.Items
import mindustry.content.UnitTypes.alpha
import mindustry.content.UnitTypes.beta
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.world.blocks.production.Drill
import mindustry.world.consumers.ConsumeLiquid
import mindustry.world.meta.BuildVisibility
import steam.world.distribution.ElectricConveyor

object ContentsOverrider {
    fun mechanicalDrill() {
        (mechanicalDrill as Drill).apply {
            removeConsumer(findConsumer { it is ConsumeLiquid })
            consumeLiquid(SteamFluids.steam, 0.05f)
            drillTime = 300f
            liquidBoostIntensity = 1f
        }
    }

    fun pneumaticDrill() {
        (pneumaticDrill as Drill).apply {
            removeConsumer(findConsumer { it is ConsumeLiquid })
            consumeLiquid(SteamFluids.steam, 0.05f)
            drillTime = 200f
            liquidBoostIntensity = 1f
        }
    }

    fun conveyor() {
/*        FakeVanilla {
            Vars.content.blocks().remove(conveyor)
            val allNameMap = Reflect.get<Array<ObjectMap<String, MappableContent>>>(Vars.content, "contentNameMap")
            allNameMap[ContentType.block.ordinal].remove("conveyor")*/
        ElectricConveyor("conveyor").apply {
            requirements(Category.distribution, ItemStack.with(Items.copper, 1), true)
            health = 45
            speed = 0.03f
            displayedSpeed = 4.2f
            buildCostMultiplier = 2f
            hasPower = true
            consumesPower = true
            conductivePower = true
            consumePower(1f / 60f)
        }
        conveyor.apply {
            buildVisibility = BuildVisibility.hidden
        }
        //}
    }

    fun alpha() {
        (alpha as UnitType).apply {
            mineTier = 2
        }
    }

    fun beta() {
        (beta as UnitType).apply {
            mineTier = 2
        }
    }
}