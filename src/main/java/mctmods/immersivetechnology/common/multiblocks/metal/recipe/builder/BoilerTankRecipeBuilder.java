package mctmods.immersivetechnology.common.multiblocks.metal.recipe.builder;

import mctmods.immersivetechnology.core.compat.ie.IEFinishedRecipe;

import mctmods.immersivetechnology.common.multiblocks.metal.recipe.BoilerTankRecipe;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class BoilerTankRecipeBuilder extends IEFinishedRecipe<BoilerTankRecipeBuilder> {
    public BoilerTankRecipeBuilder() {
        super(BoilerTankRecipe.SERIALIZER.get());
        this.maxInputCount = 1;
    }

    public static BoilerTankRecipeBuilder builder(TagKey<Fluid> inputTag, int inputAmount) { return new BoilerTankRecipeBuilder().addFluidTag("input", inputTag, inputAmount); }

    public BoilerTankRecipeBuilder addOutput(Fluid outputFluid, int outputAmount) { return addFluid("result", new FluidStack(outputFluid, outputAmount)); }

    public BoilerTankRecipeBuilder setTime(int time) { return this.addWriter((jsonObject) -> jsonObject.addProperty("time", time)); }

    public BoilerTankRecipeBuilder setRequiredHeat(double requiredHeat) { return this.addWriter((jsonObject) -> jsonObject.addProperty("requiredHeat", requiredHeat)); }
}
