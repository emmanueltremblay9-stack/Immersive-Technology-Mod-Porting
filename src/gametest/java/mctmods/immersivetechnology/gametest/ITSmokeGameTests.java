package mctmods.immersivetechnology.gametest;

import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ITLib.MODID)
@PrefixGameTestTemplate(false)
public class ITSmokeGameTests {
    @GameTest(template = "multiblocks/alternator")
    public static void modLoadsWithExistingMultiblockTemplate(GameTestHelper helper) {
        helper.succeed();
    }
}
