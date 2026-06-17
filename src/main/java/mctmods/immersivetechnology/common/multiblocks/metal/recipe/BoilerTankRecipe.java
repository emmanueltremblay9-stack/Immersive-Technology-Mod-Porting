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

public class BoilerTankRecipe extends MultiblockRecipe {
    public static DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>, IERecipeSerializer<BoilerTankRecipe>> SERIALIZER;
    public static final CachedRecipeList<BoilerTankRecipe> RECIPES = new CachedRecipeList<>(ITRecipeTypes.BOILER_TANK);

    public final SizedFluidIngredient input;
    public final FluidStack output;
    public final double requiredHeat;
    Lazy<Integer> totalProcessTime;

    public BoilerTankRecipe(SizedFluidIngredient input, FluidStack output, int time, double requiredHeat) {
        super(TagOutput.EMPTY, ITRecipeTypes.BOILER_TANK, 0, time, ITRecipeTypes.NO_MULTIPLIERS);
        this.input = input;
        this.output = output;
        this.requiredHeat = Math.min(requiredHeat, HeatCapabilities.MAX_HEAT);
        totalProcessTime = Lazy.of(() -> time);

        this.fluidInputList = Lists.newArrayList(this.input);
        this.fluidOutputList = Lists.newArrayList(this.output);
    }

    @Override protected IERecipeSerializer<?> getIESerializer() {
        return SERIALIZER.get();
    }

    @Override @NotNull public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public static BoilerTankRecipe findRecipe(Level level, FluidStack input) {
        for (var holder : RECIPES.getRecipes(level)) { BoilerTankRecipe recipe = holder.value(); if (recipe.input.test(input)) return recipe; }
        return null;
    }

    @Override public int getTotalProcessTime() {
        return totalProcessTime.get();
    }

    @Override public int getTotalProcessEnergy() {
        return 0;
    }

    @Override public int getMultipleProcessTicks() {
        return 0;
    }
}
