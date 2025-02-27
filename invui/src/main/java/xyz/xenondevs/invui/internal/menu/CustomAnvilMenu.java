package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.internal.network.PacketListener;

import java.util.List;
import java.util.function.Consumer;

/**
 * A packet-based anvil menu.
 */
public class CustomAnvilMenu extends CustomContainerMenu {
    
    private final List<Consumer<String>> renameHandlers;
    private String renameText = "";
    
    /**
     * Creates a new {@link CustomAnvilMenu} for the specified viewer.
     *
     * @param player         The player that will view the menu
     * @param renameHandlers A list of handlers that will be called when the rename text field is updated
     */
    public CustomAnvilMenu(Player player, List<Consumer<String>> renameHandlers) {
        super(MenuType.ANVIL, player);
        this.renameHandlers = renameHandlers;
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
    
    private void handleRename(ServerboundRenameItemPacket packet) {
        renameText = packet.getName();
        for (Consumer<String> handler : renameHandlers) {
            handler.accept(packet.getName());
        }
        remoteItems.set(2, DIRTY_MARKER);
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
