package mctmods.immersivetechnology.gametest;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import mctmods.immersivetechnology.common.blocks.metal.gui.RotorCreativeMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.TrashItemMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.ValveFluidMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.ValveLimiterMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.ValveLoadMenu;
import mctmods.immersivetechnology.common.blocks.wooden.gui.CrateCreativeMenu;
import mctmods.immersivetechnology.common.gui.helper.ITContainerMenu;
import mctmods.immersivetechnology.common.gui.helper.ITGenericContainerData;
import mctmods.immersivetechnology.common.gui.helper.ITGenericDataSerializers;
import mctmods.immersivetechnology.common.multiblocks.gui.SolarMenu;
import mctmods.immersivetechnology.core.lib.ITLib;
import mctmods.immersivetechnology.core.registration.ITFluids;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import mctmods.immersivetechnology.core.registration.ITMenuTypes;
import mctmods.immersivetechnology.core.registration.ITTags;
import mctmods.immersivetechnology.core.util.inventory.IITInventory;
import mctmods.immersivetechnology.core.util.inventory.ITInventoryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import java.util.List;

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

    @GameTest(template = "multiblocks/alternator")
    public static void ieToolsResolveThroughItCompatibilityTags(GameTestHelper helper) {
        Item hammer = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "hammer"));
        Item screwdriver = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "screwdriver"));

        helper.assertTrue(new ItemStack(hammer).is(ITTags.formationTools), "IE hammer is not included in the IT formation tools tag");
        helper.assertTrue(new ItemStack(screwdriver).is(ITTags.screwdrivers), "IE screwdriver is not included in the IT screwdriver tag");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void menusRegisterUnderCurrentModNamespace(GameTestHelper helper) {
        assertMenuNamespace(helper, ITMenuTypes.BOILER_LIQUID_MENU, "gui_boiler");
        assertMenuNamespace(helper, ITMenuTypes.CRATE_CREATIVE, "crate_creative");
        assertMenuNamespace(helper, ITMenuTypes.VALVE_FLUID, "valve_fluid");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void remapRangeUsesInputSpan(GameTestHelper helper) {
        float midpoint = ITLib.remapRange(100f, 500f, 0f, 1f, 300f);
        helper.assertTrue(Math.abs(midpoint - 0.5f) < 0.0001f, "remapRange did not normalize against the input span");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void fluidLookupCoversAllRegisteredItFluids(GameTestHelper helper) {
        for (ITFluids.FluidEntry entry : ITFluids.ALL_ENTRIES) {
            ResourceLocation id = entry.still().getId();
            Fluid resolved = ITFluids.getFluid.apply(id.getPath());
            helper.assertTrue(resolved == entry.getStill(), "Fluid lookup did not resolve registered fluid " + id);
        }
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void inventoryHandlerUsesBackingSlotOffsetForValidation(GameTestHelper helper) {
        ITInventoryHandler handler = new ITInventoryHandler(
                1,
                new OffsetTestInventory(),
                1,
                new boolean[]{true},
                new boolean[]{true}
        );

        helper.assertTrue(handler.isItemValid(0, new ItemStack(Items.IRON_INGOT)), "Offset slot should accept the backing slot's valid item");
        helper.assertFalse(handler.isItemValid(0, new ItemStack(Items.GOLD_INGOT)), "Offset slot should reject items invalid for the backing slot");
        helper.assertTrue(handler.getSlotLimit(0) == 7, "Offset slot should expose the backing slot limit");
        helper.assertTrue(handler.insertItem(0, new ItemStack(Items.IRON_INGOT, 20), true).getCount() == 13, "Offset slot insert simulation should respect the backing slot limit");

        handler.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 7));
        helper.assertTrue(handler.insertItem(0, new ItemStack(Items.IRON_INGOT, 3), true).getCount() == 3, "Full offset slot insert simulation should return the whole stack");
        helper.assertTrue(handler.getStackInSlot(0).getCount() == 7, "Full offset slot insert simulation should not mutate the slot");
        helper.assertTrue(handler.insertItem(0, new ItemStack(Items.IRON_INGOT, 3), false).getCount() == 3, "Full offset slot insert should return the whole stack");
        helper.assertTrue(handler.getStackInSlot(0).getCount() == 7, "Full offset slot insert should not mutate the slot");
        helper.assertTrue(handler.extractItem(0, -1, false).isEmpty(), "Negative extraction amounts should be rejected");
        helper.assertTrue(handler.getStackInSlot(0).getCount() == 7, "Negative extraction should not mutate the slot");
        helper.assertTrue(handler.extractItem(0, 2, false).getCount() == 2, "Positive extraction should return the requested stack size");
        helper.assertTrue(handler.getStackInSlot(0).getCount() == 5, "Positive extraction should shrink the backing slot");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void blockMenusRejectInvalidQuickMoveSlotIndices(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        CrateCreativeMenu crateMenu = CrateCreativeMenu.makeClient(ITMenuTypes.CRATE_CREATIVE.getType(), 1, player.getInventory());
        TrashItemMenu trashMenu = TrashItemMenu.makeClient(ITMenuTypes.TRASH_ITEM.getType(), 2, player.getInventory());

        assertQuickMoveRejectsInvalidIndices(helper, crateMenu, player, "crate_creative");
        assertQuickMoveRejectsInvalidIndices(helper, trashMenu, player, "trash_item");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void clientSolarMenusBroadcastWithoutServerState(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        SolarMenu melterMenu = SolarMenu.makeClient(ITMenuTypes.SOLAR_MELTER_MENU.getType(), 3, player.getInventory());
        SolarMenu towerMenu = SolarMenu.makeClient(ITMenuTypes.SOLAR_TOWER_MENU.getType(), 4, player.getInventory());

        melterMenu.broadcastChanges();
        towerMenu.broadcastChanges();
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void clientRotorCreativeMenuToleratesMissingBlockEntity(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        RotorCreativeMenu menu = RotorCreativeMenu.makeClient(
                ITMenuTypes.ROTOR_CREATIVE.getType(),
                5,
                player.getInventory(),
                blockEntityPosBuffer(BlockPos.ZERO)
        );

        helper.assertTrue(menu.stillValid(player), "Client rotor menu should not crash when the block entity is not loaded yet");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void clientBlockMenusTolerateUnexpectedBlockEntityType(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, IEBlocks.MetalDevices.FLUID_PIPE.defaultBlockState());
        helper.getBlockEntity(pos);

        RotorCreativeMenu rotorMenu = RotorCreativeMenu.makeClient(
                ITMenuTypes.ROTOR_CREATIVE.getType(),
                6,
                player.getInventory(),
                blockEntityPosBuffer(pos)
        );
        ValveFluidMenu fluidMenu = ValveFluidMenu.makeClient(
                ITMenuTypes.VALVE_FLUID.getType(),
                7,
                player.getInventory(),
                blockEntityPosBuffer(pos)
        );
        ValveLoadMenu loadMenu = ValveLoadMenu.makeClient(
                ITMenuTypes.VALVE_LOAD.getType(),
                8,
                player.getInventory(),
                blockEntityPosBuffer(pos)
        );
        ValveLimiterMenu limiterMenu = ValveLimiterMenu.makeClient(
                ITMenuTypes.VALVE_LIMITER.getType(),
                9,
                player.getInventory(),
                blockEntityPosBuffer(pos)
        );

        helper.assertTrue(rotorMenu.stillValid(player), "Client rotor menu should tolerate an unexpected block entity type");
        helper.assertTrue(fluidMenu.stillValid(player), "Client fluid valve menu should tolerate an unexpected block entity type");
        helper.assertTrue(loadMenu.stillValid(player), "Client load valve menu should tolerate an unexpected block entity type");
        helper.assertTrue(limiterMenu.stillValid(player), "Client limiter valve menu should tolerate an unexpected block entity type");
        helper.succeed();
    }

    @GameTest(template = "multiblocks/alternator")
    public static void menuSyncRejectsInvalidGenericDataIndices(GameTestHelper helper) {
        TestSyncMenu menu = new TestSyncMenu();

        menu.receiveSync(List.of(
                Pair.of(-1, new ITGenericDataSerializers.DataPair<>(ITGenericDataSerializers.INT32, 12)),
                Pair.of(1, new ITGenericDataSerializers.DataPair<>(ITGenericDataSerializers.INT32, 24))
        ));
        helper.assertTrue(menu.syncedValue == 0, "Invalid generic sync indices should be ignored");

        menu.receiveSync(List.of(Pair.of(0, new ITGenericDataSerializers.DataPair<>(ITGenericDataSerializers.INT32, 36))));
        helper.assertTrue(menu.syncedValue == 36, "Valid generic sync index should still update the data slot");
        helper.succeed();
    }

    private static final class OffsetTestInventory implements IITInventory {
        private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

        @Override
        public NonNullList<ItemStack> getInventory() {
            return inventory;
        }

        @Override
        public boolean isStackValid(int slot, ItemStack stack) {
            return slot == 1 && stack.is(Items.IRON_INGOT);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 1 ? 7 : 1;
        }

        @Override
        public void doGraphicalUpdates() {
        }
    }

    private static final class TestSyncMenu extends ITContainerMenu {
        private int syncedValue;

        private TestSyncMenu() {
            super(ITContainerMenu.clientCtx(ITMenuTypes.CRATE_CREATIVE.getType(), 5));
            addGenericData(ITGenericContainerData.int32(() -> syncedValue, value -> syncedValue = value));
        }
    }

    private static void assertMenuNamespace(GameTestHelper helper, ITMenuTypes.ArgContainer<?, ?> menu, String path) {
        ResourceLocation id = BuiltInRegistries.MENU.getKey(menu.getType());
        helper.assertTrue(id != null, "Menu " + path + " is not registered");
        helper.assertTrue(ITLib.MODID.equals(id.getNamespace()), "Menu " + path + " registered under wrong namespace: " + id);
        helper.assertTrue(path.equals(id.getPath()), "Menu " + path + " registered under wrong path: " + id);
    }

    private static void assertQuickMoveRejectsInvalidIndices(GameTestHelper helper, ITContainerMenu menu, Player player, String name) {
        helper.assertTrue(menu.quickMoveStack(player, -1).isEmpty(), name + " quickMoveStack should reject negative slot ids");
        helper.assertTrue(menu.quickMoveStack(player, menu.slots.size()).isEmpty(), name + " quickMoveStack should reject slot ids past the menu size");
    }

    private static FriendlyByteBuf blockEntityPosBuffer(BlockPos pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        return buf;
    }
}
