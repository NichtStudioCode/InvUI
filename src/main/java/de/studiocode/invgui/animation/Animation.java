package de.studiocode.invgui.animation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public interface Animation {
    
    /**
     * Sets the {@link Player} that will see this animation.
     * Useful for playing sounds in a showHandler. ({@link #addShowHandler(BiConsumer)})
     *
     * @param player The {@link Player} that will se this {@link Animation}.
     */
    void setPlayer(@NotNull Player player);
    
    /**
     * Sets the bounds of the {@link Inventory} this {@link Animation} will take place in.
     *
     * @param width  The width of the {@link Inventory}
     * @param height The height {@link Inventory}
     */
    void setBounds(int width, int height);
    
    /**
     * Sets the slots that should be shown.
     *
     * @param slots The slots that should be shown.
     */
    void setSlots(List<Integer> slots);
    
    /**
     * Adds a show handler.
     * Can be used to for example play a sound when a slot pops up.
     *
     * @param show The show handler as a {@link BiConsumer} consisting of
     *             frame number (first int) and slot index to show (second int).
     */
    void addShowHandler(@NotNull BiConsumer<Integer, Integer> show);
    
    /**
     * Adds a {@link Runnable} that should run after the {@link Animation} is finished.
     *
     * @param finish The {@link Runnable} that should run after the {@link Animation} is finished.
     */
    void addFinishHandler(@NotNull Runnable finish);
    
    /**
     * Starts the {@link Animation}.
     */
    void start();
    
    /**
     * Cancels the {@link Animation}.
     */
    void cancel();
    
}
