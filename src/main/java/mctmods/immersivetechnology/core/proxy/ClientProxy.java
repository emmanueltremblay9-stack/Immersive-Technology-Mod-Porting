package mctmods.immersivetechnology.core.proxy;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import mctmods.immersivetechnology.client.gui.*;
import mctmods.immersivetechnology.client.gui.helper.ITContainerScreen;
import mctmods.immersivetechnology.client.models.RotorModels;
import mctmods.immersivetechnology.client.models.SolarReflectorModels;
import mctmods.immersivetechnology.client.models.helper.ITDynamicModel;
import mctmods.immersivetechnology.client.models.helper.ITModelConfigurableSides;
import mctmods.immersivetechnology.client.models.helper.ITObjLoader;
import mctmods.immersivetechnology.client.models.mirror.ITMirroredModelLoader;
import mctmods.immersivetechnology.client.models.split.ITSplitModelLoader;
import mctmods.immersivetechnology.client.particles.helper.ITColoredSmokeProvider;
import mctmods.immersivetechnology.client.particles.helper.ITSmokeCustomProvider;
import mctmods.immersivetechnology.client.renderer.*;
import mctmods.immersivetechnology.common.blocks.metal.gui.RotorCreativeMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.ValveFluidMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.ValveLimiterMenu;
import mctmods.immersivetechnology.common.blocks.metal.gui.ValveLoadMenu;
import mctmods.immersivetechnology.common.items.helper.ITFlagItem;
import mctmods.immersivetechnology.core.lib.ITLib;
import mctmods.immersivetechnology.core.registration.ITBlockEntities;
import mctmods.immersivetechnology.core.registration.ITFluids;
import mctmods.immersivetechnology.core.registration.ITItems;
import mctmods.immersivetechnology.core.registration.ITMenuTypes;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import mctmods.immersivetechnology.core.registration.ITParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = ITLib.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ITMenuTypes.BOILER_LIQUID_MENU.getType(), BoilerLiquidScreen::new);
        event.register(ITMenuTypes.BOILER_SOLID_MENU.getType(), BoilerSolidScreen::new);
        event.register(ITMenuTypes.BOILER_TANK_MENU.getType(), BoilerTankScreen::new);
        event.register(ITMenuTypes.CRATE_CREATIVE.getType(), CrateCreativeScreen::new);
        event.register(ITMenuTypes.DISTILLER_MENU.getType(), DistillerScreen::new);
        event.register(ITMenuTypes.TRASH_ITEM.getType(), TrashItemScreen::new);
        event.register(ITMenuTypes.SOLAR_MELTER_MENU.getType(), SolarScreen::new);
        event.register(ITMenuTypes.SOLAR_TOWER_MENU.getType(), SolarScreen::new);
        event.register(ITMenuTypes.ROTOR_CREATIVE.getType(), (RotorCreativeMenu menu, Inventory inv, Component title) -> new RotorCreativeScreen(menu, inv));
        event.register(ITMenuTypes.VALVE_FLUID.getType(), (ValveFluidMenu menu, Inventory inv, Component title) -> new ValveFluidScreen(menu, inv));
        event.register(ITMenuTypes.VALVE_LIMITER.getType(), (ValveLimiterMenu menu, Inventory inv, Component title) -> new ValveLimiterScreen(menu, inv));
        event.register(ITMenuTypes.VALVE_LOAD.getType(), (ValveLoadMenu menu, Inventory inv, Component title) -> new ValveLoadScreen(menu, inv));
    }

    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (ITFluids.FluidEntry entry : ITFluids.ALL_ENTRIES) {
                ItemBlockRenderTypes.setRenderLayer(entry.getStill(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(entry.getFlowing(), RenderType.translucent());
            }

            registerManualEntries();
        });
    }

    private static void registerManualEntries() {
        ManualInstance instance = ManualHelper.getManual();
        InnerNode<ResourceLocation, ManualEntry> main = instance.getRoot().getOrCreateSubnode(ITLib.rl("main"), 99);
        addManualEntry(instance, main, "intro", 0);

        InnerNode<ResourceLocation, ManualEntry> power = main.getOrCreateSubnode(ITLib.rl("power_generation"), 10);
        addManualEntry(instance, power, "steam_turbine", 10);
        addManualEntry(instance, power, "gas_turbine", 20);
        addManualEntry(instance, power, "alternator", 30);

        InnerNode<ResourceLocation, ManualEntry> boilers = main.getOrCreateSubnode(ITLib.rl("boiler_systems"), 20);
        addManualEntry(instance, boilers, "boiler_tank", 10);
        addManualEntry(instance, boilers, "boiler_liquid", 20);
        addManualEntry(instance, boilers, "boiler_solid", 30);

        InnerNode<ResourceLocation, ManualEntry> fluidProcessing = main.getOrCreateSubnode(ITLib.rl("fluid_processing"), 30);
        addManualEntry(instance, fluidProcessing, "distiller", 10);
        addManualEntry(instance, fluidProcessing, "heat_exchanger", 20);
        addManualEntry(instance, fluidProcessing, "cooling_tower", 30);

        InnerNode<ResourceLocation, ManualEntry> solarThermal = main.getOrCreateSubnode(ITLib.rl("solar_thermal"), 40);
        addManualEntry(instance, solarThermal, "solar_reflector", 10);
        addManualEntry(instance, solarThermal, "solar_tower", 20);
        addManualEntry(instance, solarThermal, "solar_melter", 30);

        InnerNode<ResourceLocation, ManualEntry> storage = main.getOrCreateSubnode(ITLib.rl("storage"), 50);
        addManualEntry(instance, storage, "steel_sheetmetal_tank", 10);
    }

    private static void addManualEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, String path, int weight) {
        instance.addEntry(category, ITLib.rl(path), weight);
    }

    @SubscribeEvent public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ITParticles.COLORED_SMOKE.get(), ITColoredSmokeProvider::new);
        event.registerSpriteSet(ITParticles.SMOKE_CUSTOM.get(), ITSmokeCustomProvider::new);
    }

    @SubscribeEvent public static void onItemColor(RegisterColorHandlersEvent.Item event) {
        for (DeferredHolder<Item, ? extends Item> holder : ITItems.getItemRegistryMap().values()) {
            Item i = holder.get();
            if (i instanceof ITFlagItem) {
                event.register((stack, tintIndex) -> {
                    if (stack.getItem() instanceof ITFlagItem type) { return type.getColor(tintIndex); }
                    return 0xffffff;
                }, i);
            }
        }
        for (ITFluids.FluidEntry entry : ITFluids.ALL_ENTRIES) {
            final int tint = entry.tintColor();
            event.register((stack, index) -> { if (index == 1) { return tint; } return -1; }, entry.bucket().get());
        }
    }

    @SubscribeEvent public static void onBlockColor(RegisterColorHandlersEvent.Block event) {
        for (ITFluids.FluidEntry entry : ITFluids.ALL_ENTRIES) {
            final int tint = entry.tintColor();
            event.register((state, level, pos, index) -> tint, entry.block().get());
        }
    }

    @Override public void reinitializeGUI() {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof ITContainerScreen) { currentScreen.init(Minecraft.getInstance(), currentScreen.width, currentScreen.height); }
    }

    @Override public Level getClientWorld() { return Minecraft.getInstance().level; }

    @Override public Player getClientPlayer() { return Minecraft.getInstance().player; }

    @SubscribeEvent public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders ev) {
        ev.register(ITLib.rl("obj"), ITObjLoader.INSTANCE);
        ev.register(ITModelConfigurableSides.Loader.NAME, new ITModelConfigurableSides.Loader());
        ev.register(ITMirroredModelLoader.ID, new ITMirroredModelLoader());
        ev.register(ITSplitModelLoader.LOCATION, new ITSplitModelLoader());
        RotorModels.ROTOR = new ITDynamicModel("rotor");
        RotorModels.ROTOR_EAST_WEST = new ITDynamicModel("rotor_east_west");
        SolarReflectorModels.SUPPORT = new ITDynamicModel("solar_reflector_support");
        SolarReflectorModels.MIRROR = new ITDynamicModel("solar_reflector_mirror");
    }

    @SubscribeEvent public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) { registerBERenders(event); }

    private static <T extends BlockEntity> void registerBERender(EntityRenderersEvent.RegisterRenderers event, Supplier<BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) { event.registerBlockEntityRenderer(type.get(), provider); }

    public static void registerBERenders(EntityRenderersEvent.RegisterRenderers event) {
        registerBERender(event, ITBlockEntities.BARREL_OPEN::get, ctx3 -> new OpenBarrelRenderer());
        registerBERender(event, ITBlockEntities.ROTOR_CREATIVE::get, context -> new RotorCreativeRenderer());
        registerBERender(event, ITMultiblockProvider.STEAM_TURBINE.masterBE(), ctx2 -> new SteamTurbineRenderer());
        registerBERender(event, ITMultiblockProvider.GAS_TURBINE.masterBE(), ctx -> new GasTurbineRenderer());
        registerBERender(event, ITMultiblockProvider.SOLAR_REFLECTOR.masterBE(), ctx1 -> new SolarReflectorRenderer());
        registerBERender(event, ITMultiblockProvider.SOLAR_MELTER.masterBE(), ctx -> new SolarMelterRenderer());
        registerBERender(event, ITMultiblockProvider.STEEL_SHEETMETAL_TANK.masterBE(), ctx -> new SteelSheetmetalTankRenderer());
    }
}
