package mctmods.immersivetechnology.common.multiblocks.metal.recipe.builder;

import mctmods.immersivetechnology.core.compat.ie.IEFinishedRecipe;
import com.google.gson.JsonObject;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.SteamTurbineRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class SteamTurbineRecipeBuilder extends IEFinishedRecipe<SteamTurbineRecipeBuilder> {
    public SteamTurbineRecipeBuilder() { super(SteamTurbineRecipe.SERIALIZER.get()); }

    public static SteamTurbineRecipeBuilder builder() { return new SteamTurbineRecipeBuilder(); }

    public SteamTurbineRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount) {
        return this.addWriter((jsonObject) -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("tag", fluidTag.location().toString());
            obj.addProperty("amount", amount);
            jsonObject.add("input", obj);
        });
    }

    public SteamTurbineRecipeBuilder addOutput(FluidStack fluidStack) {
        return addFluid("output", fluidStack);
    }

    public SteamTurbineRecipeBuilder addOutput(Fluid fluid, int amount) { return addOutput(new FluidStack(fluid, amount)); }

    public SteamTurbineRecipeBuilder setTime(int time) { return this.addWriter((jsonObject) -> jsonObject.addProperty("time", time)); }

    /**
     * Set the torque multiplier produced while this recipe is used.
     */
    public SteamTurbineRecipeBuilder setTorque(float torque) { return this.addWriter((jsonObject) -> jsonObject.addProperty("torque", torque)); }
}
