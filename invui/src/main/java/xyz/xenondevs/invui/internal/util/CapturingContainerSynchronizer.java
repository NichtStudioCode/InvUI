package xyz.xenondevs.invui.internal.util;

import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ContainerSynchronizer} that captures all outgoing packets and stores them in a list,
 * until {@link #stopCapture()} is called. After that, all packets are immediately sent to the player.
 */
class CapturingContainerSynchronizer implements ContainerSynchronizer {
    
    private final ServerPlayer player;
    private @Nullable List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
    
    CapturingContainerSynchronizer(ServerPlayer player) {
        this.player = player;
    }
    
    @Override
    public void sendInitialData(AbstractContainerMenu handler, List<ItemStack> stacks, ItemStack cursorStack, int[] properties) {
        send(
            new ClientboundContainerSetContentPacket(
                handler.containerId,
                handler.incrementStateId(),
                stacks,
                cursorStack
            )
        );
        
        for (int i = 0; i < properties.length; i++) {
            sendDataChange(handler, i, properties[i]);
        }
    }
    
    @Override
    public void sendOffHandSlotChange() {
        send(
            new ClientboundContainerSetSlotPacket(
                player.inventoryMenu.containerId,
                player.inventoryMenu.incrementStateId(),
                InventoryMenu.SHIELD_SLOT,
                player.inventoryMenu.getSlot(InventoryMenu.SHIELD_SLOT).getItem().copy()
            )
        );
    }
    
    @Override
    public void sendSlotChange(AbstractContainerMenu handler, int slot, ItemStack stack) {
        send(
            new ClientboundContainerSetSlotPacket(
                handler.containerId,
                handler.incrementStateId(),
                slot,
                stack
            )
        );
    }
    
    @Override
    public void sendCarriedChange(AbstractContainerMenu handler, ItemStack stack) {
        send(new ClientboundSetCursorItemPacket(stack.copy()));
    }
    
    @Override
    public void sendDataChange(AbstractContainerMenu handler, int property, int value) {
        send(new ClientboundContainerSetDataPacket(handler.containerId, property, value));
    }
    
    @Override
    public RemoteSlot createSlot() {
        return player.containerSynchronizer.createSlot();
    }
    
    private void send(Packet<? super ClientGamePacketListener> packet) {
        if (packets != null) {
            packets.add(packet);
        } else {
            player.connection.send(packet);
        }
    }
    
    /**
     * Stops capturing packets and returns all captured packets.
     *
     * @return the captured packets
     */
    public List<Packet<? super ClientGamePacketListener>> stopCapture() {
        if (this.packets != null) {
            var packets = this.packets;
            this.packets = null;
            return packets;
        }
        
        return List.of();
    }
    
}
