package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.DistillerRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class DistillerRecipeSerializer extends IERecipeSerializer<DistillerRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, DistillerRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), recipe -> recipe.input,
            IEDualCodecs.FLUID_STACK.optionalFieldOf("result", FluidStack.EMPTY), recipe -> ITRecipeSerializerCodecs.nullToEmpty(recipe.fluidOutput),
            DualCodecs.ITEM_STACK.optionalFieldOf("item_output", ItemStack.EMPTY), recipe -> recipe.itemOutput,
            DualCodecs.FLOAT.optionalFieldOf("chance", 0.0f), recipe -> recipe.chance,
            DualCodecs.INT.fieldOf("time"), DistillerRecipe::getTotalProcessTime,
            DualCodecs.INT.fieldOf("energy"), DistillerRecipe::getTotalProcessEnergy,
            (input, fluidOutput, itemOutput, chance, time, energy) -> new DistillerRecipe(input, ITRecipeSerializerCodecs.emptyToNull(fluidOutput), itemOutput, chance, time, energy)
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.DISTILLER.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, DistillerRecipe> codecs() { return CODECS; }
}
