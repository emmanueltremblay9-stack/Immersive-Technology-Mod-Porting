package mctmods.immersivetechnology.core.network;

import mctmods.immersivetechnology.core.util.TranslationKey;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ITOSDSyncBlock implements ITMessage {
    private final String key;
    private final int distance;

    public ITOSDSyncBlock(String key, int distance) { this.key = key; this.distance = distance; }

    public ITOSDSyncBlock(FriendlyByteBuf buf) { this.key = buf.readUtf(); this.distance = buf.readInt(); }

    @Override public void toBytes(FriendlyByteBuf buf) { buf.writeUtf(key); buf.writeInt(distance); }

    @Override public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            TranslationKey transKey = TranslationKey.valueOf(key);
            String actualKey = transKey.getLocation();
            Component msg;
            if (distance >= 0) { msg = Component.translatable(actualKey, distance); }
            else { msg = Component.translatable(actualKey); }
            Minecraft.getInstance().gui.getChat().addMessage(msg);
        });
    }

    @Override public CustomPacketPayload.Type<ITOSDSyncBlock> type() { return ITPacketHandler.OSD_SYNC_BLOCK; }
}
