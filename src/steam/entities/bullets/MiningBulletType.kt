package steam.entities.bullets

import arc.util.Time
import mindustry.gen.Bullet
import mindustry.gen.Call
import mindustry.gen.Itemsc

class MiningBulletType : SteamBaseBulletType() {
    var mineAmount = 3
    var mineTier = 3

    override fun despawned(b: Bullet) {
        super.despawned(b)
        val drop = b.tileOn().drop() ?: return
        (b.owner as? Itemsc)?.run {
            if (drop.hardness <= mineTier && acceptsItem(drop)) for (i in 0 until mineAmount.coerceAtMost(20))
                Time.run(i / 3f) { Call.transferItemToUnit(drop, b.x, b.y, this) }
        }
    }
}