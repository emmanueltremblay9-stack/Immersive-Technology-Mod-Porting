package mctmods.immersivetechnology.common.multiblocks.stone.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer.ITRecipeSerializerCodecs;
import mctmods.immersivetechnology.common.multiblocks.stone.recipe.CoolingTowerRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class CoolingTowerRecipeSerializer extends IERecipeSerializer<CoolingTowerRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, CoolingTowerRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.FLUID_STACK.optionalFieldOf("output0", FluidStack.EMPTY), recipe -> ITRecipeSerializerCodecs.nullToEmpty(recipe.fluidOutput0),
            IEDualCodecs.FLUID_STACK.optionalFieldOf("output1", FluidStack.EMPTY), recipe -> ITRecipeSerializerCodecs.nullToEmpty(recipe.fluidOutput1),
            IEDualCodecs.FLUID_STACK.optionalFieldOf("output2", FluidStack.EMPTY), recipe -> ITRecipeSerializerCodecs.nullToEmpty(recipe.fluidOutput2),
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input0"), recipe -> recipe.input0,
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input1"), recipe -> recipe.input1,
            DualCodecs.INT.fieldOf("time"), CoolingTowerRecipe::getTotalProcessTime,
            (output0, output1, output2, input0, input1, time) -> new CoolingTowerRecipe(
                    ITRecipeSerializerCodecs.emptyToNull(output0),
                    ITRecipeSerializerCodecs.emptyToNull(output1),
                    ITRecipeSerializerCodecs.emptyToNull(output2),
                    input0,
                    input1,
                    time
            )
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.COOLING_TOWER.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, CoolingTowerRecipe> codecs() { return CODECS; }
}
