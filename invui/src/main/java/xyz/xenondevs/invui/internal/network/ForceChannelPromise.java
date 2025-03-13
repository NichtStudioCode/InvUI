package xyz.xenondevs.invui.internal.network;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;

/**
 * A channel promise for outgoing packets that directs {@link PacketListener} to not intercept the packet.
 */
class ForceChannelPromise extends DefaultChannelPromise {
    
    ForceChannelPromise(Channel channel) {
        super(channel);
    }
    
}
