package mctmods.immersivetechnology.common.multiblocks.metal.recipe.builder;

import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import mctmods.immersivetechnology.core.compat.ie.IEFinishedRecipe;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.DistillerRecipe;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class DistillerRecipeBuilder extends IEFinishedRecipe<DistillerRecipeBuilder> {
    public DistillerRecipeBuilder() {
        super(DistillerRecipe.SERIALIZER.get());
        this.maxInputCount = 2;
    }

    public static DistillerRecipeBuilder builder(SizedFluidIngredient fluidIn, FluidStack primaryFluidOutput, int time, int energy) {
        return new DistillerRecipeBuilder().addFluidTag("input", fluidIn).addFluid("result", primaryFluidOutput).setTime(time).setEnergy(energy);
    }

    public DistillerRecipeBuilder addItemOutput(ItemStack item, float chance) {
        return this.addWriter(jsonObject -> {
            jsonObject.add("item_output", encode(ItemStack.CODEC, item));
            jsonObject.addProperty("chance", chance);
        });
    }
}
