package xyz.xenondevs.invui.window;

import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.map.MapIcon;
import xyz.xenondevs.inventoryaccess.map.MapPatch;

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
