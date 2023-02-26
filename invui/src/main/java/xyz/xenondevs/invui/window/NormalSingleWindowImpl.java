package xyz.xenondevs.invui.window;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.util.InventoryUtils;

import java.util.UUID;

final class NormalSingleWindowImpl extends AbstractSingleWindow {
    
    public NormalSingleWindowImpl(
        @NotNull UUID viewerUUID,
        @Nullable ComponentWrapper title,
        @NotNull AbstractGui gui,
        boolean closeable,
        boolean retain
    ) {
        super(viewerUUID, title, gui, InventoryUtils.createMatchingInventory(gui, ""), true, closeable, retain);
        register();
    }
    
    public static final class BuilderImpl 
        extends AbstractSingleWindow.AbstractBuilder<Window, UUID, Window.Builder.Normal.Single> 
        implements Window.Builder.Normal.Single
    {
        
        @Contract("_ -> this")
        public BuilderImpl setViewer(@NotNull OfflinePlayer player) {
            setViewer(player.getUniqueId());
            return this;
        }
        
        @Override
        public @NotNull Window build() {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new NormalSingleWindowImpl(
                viewer,
                title,
                (AbstractGui) guiSupplier.get(),
                closeable,
                retain
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
