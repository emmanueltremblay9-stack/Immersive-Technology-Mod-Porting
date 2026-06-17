package mctmods.immersivetechnology.core.network;

import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ITPacketHandler {
    public static final String NET_VERSION = "1";

    public static final CustomPacketPayload.Type<ITMessageContainerUpdate> CONTAINER_UPDATE = new CustomPacketPayload.Type<>(ITLib.rl("container_update"));
    public static final CustomPacketPayload.Type<ITMessageContainerData> CONTAINER_DATA = new CustomPacketPayload.Type<>(ITLib.rl("container_data"));
    public static final CustomPacketPayload.Type<ITOSDRequestMessage> OSD_REQUEST = new CustomPacketPayload.Type<>(ITLib.rl("osd_request"));
    public static final CustomPacketPayload.Type<ITOSDSyncMessage> OSD_SYNC = new CustomPacketPayload.Type<>(ITLib.rl("osd_sync"));
    public static final CustomPacketPayload.Type<ITOSDSyncBlock> OSD_SYNC_BLOCK = new CustomPacketPayload.Type<>(ITLib.rl("osd_sync_block"));
    public static final CustomPacketPayload.Type<ITMessageTileSync> TILE_SYNC = new CustomPacketPayload.Type<>(ITLib.rl("tile_sync"));

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ITLib.MODID).versioned(NET_VERSION).optional();
        registrar.playToServer(CONTAINER_UPDATE, codec(ITMessageContainerUpdate::new), ITMessage::process);
        registrar.playToClient(CONTAINER_DATA, codec(ITMessageContainerData::new), ITMessage::process);
        registrar.playToServer(OSD_REQUEST, codec(ITOSDRequestMessage::new), ITMessage::process);
        registrar.playToClient(OSD_SYNC, codec(ITOSDSyncMessage::new), ITMessage::process);
        registrar.playToClient(OSD_SYNC_BLOCK, codec(ITOSDSyncBlock::new), ITMessage::process);
        registrar.playToServer(TILE_SYNC, codec(ITMessageTileSync::new), ITMessage::process);
    }

    private static <T extends ITMessage> StreamCodec<RegistryFriendlyByteBuf, T> codec(Function<FriendlyByteBuf, T> decoder) {
        return StreamCodec.ofMember(ITMessage::toBytes, decoder::apply);
    }

    public static void sendToPlayer(Player player, @Nonnull ITMessage message) { if (player instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, message); }

    public static void sendToServer(ITMessage message) { if (message != null) PacketDistributor.sendToServer(message); }

    public static void sendToDimension(ResourceKey<Level> dim, ITMessage message) {
        if (message == null) return;
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        var level = server.getLevel(dim);
        if (level != null) PacketDistributor.sendToPlayersInDimension(level, message);
    }

    public static void sendAll(ITMessage message) { if (message != null) PacketDistributor.sendToAllPlayers(message); }
}
