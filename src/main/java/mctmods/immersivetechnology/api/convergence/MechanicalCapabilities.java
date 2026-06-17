package mctmods.immersivetechnology.api.convergence;

import mctmods.immersivetechnology.api.convergence.capability.IMechanicalEnergyConsumer;
import mctmods.immersivetechnology.api.convergence.capability.IMechanicalEnergyProvider;
import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class MechanicalCapabilities {
    public static final int MAX_RPM = 7200;

    public static final BlockCapability<IMechanicalEnergyProvider, Direction> MECHANICAL_PROVIDER_CAPABILITY =
            BlockCapability.createSided(ITLib.rl("mechanical_provider"), IMechanicalEnergyProvider.class);

    public static final BlockCapability<IMechanicalEnergyConsumer, Direction> MECHANICAL_CONSUMER_CAPABILITY =
            BlockCapability.createSided(ITLib.rl("mechanical_consumer"), IMechanicalEnergyConsumer.class);

    private MechanicalCapabilities() {}
}
