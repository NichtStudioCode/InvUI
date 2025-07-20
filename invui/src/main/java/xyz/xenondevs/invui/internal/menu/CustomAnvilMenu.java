package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;

import java.util.function.Consumer;

/**
 * A packet-based anvil menu.
 */
public class CustomAnvilMenu extends CustomContainerMenu {
    
    private static final int ENCHANTMENT_COST_DIRTY_MARKER = Integer.MIN_VALUE;
    
    private String renameText = "";
    private @Nullable Consumer<? super String> renameHandler;
    
    /**
     * Creates a new {@link CustomAnvilMenu} for the specified viewer.
     *
     * @param player The player that will view the menu
     */
    public CustomAnvilMenu(Player player) {
        super(MenuType.ANVIL, player);
    }
    
    @Override
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.redirectIncoming(player, ServerboundRenameItemPacket.class, this::handleRename);
        super.open(title);
    }
    
    @Override
    public void handleClosed() {
        var pl = PacketListener.getInstance();
        pl.removeRedirect(player, ServerboundRenameItemPacket.class);
        super.handleClosed();
    }
    
    @Override
    public void setItem(int slot, @Nullable ItemStack item) {
        super.setItem(slot, item);
        
        // updating second input slot causes client-side prediction of enchantment cost
        if (slot == 1) {
            remoteDataSlots[0] = ENCHANTMENT_COST_DIRTY_MARKER;
        }
    }
    
    private void handleRename(ServerboundRenameItemPacket packet) {
        renameText = packet.getName();
        if (renameHandler != null)
            renameHandler.accept(renameText);
        remoteItems.set(2, DIRTY_MARKER);
        remoteDataSlots[0] = ENCHANTMENT_COST_DIRTY_MARKER;
    }
    
    /**
     * Sets the rename handler that is called when the input text changes.
     *
     * @param renameHandler The rename handler to set.
     */
    public void setRenameHandler(Consumer<? super String> renameHandler) {
        this.renameHandler = renameHandler;
    }
    
    /**
     * Gets the current text in the rename field.
     *
     * @return The current text in the rename field
     */
    public String getRenameText() {
        return renameText;
    }
    
    /**
     * Gets the enchantment cost.
     *
     * @return The enchantment cost
     */
    public int getEnchantmentCost() {
        return dataSlots[0];
    }
    
    /**
     * Sets the enchantment cost.
     *
     * @param enchantmentCost The new enchantment cost
     */
    public void setEnchantmentCost(int enchantmentCost) {
        dataSlots[0] = enchantmentCost;
    }
    
}
