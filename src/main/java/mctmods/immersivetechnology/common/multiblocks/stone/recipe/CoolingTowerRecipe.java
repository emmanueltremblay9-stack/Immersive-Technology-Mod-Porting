package mctmods.immersivetechnology.common.multiblocks.stone.recipe;

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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class CoolingTowerRecipe extends MultiblockRecipe {
    public static DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>, IERecipeSerializer<CoolingTowerRecipe>> SERIALIZER;
    public static final CachedRecipeList<CoolingTowerRecipe> RECIPES = new CachedRecipeList<>(ITRecipeTypes.COOLING_TOWER);

    public final FluidStack fluidOutput0;
    public final FluidStack fluidOutput1;
    public final FluidStack fluidOutput2;
    public final SizedFluidIngredient input0;
    public final SizedFluidIngredient input1;
    public final int totalProcessTime;
    private static final Lazy<Integer> totalProcessEnergy = Lazy.of(() -> 0);

    public CoolingTowerRecipe(FluidStack fluidOutput0, FluidStack fluidOutput1, FluidStack fluidOutput2, SizedFluidIngredient input0, SizedFluidIngredient input1, int time) {
        super(TagOutput.EMPTY, ITRecipeTypes.COOLING_TOWER, 0, time, ITRecipeTypes.NO_MULTIPLIERS);
        this.fluidOutput0 = fluidOutput0;
        this.fluidOutput1 = fluidOutput1;
        this.fluidOutput2 = fluidOutput2;
        this.input0 = input0;
        this.input1 = input1;
        this.totalProcessTime = time;
        this.fluidInputList = Lists.newArrayList(input0, input1);
        this.fluidOutputList = Lists.newArrayList(fluidOutput0, fluidOutput1, fluidOutput2);
        this.outputList = TagOutputList.EMPTY;
    }

    public static CoolingTowerRecipe findRecipe(Level level, FluidStack fluidInput0, FluidStack fluidInput1) {
        if (fluidInput0.isEmpty() || fluidInput1.isEmpty()) return null;
        for (var holder : RECIPES.getRecipes(level)) {
            CoolingTowerRecipe r = holder.value();
            if (r.input0.test(fluidInput0) && fluidInput0.getAmount() >= r.input0.amount() && r.input1.test(fluidInput1) && fluidInput1.getAmount() >= r.input1.amount()) return r;
        }
        return null;
    }

    @Override public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) { return ItemStack.EMPTY; }

    @Override protected IERecipeSerializer<?> getIESerializer() { return SERIALIZER.get(); }

    @Override public int getTotalProcessTime() { return totalProcessTime; }

    @Override public int getTotalProcessEnergy() { return totalProcessEnergy.get(); }

    @Override public int getMultipleProcessTicks() { return 0; }
}
