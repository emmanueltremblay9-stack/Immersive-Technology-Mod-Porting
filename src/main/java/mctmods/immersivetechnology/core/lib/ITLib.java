package mctmods.immersivetechnology.core.lib;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class ITLib {
    public static final String MODID = "immersive_technology_reforged";
    public static final String VERSION = "3.0.28";
    public static final String DESC = "desc." + MODID + ".";
    public static final String DESC_INFO = DESC + "info.";
    public static final String DESC_FLAVOUR = DESC + "flavour.";
    public static final Logger IT_LOGGER = LogUtils.getLogger();

    public static ResourceLocation makeTextureLocation(String name) { return rl("textures/gui/" + name + ".png"); }

    public static ResourceLocation rl(String name) { return ResourceLocation.fromNamespaceAndPath(ITLib.MODID, name); }

    public static float remapRange(float inMin, float inMax, float outMin, float outMax, float value) {
        if (inMax == inMin) return outMin;
        return outMin + ((value - inMin) / (inMax - inMin)) * (outMax - outMin);
    }
}
