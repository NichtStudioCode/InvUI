package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.AnvilInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class AnvilSingleWindowImpl extends AbstractSingleWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSingleWindowImpl(
        @NotNull Player player,
        @Nullable ComponentWrapper title,
        @NotNull AbstractGui gui,
        @Nullable List<@NotNull Consumer<@NotNull String>> renameHandlers,
        boolean closable,
        boolean retain
    ) {
        super(player.getUniqueId(), title, gui, null, false, closable, retain);
        anvilInventory = InventoryAccess.createAnvilInventory(player, title, renameHandlers);
        inventory = anvilInventory.getBukkitInventory();
        
        initItems();
        register();
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        anvilInventory.setItem(slot, itemStack);
    }
    
    @Override
    public void show() {
        if (isRemoved()) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        anvilInventory.open();
    }
    
    @Override
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }
    
    public static final class BuilderImpl
        extends AbstractSingleWindow.AbstractBuilder<AnvilWindow, Player, AnvilWindow.Builder.Single>
        implements AnvilWindow.Builder.Single
    {
        
        private List<Consumer<String>> renameHandlers;
        
        @Override
        public BuilderImpl setRenameHandlers(@NotNull List<@NotNull Consumer<String>> renameHandlers) {
            this.renameHandlers = renameHandlers;
            return this;
        }
        
        @Override
        public BuilderImpl addRenameHandler(@NotNull Consumer<String> renameHandler) {
            if (renameHandlers == null)
                renameHandlers = new ArrayList<>();
            
            renameHandlers.add(renameHandler);
            return this;
        }
        
        @Override
        public @NotNull AnvilWindow build() {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new AnvilSingleWindowImpl(
                viewer,
                title,
                (AbstractGui) guiSupplier.get(),
                renameHandlers,
                closeable,
                retain
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
