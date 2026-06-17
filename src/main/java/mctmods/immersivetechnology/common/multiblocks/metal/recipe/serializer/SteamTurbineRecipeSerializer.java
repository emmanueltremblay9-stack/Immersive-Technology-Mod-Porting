package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.SteamTurbineRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.item.ItemStack;

public class SteamTurbineRecipeSerializer extends IERecipeSerializer<SteamTurbineRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, SteamTurbineRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), recipe -> recipe.input,
            IEDualCodecs.FLUID_STACK.optionalFieldOf("output", FluidStack.EMPTY), recipe -> ITRecipeSerializerCodecs.nullToEmpty(recipe.fluidOutput),
            DualCodecs.INT.fieldOf("time"), SteamTurbineRecipe::getTotalProcessTime,
            DualCodecs.FLOAT.optionalFieldOf("torque", 1.0f), recipe -> recipe.torque,
            (input, output, time, torque) -> new SteamTurbineRecipe(input, ITRecipeSerializerCodecs.emptyToNull(output), time, torque)
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.STEAM_TURBINE.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, SteamTurbineRecipe> codecs() { return CODECS; }
}
