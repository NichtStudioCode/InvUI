package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.impl.ScrollItemsGuiImpl;
import xyz.xenondevs.invui.item.Item;

public final class ScrollItemsGuiBuilder extends AbstractScrollGuiBuilder<Item, ScrollItemsGuiBuilder> {

    ScrollItemsGuiBuilder() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ScrollGui<Item> build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        
        var gui = new ScrollItemsGuiImpl(content, structure);
        applyModifiers(gui);
        return gui;
    }

    @Override
    protected ScrollItemsGuiBuilder getThis() {
        return this;
    }

    @Override
    public @NotNull ScrollItemsGuiBuilder clone() {
        return (ScrollItemsGuiBuilder) super.clone();
    }
    
}
