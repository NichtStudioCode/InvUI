package de.studiocode.invui.gui.builder.guitype;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.PagedGUI;
import de.studiocode.invui.gui.ScrollGUI;
import de.studiocode.invui.gui.TabGUI;
import de.studiocode.invui.gui.builder.GUIContext;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;

public interface GUIType<G extends GUI, C> {
    
    GUIType<GUI, Void> NORMAL = new NormalGUIType();
    GUIType<PagedGUI<Item>, Item> PAGED_ITEMS = new PagedItemsGUIType();
    GUIType<PagedGUI<GUI>, GUI> PAGED_GUIs = new PagedGUIsGUIType();
    GUIType<TabGUI, GUI> TAB = new TabGUIType();
    GUIType<ScrollGUI<Item>, Item> SCROLL_ITEMS = new ScrollItemsGUIType();
    GUIType<ScrollGUI<GUI>, GUI> SCROLL_GUIS = new ScrollGUIsGUIType();
    GUIType<ScrollGUI<VirtualInventory>, VirtualInventory> SCROLL_INVENTORY = new ScrollVIGUIType();
    
    /**
     * Creates a {@link GUI} of type {@link G} with the given {@link GUIContext}
     *
     * @param context The {@link GUIContext} to create the {@link G} from.
     * @return The created {@link G}
     */
    G createGUI(GUIContext<C> context);
    
}
