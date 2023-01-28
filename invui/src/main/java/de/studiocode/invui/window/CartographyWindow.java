package de.studiocode.invui.window;

import de.studiocode.inventoryaccess.map.MapIcon;
import de.studiocode.inventoryaccess.map.MapPatch;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CartographyWindow extends Window {
    
    void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons);
    
    default void updateMap(@Nullable MapPatch patch) {
        updateMap(patch, null);
    }
    
    default void updateMap(@Nullable List<MapIcon> icons) {
        updateMap(null, icons);
    }
    
    void resetMap();
    
}
