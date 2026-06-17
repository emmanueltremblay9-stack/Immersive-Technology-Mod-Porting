package mctmods.immersivetechnology.common.multiblocks.metal.recipe;

import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.Lists;
import mctmods.immersivetechnology.core.registration.ITRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

public class SolarTowerRecipe extends MultiblockRecipe {
    public static DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>, IERecipeSerializer<SolarTowerRecipe>> SERIALIZER;
    public static final CachedRecipeList<SolarTowerRecipe> RECIPES = new CachedRecipeList<>(ITRecipeTypes.SOLAR_TOWER);

    public final SizedFluidIngredient input;
    public final FluidStack fluidOutput;
    private final int time;
    public final double requiredTemp;

    public SolarTowerRecipe(SizedFluidIngredient input, @Nullable FluidStack fluidOutput, int time, double requiredTemp) {
        super(TagOutput.EMPTY, ITRecipeTypes.SOLAR_TOWER, 0, time, ITRecipeTypes.NO_MULTIPLIERS);
        this.input = input;
        this.fluidOutput = fluidOutput;
        this.time = time;
        this.requiredTemp = requiredTemp;
        this.fluidInputList = Lists.newArrayList(this.input);
        this.fluidOutputList = fluidOutput == null ? Lists.newArrayList() : Lists.newArrayList(fluidOutput);
    }

    @Nullable public static SolarTowerRecipe findRecipe(Level level, FluidStack fluid) {
        if (fluid == null || fluid.isEmpty()) return null;
        for (var holder : RECIPES.getRecipes(level)) { SolarTowerRecipe recipe = holder.value();
            if (recipe.input.ingredient().test(fluid) && fluid.getAmount() >= recipe.input.amount()) return recipe;
        }
        return null;
    }

    @Override protected IERecipeSerializer<?> getIESerializer() { return SERIALIZER.get(); }

    @Override public int getMultipleProcessTicks() { return 0; }

    @Override public int getTotalProcessTime() { return time; }

    @Override public int getTotalProcessEnergy() { return 0; }
}
