package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.text.Component;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;

import java.util.function.BiConsumer;

/**
 * A packet-based crafter menu.
 */
public class CustomCrafterMenu extends CustomContainerMenu {
    
    private @Nullable BiConsumer<Integer, Boolean> slotStateChangeHandler;
    
    /**
     * Creates a new custom crafter menu.
     *
     * @param player The player that will view this menu.
     */
    public CustomCrafterMenu(Player player) {
        super(MenuType.CRAFTER_3x3, player);
    }
    
    @Override
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.redirectIncoming(player, ServerboundContainerSlotStateChangedPacket.class, this::handleSlotStateChange);
        
        super.open(title);
    }
    
    @Override
    public void handleClosed() {
        var pl = PacketListener.getInstance();
        pl.removeRedirect(player, ServerboundContainerSlotStateChangedPacket.class);
        
        super.handleClosed();
    }
    
    /**
     * Returns whether the specified slot is disabled.
     *
     * @param slot The slot to check.
     * @return Whether the slot is disabled.
     */
    public boolean isSlotDisabled(int slot) {
        return dataSlots[slot] == 1;
    }
    
    /**
     * Sets the state of the specified slot.
     *
     * @param slot  The slot to set the state of.
     * @param state The new state of the slot.
     */
    public void setSlotDisabled(int slot, boolean state) {
        dataSlots[slot] = state ? 1 : 0;
        remoteItems.set(slot, HashedStack.EMPTY);
    }
    
    /**
     * Sets the slot state change handler.
     *
     * @param slotStateChangeHandler The slot state change handler.
     */
    public void setSlotStateChangeHandler(BiConsumer<Integer, Boolean> slotStateChangeHandler) {
        this.slotStateChangeHandler = slotStateChangeHandler;
    }
    
    private void handleSlotStateChange(ServerboundContainerSlotStateChangedPacket packet) {
        int slot = packet.slotId();
        int value = packet.newState() ? 0 : 1;
        remoteDataSlots[slot] = value;
        dataSlots[slot] = value;
        
        if (slotStateChangeHandler != null)
            slotStateChangeHandler.accept(slot, !packet.newState());
    }
    
}
