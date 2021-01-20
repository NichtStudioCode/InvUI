package de.studiocode.invgui.window.impl;

import de.studiocode.invgui.InvGui;
import de.studiocode.invgui.animation.Animation;
import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.util.ArrayUtils;
import de.studiocode.invgui.window.Window;
import de.studiocode.invgui.window.WindowManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseWindow implements Window {
    
    private final GUI gui;
    private final int size;
    private final UUID viewerUUID;
    private final Inventory inventory;
    private final boolean closeOnEvent;
    private final Item[] itemsDisplayed;
    
    private Animation animation;
    private boolean closeable;
    private boolean closed;
    
    public BaseWindow(UUID viewerUUID, GUI gui, Inventory inventory, boolean closeable, boolean closeOnEvent) {
        this.gui = gui;
        this.size = gui.getSize();
        this.viewerUUID = viewerUUID;
        this.inventory = inventory;
        this.closeable = closeable;
        this.closeOnEvent = closeOnEvent;
        this.itemsDisplayed = new Item[size];
        
        initItems();
        WindowManager.getInstance().addWindow(this);
    }
    
    private void initItems() {
        for (int i = 0; i < size; i++) {
            Item item = gui.getItem(i);
            if (item != null) redrawItem(i, item, true);
        }
    }
    
    private void redrawItem(int index, Item item, boolean setItem) {
        inventory.setItem(index, item == null ? null : item.getItemBuilder().buildFor(viewerUUID));
        if (setItem) {
            // tell the Item that this is now its Window
            if (item != null) item.addWindow(this);
            
            // tell the previous item (if there is one) that this is no longer its window
            Item previousItem = itemsDisplayed[index];
            if (previousItem != null) previousItem.removeWindow(this);
            
            itemsDisplayed[index] = item;
        }
    }
    
    @Override
    public void handleTick() {
        for (int i = 0; i < size; i++) {
            Item item = gui.getItem(i);
            if (itemsDisplayed[i] != item) redrawItem(i, item, true);
        }
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (animation == null) { // if not in animation, let the gui handle the click
            gui.handleClick(event.getSlot(), (Player) event.getWhoClicked(), event.getClick(), event);
        }
    }
    
    @Override
    public void handleOpen(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(getViewer()))
            event.setCancelled(true);
    }
    
    @Override
    public void handleClose(Player player) {
        if (closeable) {
            stopAnimation();
            if (closeOnEvent) close(false);
        } else {
            if (player.equals(getViewer()))
                Bukkit.getScheduler().runTaskLater(InvGui.getInstance().getPlugin(),
                    () -> player.openInventory(inventory), 0);
        }
    }
    
    @Override
    public void handleItemBuilderUpdate(Item item) {
        for (int i : ArrayUtils.findAllOccurrences(itemsDisplayed, item)) {
            redrawItem(i, item, false);
        }
    }
    
    @Override
    public void close(boolean closeForViewer) {
        closed = true;
        
        WindowManager.getInstance().removeWindow(this);
        
        Arrays.stream(itemsDisplayed)
            .filter(Objects::nonNull)
            .forEach(item -> item.removeWindow(this));
        
        if (closeForViewer) closeForViewer();
    }
    
    @Override
    public void closeForViewer() {
        closeable = true;
        new ArrayList<>(inventory.getViewers()).forEach(HumanEntity::closeInventory); // clone list to prevent ConcurrentModificationException
    }
    
    @Override
    public void show() {
        if (closed) throw new IllegalStateException("The Window has already been closed.");
        if (inventory.getViewers().size() != 0) throw new IllegalStateException("A Window can only have one viewer.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        viewer.openInventory(inventory);
    }
    
    @Override
    public void playAnimation(Animation animation) {
        if (getViewer() != null) {
            this.animation = animation;
            
            animation.setBounds(getGui().getWidth(), getGui().getHeight());
            animation.setPlayer(getViewer());
            animation.addShowHandler((frame, index) -> redrawItem(index, itemsDisplayed[index], false));
            animation.setFinishHandler(() -> this.animation = null);
            animation.setSlots(IntStream.range(0, size)
                .filter(i -> itemsDisplayed[i] != null)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new)));
            
            clearItemStacks();
            animation.start();
        }
    }
    
    private void stopAnimation() {
        if (this.animation != null) {
            // cancel the scheduler task and set animation to null
            animation.cancel();
            animation = null;
            
            // show all items again
            for (int i = 0; i < gui.getSize(); i++)
                redrawItem(i, itemsDisplayed[i], false);
        }
    }
    
    private void clearItemStacks() {
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, null);
    }
    
    @Override
    public Player getViewer() {
        return Bukkit.getPlayer(viewerUUID);
    }
    
    @Override
    public UUID getViewerUUID() {
        return viewerUUID;
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    @Override
    public GUI getGui() {
        return gui;
    }
    
    @Override
    public boolean isCloseable() {
        return closeable;
    }
    
    @Override
    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
}