package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.HeatExchangerRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class HeatExchangerRecipeSerializer extends IERecipeSerializer<HeatExchangerRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, HeatExchangerRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input0"), recipe -> recipe.input0,
            IEDualCodecs.SIZED_FLUID_INGREDIENT.optionalFieldOf("input1"), recipe -> Optional.ofNullable(recipe.input1),
            IEDualCodecs.FLUID_STACK.fieldOf("output0"), recipe -> recipe.output0,
            IEDualCodecs.FLUID_STACK.optionalFieldOf("output1", FluidStack.EMPTY), recipe -> ITRecipeSerializerCodecs.nullToEmpty(recipe.output1),
            DualCodecs.INT.fieldOf("energy"), HeatExchangerRecipe::getTotalProcessEnergy,
            DualCodecs.INT.fieldOf("time"), HeatExchangerRecipe::getTotalProcessTime,
            (input0, input1, output0, output1, energy, time) -> new HeatExchangerRecipe(input0, input1.orElse(null), output0, ITRecipeSerializerCodecs.emptyToNull(output1), energy, time)
                    .modifyTimeAndEnergy(t -> t * HeatExchangerRecipe.timeModifier, e -> e * HeatExchangerRecipe.energyModifier)
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.HEAT_EXCHANGER.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, HeatExchangerRecipe> codecs() { return CODECS; }
}
