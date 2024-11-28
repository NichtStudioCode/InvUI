package xyz.xenondevs.invui.animation.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.animation.Animation;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.util.SlotUtils;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Abstract base class for {@link Animation} implementations.
 */
public abstract class AbstractAnimation implements Animation {
    
    private final List<Runnable> finishHandlers = new ArrayList<>();
    private final int tickDelay;
    
    private Gui gui;
    private int width;
    private int height;
    
    private List<Window> windows;
    private CopyOnWriteArrayList<Integer> slots;
    private BiConsumer<Integer, Integer> show;
    private BukkitTask task;
    
    private int frame;
    private int noViewerTicks;
    
    /**
     * Creates a new {@link AbstractAnimation}.
     *
     * @param tickDelay The delay between each frame
     */
    public AbstractAnimation(int tickDelay) {
        this.tickDelay = tickDelay;
    }
    
    @Override
    public void setGui(Gui gui) {
        this.gui = gui;
        this.width = gui.getWidth();
        this.height = gui.getHeight();
    }
    
    @Override
    public void setWindows(@NotNull List<Window> windows) {
        this.windows = windows;
    }
    
    @Override
    public void addShowHandler(@NotNull BiConsumer<Integer, Integer> show) {
        if (this.show != null) this.show = this.show.andThen(show);
        else this.show = show;
    }
    
    @Override
    public void addFinishHandler(@NotNull Runnable finish) {
        finishHandlers.add(finish);
    }
    
    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), () -> {
            // if there are no viewers for more than 3 ticks, the animation can be cancelled
            if (getCurrentViewers().isEmpty()) {
                noViewerTicks++;
                if (noViewerTicks > 3) {
                    gui.cancelAnimation();
                    return;
                }
            } else noViewerTicks = 0;
            
            // handle the next frame
            handleFrame(frame);
            frame++;
        }, 0, tickDelay);
    }
    
    @Override
    public void cancel() {
        task.cancel();
    }
    
    /**
     * Stops the {@link Animation} and runs finish handlers.
     */
    protected void finish() {
        task.cancel();
        finishHandlers.forEach(Runnable::run);
    }
    
    /**
     * Handles the next frame of the {@link Animation}.
     *
     * @param frame The current frame
     */
    protected abstract void handleFrame(int frame);
    
    /**
     * The slots that are being animated. Animation implementations may or may not remove
     * slots that have been shown from the list.
     *
     * @return The slots that are being animated
     */
    public CopyOnWriteArrayList<Integer> getSlots() {
        return slots;
    }
    
    @Override
    public void setSlots(List<Integer> slots) {
        this.slots = new CopyOnWriteArrayList<>(slots);
    }
    
    /**
     * Shows the given slots.
     *
     * @param slots The slots to show
     */
    protected void show(int... slots) {
        for (int i : slots) show.accept(frame, i);
    }
    
    /**
     * Converts the given x and y coordinates to a slot index.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The slot index
     */
    protected int convToIndex(int x, int y) {
        if (x >= width || y >= height)
            throw new IllegalArgumentException("Coordinates out of bounds");
        
        return SlotUtils.convertToIndex(x, y, width);
    }
    
    /**
     * Gets the width of the {@link Gui} this {@link Animation} is taking place in.
     *
     * @return The width of the {@link Gui}
     */
    protected int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the {@link Gui} this {@link Animation} is taking place in.
     *
     * @return The height of the {@link Gui}
     */
    protected int getHeight() {
        return height;
    }
    
    /**
     * Finds all current viewers of this {@link Animation}.
     *
     * @return The current viewers
     */
    public Set<Player> getCurrentViewers() {
        return windows.stream()
            .map(Window::getCurrentViewer)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
    
}
