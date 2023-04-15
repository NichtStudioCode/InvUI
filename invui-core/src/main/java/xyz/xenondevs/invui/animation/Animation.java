package xyz.xenondevs.invui.animation;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.function.BiConsumer;

public interface Animation {
    
    /**
     * Sets the {@link Gui} this {@link Animation} will take place in.
     *
     * @param gui The {@link Gui} this {@link Animation} will take place in
     */
    void setGui(Gui gui);
    
    /**
     * Sets the {@link Window}s that will see this animation.
     * Useful for playing sounds in a showHandler. ({@link #addShowHandler(BiConsumer)})
     *
     * @param windows The {@link Window}s that will see this animation
     */
    void setWindows(@NotNull List<Window> windows);
    
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
