package mctmods.immersivetechnology.common.multiblocks.metal.recipe;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.Lists;
import mctmods.immersivetechnology.api.convergence.HeatCapabilities;
import mctmods.immersivetechnology.core.registration.ITRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class BoilerLiquidRecipe extends MultiblockRecipe {
    public static DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>, IERecipeSerializer<BoilerLiquidRecipe>> SERIALIZER;
    public static final CachedRecipeList<BoilerLiquidRecipe> RECIPES = new CachedRecipeList<>(ITRecipeTypes.BOILER_LIQUID);

    public final SizedFluidIngredient input;
    private final int time;
    private final double heatPerTick;
    private final double targetHeat;
    Lazy<Integer> totalProcessTime;

    public BoilerLiquidRecipe(SizedFluidIngredient input, int time, double heatPerTick, double targetHeat) {
        super(TagOutput.EMPTY, ITRecipeTypes.BOILER_LIQUID, 0, time, ITRecipeTypes.NO_MULTIPLIERS);
        this.input = input;
        this.time = time;
        this.heatPerTick = heatPerTick;
        this.targetHeat = Math.min(targetHeat, HeatCapabilities.MAX_HEAT);
        totalProcessTime = Lazy.of(() -> this.time);
        this.fluidInputList = Lists.newArrayList(this.input);
    }

    public static BoilerLiquidRecipe findRecipe(Level level, FluidStack input) {
        for (var holder : RECIPES.getRecipes(level)) { BoilerLiquidRecipe recipe = holder.value(); if (recipe.input.test(input)) return recipe; }
        return null;
    }

    @Override @NotNull public ItemStack getResultItem(HolderLookup.Provider provider) { return ItemStack.EMPTY; }

    @Override protected IERecipeSerializer<?> getIESerializer() { return SERIALIZER.get(); }

    @Override public int getTotalProcessTime() { return totalProcessTime.get(); }

    @Override public int getTotalProcessEnergy() { return 0; }

    @Override public int getMultipleProcessTicks() { return 0; }

    public double getHeatPerTick() { return heatPerTick; }

    public double getTargetHeat() { return targetHeat; }
}
