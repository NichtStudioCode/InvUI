package xyz.xenondevs.invui.gui.builder.guitype;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.gui.builder.GuiContext;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

public interface GuiType<G extends Gui, C> {
    
    GuiType<Gui, Void> NORMAL = new NormalGuiType();
    GuiType<PagedGui<Item>, Item> PAGED_ITEMS = new PagedItemsGuiType();
    GuiType<PagedGui<Gui>, Gui> PAGED_GUIS = new PagedGuisGuiType();
    GuiType<TabGui, Gui> TAB = new TabGuiType();
    GuiType<ScrollGui<Item>, Item> SCROLL_ITEMS = new ScrollItemsGuiType();
    GuiType<ScrollGui<Gui>, Gui> SCROLL_GUIS = new ScrollGuisGuiType();
    GuiType<ScrollGui<VirtualInventory>, VirtualInventory> SCROLL_INVENTORY = new ScrollVIGuiType();
    
    /**
     * Creates a {@link Gui} of type {@link G} with the given {@link GuiContext}
     *
     * @param context The {@link GuiContext} to create the {@link G} from.
     * @return The created {@link G}
     */
    G createGui(GuiContext<C> context);
    
}
