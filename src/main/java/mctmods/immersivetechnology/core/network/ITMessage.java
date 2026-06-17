package mctmods.immersivetechnology.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ITMessage extends CustomPacketPayload {
    void toBytes(FriendlyByteBuf buf);
    void process(IPayloadContext context);
}
