package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.AnvilInventory;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.i18n.Languages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An {@link AbstractSingleWindow} that uses an {@link AnvilInventory} as the upper inventory.
 * <p>
 * Use the builder obtained by {@link AnvilWindow#single()}, to get an instance of this class.
 */
final class AnvilSingleWindowImpl extends AbstractSingleWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSingleWindowImpl(
        Player player,
        @Nullable Component title,
        AbstractGui gui,
        @Nullable List<Consumer<String>> renameHandlers,
        boolean closable
    ) {
        super(player, title, gui, null, closable);
        anvilInventory = InventoryAccess.createAnvilInventory(
            player,
            title != null ? Languages.getInstance().localized(player, title) : null, 
            renameHandlers
        );
        inventory = anvilInventory.getBukkitInventory();
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        anvilInventory.setItem(slot, itemStack);
    }
    
    @Override
    protected void openInventory(Player viewer) {
        anvilInventory.open();
    }
    
    @Override
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }
    
    public static final class BuilderImpl
        extends AbstractSingleWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder.Single>
        implements AnvilWindow.Builder.Single
    {
        
        private @Nullable List<Consumer<String>> renameHandlers;
        
        @Override
        public BuilderImpl setRenameHandlers(List<Consumer<String>> renameHandlers) {
            this.renameHandlers = renameHandlers;
            return this;
        }
        
        @Override
        public BuilderImpl addRenameHandler(Consumer<String> renameHandler) {
            if (renameHandlers == null)
                renameHandlers = new ArrayList<>();
            
            renameHandlers.add(renameHandler);
            return this;
        }
        
        @Override
        public AnvilWindow build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new AnvilSingleWindowImpl(
                viewer,
                title,
                (AbstractGui) guiSupplier.get(),
                renameHandlers,
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
