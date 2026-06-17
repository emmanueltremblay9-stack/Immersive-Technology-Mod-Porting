package mctmods.immersivetechnology.api.convergence;

import mctmods.immersivetechnology.api.convergence.capability.IHeatConsumer;
import mctmods.immersivetechnology.api.convergence.capability.IHeatProvider;
import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class HeatCapabilities {
    public static final double MAX_HEAT = 2000.0D;

    public static final BlockCapability<IHeatProvider, Direction> HEAT_PROVIDER_CAPABILITY =
            BlockCapability.createSided(ITLib.rl("heat_provider"), IHeatProvider.class);

    public static final BlockCapability<IHeatConsumer, Direction> HEAT_CONSUMER_CAPABILITY =
            BlockCapability.createSided(ITLib.rl("heat_consumer"), IHeatConsumer.class);

    private HeatCapabilities() {}
}
