package xyz.xenondevs.invui.internal.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class PacketListener implements Listener {
    
    private static final String MC_PACKET_HANDLER_NAME = "packet_handler";
    private static final String INVUI_PACKET_HANDLER_NAME = "invui_packet_handler";
    
    private static final PacketListener INSTANCE = new PacketListener();
    
    private final Map<UUID, PacketHandler> packetHandlers = new HashMap<>();
    
    private PacketListener() {
        Bukkit.getOnlinePlayers().forEach(this::injectChannelHandler);
        Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
        InvUI.getInstance().addDisableHandler(() -> Bukkit.getOnlinePlayers().forEach(this::removeChannelHandler));
    }
    
    public static PacketListener getInstance() {
        return INSTANCE;
    }
    
    public void discard(Player player, Class<? extends Packet<ClientGamePacketListener>> clazz) {
        getPacketHandler(player.getUniqueId()).discardRules.add(clazz);
    }
    
    public void stopDiscard(Player player, Class<? extends Packet<ClientGamePacketListener>> clazz) {
        getPacketHandler(player.getUniqueId()).discardRules.remove(clazz);
    }
    
    public void injectOutgoing(Player player, Packet<ClientGamePacketListener> packet) {
        getPacketHandler(player.getUniqueId()).injectOutgoing(packet);
    }
    
    public void injectOutgoing(Player player, List<Packet<? super ClientGamePacketListener>> packets) {
        getPacketHandler(player.getUniqueId()).injectOutgoing(new ClientboundBundlePacket(packets));
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Packet<ServerGamePacketListener>> void redirectIncoming(Player player, Class<T> clazz, Consumer<T> handler) {
        getPacketHandler(player.getUniqueId()).redirections.put(clazz, (Consumer<Packet<ServerGamePacketListener>>) handler);
    }
    
    public boolean removeRedirect(Player player, Class<? extends Packet<ServerGamePacketListener>> clazz) {
        return getPacketHandler(player.getUniqueId()).redirections.remove(clazz) != null;
    }
    
    private PacketHandler getPacketHandler(UUID uuid) {
        var packetHandler = packetHandlers.get(uuid);
        if (packetHandler == null)
            throw new IllegalStateException("No packet handler is registered for this player");
        return packetHandler;
    }
    
    @EventHandler
    private void handleJoin(PlayerJoinEvent event) {
        injectChannelHandler(event.getPlayer());
    }
    
    @EventHandler
    private void handleQuit(PlayerQuitEvent event) {
        packetHandlers.remove(event.getPlayer().getUniqueId());
    }
    
    private void injectChannelHandler(Player player) {
        if (packetHandlers.containsKey(player.getUniqueId()))
            throw new IllegalStateException("A packet handler is already registered for this player");
        
        var channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        var packetHandler = new PacketHandler(channel);
        packetHandlers.put(player.getUniqueId(), packetHandler);
        channel.pipeline().addBefore(MC_PACKET_HANDLER_NAME, INVUI_PACKET_HANDLER_NAME, packetHandler);
    }
    
    private void removeChannelHandler(Player player) {
        packetHandlers.remove(player.getUniqueId());
        var channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.pipeline().remove(INVUI_PACKET_HANDLER_NAME);
    }
    
    private static class PacketHandler extends ChannelDuplexHandler {
        
        private final Map<Class<? extends Packet<ServerGamePacketListener>>, Consumer<Packet<ServerGamePacketListener>>> redirections = new ConcurrentHashMap<>();
        private final Set<Class<? extends Packet<ClientGamePacketListener>>> discardRules = Collections.newSetFromMap(new ConcurrentHashMap<>());
        private final Channel channel;
        
        public PacketHandler(Channel channel) {
            this.channel = channel;
        }
        
        public void injectOutgoing(Packet<ClientGamePacketListener> packet) {
            if (!channel.eventLoop().inEventLoop()) {
                channel.eventLoop().execute(() -> injectOutgoing(packet));
                return;
            }
            
            try {
                print(packet, "injected");
                super.write(channel.pipeline().context(this), packet, channel.newPromise());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            if (msg instanceof Packet<?> packet) {
                if (packet instanceof ClientboundBundlePacket bundle) {
                    var subPackets = StreamSupport.stream(bundle.subPackets().spliterator(), false)
                        .<Packet<? super ClientGamePacketListener>>map(this::singleWrite)
                        .filter(Objects::nonNull)
                        .toList();
                    
                    if (subPackets.isEmpty()) {
                        print(packet, "intercepted");
                        promise.setSuccess();
                    } else {
                        print(packet, "forwarded");
                        ctx.write(new ClientboundBundlePacket(subPackets), promise);
                    }
                } else {
                    var newPacket = singleWrite((Packet<ClientGamePacketListener>) packet);
                    if (newPacket == null) {
                        print(packet, "intercepted");
                        promise.setSuccess();
                    } else {
                        print(packet, "forwarded");
                        ctx.write(newPacket, promise);
                    }
                }
            } else {
                ctx.write(msg, promise);
            }
        }
        
        private void print(Packet<?> packet, String status) {
            if (packet instanceof ClientboundBundlePacket bundle) {
                bundle.subPackets().forEach(p -> print(p, status));
            }
            
            switch (packet) {
                case ClientboundOpenScreenPacket p -> {
                    System.out.println(status + ": open screen");
                }
                
                case ClientboundContainerSetContentPacket p -> {
                    System.out.println(status + ": Container content");
                }
                
                case ClientboundContainerSetSlotPacket p -> {
                    System.out.println(status + ": Set slot: " + p.getSlot() + " item: "+p.getItem());
                }
                
                case ClientboundContainerSetDataPacket p -> {
                    System.out.println(status + ": Set data: " + p.getId() + "=" + p.getValue());
                }
                
                case ClientboundSetCursorItemPacket p -> {
                    System.out.println(status + ": Cursor: " + p.contents());
                }
                
                case ClientboundMapItemDataPacket p -> {
                    System.out.println(status + ": Map data " + p);
                }
                
                default -> {}
            }
        }
        
        private @Nullable <P extends Packet<? super ClientGamePacketListener>> P singleWrite(P packet) {
            if (discardRules.contains(packet.getClass())) {
                return null;
            } else {
                return packet;
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            var handler = redirections.get(msg.getClass());
            if (handler != null) {
                Bukkit.getScheduler().runTask(
                    InvUI.getInstance().getPlugin(),
                    () -> handler.accept((Packet<ServerGamePacketListener>) msg)
                );
            } else {
                super.channelRead(ctx, msg);
            }
        }
        
    }
    
}
