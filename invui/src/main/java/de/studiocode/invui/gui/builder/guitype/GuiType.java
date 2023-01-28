package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.PagedGui;
import de.studiocode.invui.gui.ScrollGui;
import de.studiocode.invui.gui.TabGui;
import de.studiocode.invui.gui.builder.GuiContext;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;

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
