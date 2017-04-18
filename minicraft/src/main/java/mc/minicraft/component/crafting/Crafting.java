package mc.minicraft.component.crafting;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.entity.*;
import mc.minicraft.component.item.ToolType;
import mc.minicraft.component.item.resource.Resource;
import mc.api.Sound;

import java.util.ArrayList;
import java.util.List;

public final class Crafting {
    public static final List<Recipe> anvilRecipes = new ArrayList<Recipe>();
    public static final List<Recipe> ovenRecipes = new ArrayList<Recipe>();
    public static final List<Recipe> furnaceRecipes = new ArrayList<Recipe>();
    public static final List<Recipe> workbenchRecipes = new ArrayList<Recipe>();

    public static void init(Sound sound, PlayerHandler handler, PropertyReader reader) {
        anvilRecipes.clear();
        ovenRecipes.clear();
        furnaceRecipes.clear();
        workbenchRecipes.clear();
        try {
            workbenchRecipes.add(new FurnitureRecipe(Lantern.class, sound, handler, reader)
                    .addCost(Resource.wood, 5).addCost(Resource.slime, 10).addCost(Resource.glass, 4));

            workbenchRecipes.add(new FurnitureRecipe(Oven.class, sound, handler, reader).addCost(Resource.stone, 15));
            workbenchRecipes.add(new FurnitureRecipe(Furnace.class, sound, handler, reader).addCost(Resource.stone, 20));
            workbenchRecipes.add(new FurnitureRecipe(Workbench.class, sound, handler, reader).addCost(Resource.wood, 20));
            workbenchRecipes.add(new FurnitureRecipe(Chest.class, sound, handler, reader).addCost(Resource.wood, 20));
            workbenchRecipes.add(new FurnitureRecipe(Anvil.class, sound, handler, reader).addCost(Resource.ironIngot, 5));

            workbenchRecipes.add(new ToolRecipe(ToolType.sword, 0).addCost(Resource.wood, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.axe, 0).addCost(Resource.wood, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.hoe, 0).addCost(Resource.wood, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.pickaxe, 0).addCost(Resource.wood, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.shovel, 0).addCost(Resource.wood, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.sword, 1).addCost(Resource.wood, 5).addCost(Resource.stone, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.axe, 1).addCost(Resource.wood, 5).addCost(Resource.stone, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.hoe, 1).addCost(Resource.wood, 5).addCost(Resource.stone, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.pickaxe, 1).addCost(Resource.wood, 5).addCost(Resource.stone, 5));
            workbenchRecipes.add(new ToolRecipe(ToolType.shovel, 1).addCost(Resource.wood, 5).addCost(Resource.stone, 5));

            anvilRecipes.add(new ToolRecipe(ToolType.sword, 2).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.axe, 2).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.hoe, 2).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.pickaxe, 2).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.shovel, 2).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5));

            anvilRecipes.add(new ToolRecipe(ToolType.sword, 3).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.axe, 3).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.hoe, 3).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.pickaxe, 3).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5));
            anvilRecipes.add(new ToolRecipe(ToolType.shovel, 3).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5));

            anvilRecipes.add(new ToolRecipe(ToolType.sword, 4).addCost(Resource.wood, 5).addCost(Resource.gem, 50));
            anvilRecipes.add(new ToolRecipe(ToolType.axe, 4).addCost(Resource.wood, 5).addCost(Resource.gem, 50));
            anvilRecipes.add(new ToolRecipe(ToolType.hoe, 4).addCost(Resource.wood, 5).addCost(Resource.gem, 50));
            anvilRecipes.add(new ToolRecipe(ToolType.pickaxe, 4).addCost(Resource.wood, 5).addCost(Resource.gem, 50));
            anvilRecipes.add(new ToolRecipe(ToolType.shovel, 4).addCost(Resource.wood, 5).addCost(Resource.gem, 50));

            furnaceRecipes.add(new ResourceRecipe(Resource.ironIngot).addCost(Resource.ironOre, 4).addCost(Resource.coal, 1));
            furnaceRecipes.add(new ResourceRecipe(Resource.goldIngot).addCost(Resource.goldOre, 4).addCost(Resource.coal, 1));
            furnaceRecipes.add(new ResourceRecipe(Resource.glass).addCost(Resource.sand, 4).addCost(Resource.coal, 1));

            ovenRecipes.add(new ResourceRecipe(Resource.bread).addCost(Resource.wheat, 4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}