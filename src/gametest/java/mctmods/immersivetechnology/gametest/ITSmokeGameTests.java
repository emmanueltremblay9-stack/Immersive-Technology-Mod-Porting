package mctmods.immersivetechnology.gametest;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import mctmods.immersivetechnology.core.lib.ITLib;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ITLib.MODID)
@PrefixGameTestTemplate(false)
public class ITSmokeGameTests {
    @GameTest(template = "multiblocks/alternator")
    public static void registeredMultiblockTemplatesLoad(GameTestHelper helper) {
        helper.assertFalse(ITMultiblockProvider.MB_TEMPLATE_MAP.isEmpty(), "No Immersive Technology multiblock templates were registered");
        ITMultiblockProvider.MB_TEMPLATE_MAP.forEach((name, template) -> template.getTemplate(helper.getLevel()));
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void fluidPipeConnectionUpdateDoesNotCrash(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, IEBlocks.MetalDevices.FLUID_PIPE.defaultBlockState());

        FluidPipeBlockEntity pipe = helper.getBlockEntity(pos);
        pipe.updateConnectionByte(Direction.UP);

        helper.succeed();
    }
}
