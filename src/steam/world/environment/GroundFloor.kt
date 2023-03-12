package steam.world.environment

import arc.graphics.g2d.TextureRegion
import mindustry.world.Tile
import mindustry.world.blocks.environment.OverlayFloor

class GroundFloor(name: String) : OverlayFloor(name) {
    override fun drawBase(tile: Tile) {}
    override fun editorIcon(): TextureRegion {
        return region
    }
}