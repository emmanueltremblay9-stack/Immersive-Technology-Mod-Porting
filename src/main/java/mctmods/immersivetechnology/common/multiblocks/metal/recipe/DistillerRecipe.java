package mctmods.immersivetechnology.common.multiblocks.metal.recipe;

import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.TagOutputList;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.Lists;
import mctmods.immersivetechnology.core.registration.ITRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DistillerRecipe extends MultiblockRecipe {
    public static DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>, IERecipeSerializer<DistillerRecipe>> SERIALIZER;
    public static final CachedRecipeList<DistillerRecipe> RECIPES = new CachedRecipeList<>(ITRecipeTypes.DISTILLER);

    public final SizedFluidIngredient input;
    @Nullable public final FluidStack fluidOutput;
    public final ItemStack itemOutput;
    public final float chance;
    private final int time;
    private final int energy;
    Lazy<Integer> totalProcessTime;
    Lazy<Integer> totalProcessEnergy;

    public DistillerRecipe(SizedFluidIngredient input, @Nullable FluidStack fluidOutput, ItemStack itemOutput, float chance, int time, int energy) {
        super(TagOutput.EMPTY, ITRecipeTypes.DISTILLER, energy, time, ITRecipeTypes.NO_MULTIPLIERS);
        this.input = input;
        this.fluidOutput = fluidOutput;
        this.itemOutput = itemOutput;
        this.chance = chance;
        this.time = time;
        this.energy = energy;

        totalProcessTime = Lazy.of(() -> this.time);
        totalProcessEnergy = Lazy.of(() -> this.energy);

        this.fluidInputList = Lists.newArrayList(this.input);
        if (this.fluidOutput != null) this.fluidOutputList = Lists.newArrayList(this.fluidOutput);
        this.outputList = itemOutput.isEmpty() ? TagOutputList.EMPTY : new TagOutputList(new TagOutput(itemOutput));
    }

    public static DistillerRecipe findRecipe(Level level, FluidStack inputFluid) {
        RecipeHolder<DistillerRecipe> holder = findRecipeHolder(level, inputFluid);
        return holder != null ? holder.value() : null;
    }

    public static RecipeHolder<DistillerRecipe> findRecipeHolder(Level level, FluidStack inputFluid) {
        for (var holder : RECIPES.getRecipes(level)) { DistillerRecipe recipe = holder.value(); if (recipe.input.test(inputFluid)) return holder; }
        return null;
    }

    @Override @NotNull public ItemStack getResultItem(HolderLookup.Provider provider) { return ItemStack.EMPTY; }

    @Override protected IERecipeSerializer<?> getIESerializer() { return SERIALIZER.get(); }

    @Override public int getTotalProcessTime() { return totalProcessTime.get(); }

    @Override public int getTotalProcessEnergy() { return totalProcessEnergy.get(); }

    @Override public int getMultipleProcessTicks() { return 0; }
}
