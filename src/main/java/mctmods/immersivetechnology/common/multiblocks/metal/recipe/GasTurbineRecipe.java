package mctmods.immersivetechnology.common.multiblocks.metal.recipe;

import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import mctmods.immersivetechnology.core.registration.ITRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class GasTurbineRecipe extends IESerializableRecipe {
    public static DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>, IERecipeSerializer<GasTurbineRecipe>> SERIALIZER;
    public static final CachedRecipeList<GasTurbineRecipe> RECIPES = new CachedRecipeList<>(ITRecipeTypes.GAS_TURBINE);

    public final SizedFluidIngredient input;
    @Nullable public final FluidStack fluidOutput;
    /**
     * Dimensionless torque multiplier applied to mechanical output while this recipe is used.
     *
     * <p>Defaults to {@code 1.0f} for backwards compatibility with older datapacks.</p>
     */
    public final float torque;
    private final int time;
    Lazy<Integer> totalProcessTime;

    public GasTurbineRecipe(SizedFluidIngredient input, @Nullable FluidStack fluidOutput, int time, float torque) {
        super(TagOutput.EMPTY, ITRecipeTypes.GAS_TURBINE);
        this.input = input;
        this.fluidOutput = fluidOutput;
        this.time = time;
        this.torque = torque;
        totalProcessTime = Lazy.of(() -> this.time);
    }

    @Override protected IERecipeSerializer<?> getIESerializer() { return SERIALIZER.get(); }

    @Override @NotNull public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) { return ItemStack.EMPTY; }

    public boolean matches(FluidStack fluid) { return input.test(fluid); }

    public static GasTurbineRecipe findRecipe(Level level, FluidStack fluid, @Nullable GasTurbineRecipe hint) {
        if (hint != null && hint.matches(fluid)) return hint;
        for (var holder : RECIPES.getRecipes(level)) { GasTurbineRecipe recipe = holder.value(); if (recipe.matches(fluid)) return recipe; }
        return null;
    }

    public int getTotalProcessTime() { return totalProcessTime.get(); }
}
