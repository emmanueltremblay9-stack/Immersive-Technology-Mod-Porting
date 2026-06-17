package mctmods.immersivetechnology.core.network;

import com.mojang.datafixers.util.Pair;
import mctmods.immersivetechnology.common.gui.helper.ITGenericDataSerializers;
import mctmods.immersivetechnology.common.gui.helper.ITGenericDataSerializers.DataPair;
import mctmods.immersivetechnology.common.gui.helper.ITContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import java.util.ArrayList;
import java.util.List;

public record ITMessageContainerData(List<Pair<Integer, DataPair<?>>> synced) implements ITMessage {
    public ITMessageContainerData(FriendlyByteBuf buf) { this(readSynced(buf)); }

    private static List<Pair<Integer, DataPair<?>>> readSynced(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Pair<Integer, DataPair<?>>> synced = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int index = buf.readVarInt();
            DataPair<?> dataPair = ITGenericDataSerializers.read(buf);
            synced.add(Pair.of(index, dataPair));
        }
        return synced;
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(synced.size());
        for (Pair<Integer, DataPair<?>> pair : synced) {
            buf.writeVarInt(pair.getFirst());
            pair.getSecond().write(buf);
        }
    }

    @Override public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) { return; }
            AbstractContainerMenu currentContainer = player.containerMenu;
            if (currentContainer instanceof ITContainerMenu itContainer) { itContainer.receiveSync(synced); }
        });
    }

    @Override public CustomPacketPayload.Type<ITMessageContainerData> type() { return ITPacketHandler.CONTAINER_DATA; }
}
