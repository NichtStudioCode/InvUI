package xyz.xenondevs.invui.internal.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    private static final PacketListener INSTANCE = new PacketListener();
    
    private final String invuiPacketHandlerName;
    private final Map<UUID, PacketHandler> packetHandlers = new HashMap<>();
    
    private PacketListener() {
        invuiPacketHandlerName = "invui_packet_handler_" + InvUI.getInstance().getPlugin().getName();
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
    
    public void injectOutgoing(Player player, List<Packet<? super ClientGamePacketListener>> packets) {
        if (packets.isEmpty())
            return;
        injectOutgoing(player, new ClientboundBundlePacket(packets));
    }
    
    public void injectOutgoing(Player player, Packet<ClientGamePacketListener> packet) {
        getPacketHandler(player.getUniqueId()).injectOutgoing(packet);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Packet<ServerGamePacketListener>> void redirectIncoming(Player player, Class<? extends T> clazz, Consumer<? super T> handler) {
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
    
    @EventHandler(priority = EventPriority.LOWEST)
    private void handleJoin(PlayerJoinEvent event) {
        injectChannelHandler(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    private void handleQuit(PlayerQuitEvent event) {
        packetHandlers.remove(event.getPlayer().getUniqueId());
    }
    
    private void injectChannelHandler(Player player) {
        if (packetHandlers.containsKey(player.getUniqueId()))
            throw new IllegalStateException("A packet handler is already registered for this player");
        
        var channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        var packetHandler = new PacketHandler(channel);
        packetHandlers.put(player.getUniqueId(), packetHandler);
        channel.pipeline().addBefore(MC_PACKET_HANDLER_NAME, invuiPacketHandlerName, packetHandler);
    }
    
    private void removeChannelHandler(Player player) {
        packetHandlers.remove(player.getUniqueId());
        var channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.pipeline().remove(invuiPacketHandlerName);
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
                // This is a workaround for "promise.channel does not match: com.comphenix.protocol.injector.netty.channel.NettyChannelProxy"
                // If ProtocolLib is installed, this.channel is a proxy channel that delegates to the real channel.
                // Using channel.newPromise(), we can obtain a promise bound to the real channel. Otherwise, netty will throw this exception.
                var channelForPromise = channel.newPromise().channel();
                
                channel.writeAndFlush(packet, new ForceChannelPromise(channelForPromise));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            if (msg instanceof Packet<?> packet && !(promise instanceof ForceChannelPromise)) {
                if (packet instanceof ClientboundBundlePacket bundle) {
                    var subPackets = StreamSupport.stream(bundle.subPackets().spliterator(), false)
                        .<Packet<? super ClientGamePacketListener>>map(this::singleWrite)
                        .filter(Objects::nonNull)
                        .toList();
                    
                    if (subPackets.isEmpty()) {
                        promise.setSuccess();
                    } else {
                        ctx.write(new ClientboundBundlePacket(subPackets), promise);
                    }
                } else {
                    var newPacket = singleWrite((Packet<ClientGamePacketListener>) packet);
                    if (newPacket == null) {
                        promise.setSuccess();
                    } else {
                        ctx.write(newPacket, promise);
                    }
                }
            } else {
                ctx.write(msg, promise);
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
