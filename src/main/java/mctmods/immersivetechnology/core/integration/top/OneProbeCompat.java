package mctmods.immersivetechnology.core.integration.top;

import java.util.function.Function;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.neoforged.fml.InterModComms;

public final class OneProbeCompat {
    private OneProbeCompat() {
    }

    public static void enqueueIMC() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> (Function<ITheOneProbe, Void>) top -> {
            OneProbeHelper.register(top);
            return null;
        });
    }
}
