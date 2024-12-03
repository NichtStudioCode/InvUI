package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;

import java.util.function.Supplier;

final class NormalMergedWindowImpl extends AbstractMergedWindow {
    
    public NormalMergedWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui gui,
        boolean closeable
    ) {
        super(player, title, gui, createInventory(gui), closeable);
    }
    
    private static Inventory createInventory(Gui gui) {
        if (gui.getWidth() != 9)
            throw new IllegalArgumentException("Gui width has to be 9");
        if (gui.getHeight() <= 4)
            throw new IllegalArgumentException("Gui height has to be bigger than 4");
        
        return Bukkit.createInventory(null, gui.getSize() - 36);
    }
    
    public static final class BuilderImpl
        extends AbstractSingleWindow.AbstractBuilder<Window, Window.Builder.Normal.Merged>
        implements Window.Builder.Normal.Merged
    {
        
        @Override
        public Window build(Player viewer) {
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new NormalMergedWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) guiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
    }
    
}
