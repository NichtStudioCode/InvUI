package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.AbstractPagedGUI;
import de.studiocode.invui.gui.AbstractScrollGUI;
import de.studiocode.invui.gui.AbstractTabGUI;

/**
 * Registry class for default markers
 */
public class Markers {
    
    /**
     * The marker for horizontal content list slots in {@link AbstractPagedGUI PagedGUIs},
     * {@link AbstractScrollGUI ScrollGUIs} and {@link AbstractTabGUI TabGUIs}
     */
    public static final Marker CONTENT_LIST_SLOT_HORIZONTAL = new Marker(true);
    
    /**
     * The marker for vertical content list slots in {@link AbstractPagedGUI PagedGUIs},
     * {@link AbstractScrollGUI ScrollGUIs} and {@link AbstractTabGUI TabGUIs}
     */
    public static final Marker CONTENT_LIST_SLOT_VERTICAL = new Marker(false);
    
}
