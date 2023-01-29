package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.impl.ScrollInventoryGuiImpl;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

public final class ScrollInventoryGuiBuilder extends AbstractScrollGuiBuilder<VirtualInventory, ScrollInventoryGuiBuilder> {
    
    ScrollInventoryGuiBuilder() {
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ScrollGui<VirtualInventory> build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        
        var gui = new ScrollInventoryGuiImpl(content, structure);
        applyModifiers(gui);
        return gui;
    }
    
    @Override
    protected ScrollInventoryGuiBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull ScrollInventoryGuiBuilder clone() {
        return (ScrollInventoryGuiBuilder) super.clone();
    }
    
}
