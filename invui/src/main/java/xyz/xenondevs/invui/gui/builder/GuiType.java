package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

import java.util.function.Consumer;

public interface GuiType<G extends Gui, B extends GuiBuilder<G>> {
    
    GuiType<Gui, NormalGuiBuilder> NORMAL = NormalGuiBuilder::new;
    GuiType<PagedGui<Item>, PagedItemsGuiBuilder> PAGED_ITEMS = PagedItemsGuiBuilder::new;
    GuiType<PagedGui<Gui>, PagedNestedGuiBuilder> PAGED_GUIS = PagedNestedGuiBuilder::new;
    GuiType<TabGui, TabGuiBuilder> TAB = TabGuiBuilder::new;
    GuiType<ScrollGui<Item>, ScrollItemsGuiBuilder> SCROLL_ITEMS = ScrollItemsGuiBuilder::new;
    GuiType<ScrollGui<Gui>, ScrollNestedGuiBuilder> SCROLL_GUIS = ScrollNestedGuiBuilder::new;
    GuiType<ScrollGui<VirtualInventory>, ScrollInventoryGuiBuilder> SCROLL_INVENTORY = ScrollInventoryGuiBuilder::new;
    
    /**
     * Creates a new {@link GuiBuilder} for this {@link GuiType}.
     *
     * @return The created {@link GuiBuilder}.
     */
    @NotNull B builder();
    
    /**
     * Creates a new {@link Gui} after modifying the {@link GuiBuilder} with the given {@link Consumer}.
     *
     * @param builderConsumer The {@link Consumer} which modifies the {@link GuiBuilder}.
     * @return The new {@link Gui}.
     */
    default @NotNull G createGui(@NotNull Consumer<B> builderConsumer) {
        var builder = builder();
        builderConsumer.accept(builder);
        return builder.build();
    }
    
}
