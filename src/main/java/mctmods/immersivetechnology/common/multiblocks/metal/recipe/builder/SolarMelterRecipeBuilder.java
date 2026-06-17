package mctmods.immersivetechnology.common.multiblocks.metal.recipe.builder;

import mctmods.immersivetechnology.core.compat.ie.IEFinishedRecipe;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.SolarMelterRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class SolarMelterRecipeBuilder extends IEFinishedRecipe<SolarMelterRecipeBuilder> {
    private SolarMelterRecipeBuilder() { super(SolarMelterRecipe.SERIALIZER.get()); }

    public static SolarMelterRecipeBuilder builder() { return new SolarMelterRecipeBuilder(); }

    public SolarMelterRecipeBuilder addInput(TagKey<Fluid> tag, int amount) { return addFluidTag("input", tag, amount); }

    public SolarMelterRecipeBuilder addOutput(FluidStack output) { return addFluid("output", output); }

    public SolarMelterRecipeBuilder addOutput(Fluid fluid, int amount) { return addOutput(new FluidStack(fluid, amount)); }

    public SolarMelterRecipeBuilder setTime(int time) { return super.setTime(time); }

    public SolarMelterRecipeBuilder setRequiredTemp(double temp) { return addWriter(json -> json.addProperty("requiredTemp", temp)); }
}
