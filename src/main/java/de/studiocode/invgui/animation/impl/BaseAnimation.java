package de.studiocode.invgui.animation.impl;

import de.studiocode.invgui.InvGui;
import de.studiocode.invgui.animation.Animation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public abstract class BaseAnimation implements Animation {
    
    private final int tickDelay;
    private final List<Runnable> finishHandlers = new ArrayList<>();
    private int width;
    private int height;
    private int size;
    private Player player;
    private CopyOnWriteArrayList<Integer> slots;
    private BiConsumer<Integer, Integer> show;
    private BukkitTask task;
    private int frame;
    
    public BaseAnimation(int tickDelay) {
        this.tickDelay = tickDelay;
    }
    
    protected abstract void handleFrame(int frame);
    
    @Override
    public void setBounds(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
    }
    
    @Override
    public void addShowHandler(@NotNull BiConsumer<Integer, Integer> show) {
        if (this.show != null) this.show = this.show.andThen(show);
        else this.show = show;
    }
    
    protected void show(int i) {
        show.accept(frame, i);
    }
    
    @Override
    public void addFinishHandler(@NotNull Runnable finish) {
        finishHandlers.add(finish);
    }
    
    public CopyOnWriteArrayList<Integer> getSlots() {
        return slots;
    }
    
    @Override
    public void setSlots(List<Integer> slots) {
        this.slots = new CopyOnWriteArrayList<>(slots);
    }
    
    protected void finished() {
        task.cancel();
        finishHandlers.forEach(Runnable::run);
    }
    
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(InvGui.getInstance().getPlugin(), () -> {
            handleFrame(frame);
            frame++;
        }, 0, tickDelay);
    }
    
    public void cancel() {
        task.cancel();
    }
    
    protected int getWidth() {
        return width;
    }
    
    protected int getHeight() {
        return height;
    }
    
    protected int getSize() {
        return size;
    }
    
    protected Player getPlayer() {
        return player;
    }
    
    public void setPlayer(@NotNull Player player) {
        this.player = player;
    }
    
}
