package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.impl.PagedItemsGuiImpl;
import xyz.xenondevs.invui.item.Item;

public final class PagedItemsGuiBuilder extends AbstractPagedGuiBuilder<Item, PagedItemsGuiBuilder> {
    
    PagedItemsGuiBuilder() {
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull PagedGui<Item> build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        
        var gui = new PagedItemsGuiImpl(content, structure);
        applyModifiers(gui);
        return gui;
    }
    
    @Override
    protected PagedItemsGuiBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull PagedItemsGuiBuilder clone() {
        return (PagedItemsGuiBuilder) super.clone();
    }
    
}