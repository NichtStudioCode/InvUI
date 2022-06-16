package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.impl.PagedGUI;
import de.studiocode.invui.gui.impl.ScrollGUI;
import de.studiocode.invui.gui.impl.TabGUI;

/**
 * Registry class for default markers
 */
public class Markers {
    
    /**
     * The marker for horizontal item list slots in {@link PagedGUI PagedGUIs},
     * {@link ScrollGUI ScrollGUIs} and {@link TabGUI TabGUIs}
     */
    public static final Marker ITEM_LIST_SLOT_HORIZONTAL = new Marker(true);
    
    /**
     * The marker for vertical item list slots in {@link PagedGUI PagedGUIs},
     * {@link ScrollGUI ScrollGUIs} and {@link TabGUI TabGUIs}
     */
    public static final Marker ITEM_LIST_SLOT_VERTICAL = new Marker(false);
    
}
