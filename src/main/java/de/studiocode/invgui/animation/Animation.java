package de.studiocode.invgui.animation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public interface Animation {
    
    void setPlayer(@NotNull Player player);
    
    void setBounds(int width, int height);
    
    void setSlots(List<Integer> slots);
    
    void addShowHandler(@NotNull BiConsumer<Integer, Integer> show);
    
    void setFinishHandler(@NotNull Runnable finish);
    
    void start();
    
    void cancel();
    
}
