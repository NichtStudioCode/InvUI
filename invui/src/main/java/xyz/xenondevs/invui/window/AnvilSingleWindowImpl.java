package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.AnvilInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class AnvilSingleWindowImpl extends AbstractSingleWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSingleWindowImpl(
        Player player,
        Component title,
        AbstractGui gui,
        List<Consumer<String>> renameHandlers,
        boolean closable
    ) {
        super(player, title, gui, null, closable);
        anvilInventory = new AnvilInventory(player, Languages.getInstance().localized(player, title), renameHandlers);
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
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new AnvilSingleWindowImpl(
                viewer,
                title,
                (AbstractGui) guiSupplier.get(),
                renameHandlers != null ? renameHandlers : List.of(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
