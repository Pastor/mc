package mc.minicraft.component.item.resource;

import mc.minicraft.component.entity.Player;
import mc.minicraft.component.item.resource.Resource;
import mc.minicraft.component.level.Level;
import mc.minicraft.component.level.tile.Tile;

import java.util.Arrays;
import java.util.List;

public class PlantableResource extends Resource {
    private List<Tile> sourceTiles;
    private Tile targetTile;

    public PlantableResource(String name, int sprite, int color, Tile targetTile, Tile... sourceTiles1) {
        this(name, sprite, color, targetTile, Arrays.asList(sourceTiles1));
    }

    public PlantableResource(String name, int sprite, int color, Tile targetTile, List<Tile> sourceTiles) {
        super(name, sprite, color);
        this.sourceTiles = sourceTiles;
        this.targetTile = targetTile;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (sourceTiles.contains(tile)) {
            level.setTile(xt, yt, targetTile, 0);
            return true;
        }
        return false;
    }
}
