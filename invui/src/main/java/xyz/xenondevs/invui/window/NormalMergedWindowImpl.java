package xyz.xenondevs.invui.window;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;

final class NormalMergedWindowImpl extends AbstractMergedWindow {
    
    public NormalMergedWindowImpl(
        @NotNull Player player,
        @Nullable ComponentWrapper title,
        @NotNull AbstractGui gui,
        boolean closeable,
        boolean retain
    ) {
        super(player, title, gui, createInventory(gui), closeable, retain);
        register();
    }
    
    private static Inventory createInventory(Gui gui) {
        if (gui.getWidth() != 9)
            throw new IllegalArgumentException("Gui width has to be 9");
        if (gui.getHeight() <= 4)
            throw new IllegalArgumentException("Gui height has to be bigger than 4");
        
        return Bukkit.createInventory(null, gui.getSize() - 36);
    }
    
    public static final class BuilderImpl
        extends AbstractSingleWindow.AbstractBuilder<Window, Player, Window.Builder.Normal.Merged>
        implements Window.Builder.Normal.Merged
    {
        
        @Override
        public @NotNull Window build() {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new NormalMergedWindowImpl(
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
