package xyz.xenondevs.invui.window;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.component.BaseComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.GuiParent;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.util.ArrayUtils;
import xyz.xenondevs.invui.util.Pair;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;
import xyz.xenondevs.invui.virtualinventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.virtualinventory.event.UpdateReason;

import java.util.*;

public abstract class AbstractWindow implements Window, GuiParent {
    
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(InvUI.getInstance().getPlugin(), "slot");
    
    private final UUID viewerUUID;
    private final boolean retain;
    private final SlotElement[] elementsDisplayed;
    private final ArrayList<Runnable> closeHandlers = new ArrayList<>();
    private ComponentWrapper title;
    private boolean closeable;
    private boolean removed;
    
    public AbstractWindow(UUID viewerUUID, ComponentWrapper title, int size, boolean closeable, boolean retain) {
        this.viewerUUID = viewerUUID;
        this.title = title;
        this.closeable = closeable;
        this.retain = retain;
        this.elementsDisplayed = new SlotElement[size];
    }
    
    protected void register() {
        WindowManager.getInstance().addWindow(this);
    }
    
    protected void redrawItem(int index) {
        redrawItem(index, getSlotElement(index), false);
    }
    
    protected void redrawItem(int index, SlotElement element, boolean setItem) {
        // put ItemStack in inventory
        ItemStack itemStack;
        if (element == null || (element instanceof SlotElement.VISlotElement && element.getItemStack(viewerUUID) == null)) {
            ItemProvider background = getGuiAt(index).getFirst().getBackground();
            itemStack = background == null ? null : background.getFor(viewerUUID);
        } else if (element instanceof SlotElement.LinkedSlotElement && element.getHoldingElement() == null) {
            ItemProvider background = null;
            
            List<Gui> guis = ((SlotElement.LinkedSlotElement) element).getGuiList();
            guis.add(0, getGuiAt(index).getFirst());
            
            for (int i = guis.size() - 1; i >= 0; i--) {
                background = guis.get(i).getBackground();
                if (background != null) break;
            }
            
            itemStack = background == null ? null : background.getFor(viewerUUID);
        } else {
            itemStack = element.getItemStack(viewerUUID);
            
            // This makes every item unique to prevent Shift-DoubleClick "clicking" multiple items at the same time.
            if (itemStack.hasItemMeta()) {
                // clone ItemStack in order to not modify the original
                itemStack = itemStack.clone();
                
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(SLOT_KEY, PersistentDataType.BYTE, (byte) index);
                itemStack.setItemMeta(itemMeta);
            }
        }
        setInvItem(index, itemStack);
        
        if (setItem) {
            // tell the previous item (if there is one) that this is no longer its window
            SlotElement previousElement = elementsDisplayed[index];
            if (previousElement instanceof SlotElement.ItemSlotElement) {
                SlotElement.ItemSlotElement itemSlotElement = (SlotElement.ItemSlotElement) previousElement;
                Item item = itemSlotElement.getItem();
                // check if the Item isn't still present on another index
                if (getItemSlotElements(item).size() == 1) {
                    // only if not, remove Window from list in Item
                    item.removeWindow(this);
                }
            } else if (previousElement instanceof SlotElement.VISlotElement) {
                SlotElement.VISlotElement viSlotElement = (SlotElement.VISlotElement) previousElement;
                VirtualInventory virtualInventory = viSlotElement.getVirtualInventory();
                // check if the VirtualInventory isn't still present on another index
                if (getVISlotElements(viSlotElement.getVirtualInventory()).size() == 1) {
                    // only if not, remove Window from list in VirtualInventory
                    virtualInventory.removeWindow(this);
                }
            }
            
            if (element != null) {
                // tell the Item or VirtualInventory that it is being displayed in this Window
                SlotElement holdingElement = element.getHoldingElement();
                if (holdingElement instanceof SlotElement.ItemSlotElement) {
                    ((SlotElement.ItemSlotElement) holdingElement).getItem().addWindow(this);
                } else if (holdingElement instanceof SlotElement.VISlotElement) {
                    ((SlotElement.VISlotElement) holdingElement).getVirtualInventory().addWindow(this);
                }
                
                elementsDisplayed[index] = holdingElement;
            } else {
                elementsDisplayed[index] = null;
            }
        }
    }
    
    public void handleDrag(InventoryDragEvent event) {
        Player player = ((Player) event.getWhoClicked()).getPlayer();
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Map<Integer, ItemStack> newItems = event.getNewItems();
        
        int itemsLeft = event.getCursor() == null ? 0 : event.getCursor().getAmount();
        for (int rawSlot : event.getRawSlots()) { // loop over all affected slots
            ItemStack currentStack = event.getView().getItem(rawSlot);
            if (currentStack != null && currentStack.getType() == Material.AIR) currentStack = null;
            
            // get the Gui at that slot and ask for permission to drag an Item there
            Pair<AbstractGui, Integer> pair = getGuiAt(rawSlot);
            if (pair != null && !pair.getFirst().handleItemDrag(updateReason, pair.getSecond(), currentStack, newItems.get(rawSlot))) {
                // the drag was cancelled
                int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
                int newAmount = newItems.get(rawSlot).getAmount();
                
                itemsLeft += newAmount - currentAmount;
            }
        }
        
        // Redraw all items after the event so there won't be any Items that aren't actually there
        Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(),
            () -> event.getRawSlots().forEach(rawSlot -> {
                if (getGuiAt(rawSlot) != null) redrawItem(rawSlot);
            })
        );
        
        // update the amount on the cursor
        ItemStack cursorStack = event.getOldCursor();
        cursorStack.setAmount(itemsLeft);
        event.setCursor(cursorStack);
    }
    
    public void handleOpen(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(getViewer()))
            event.setCancelled(true);
        else handleOpened();
    }
    
    public void handleClose(Player player) {
        if (closeable) {
            if (!retain) {
                remove(false);
            }
            
            handleClosed();
            closeHandlers.forEach(Runnable::run);
        } else {
            if (player.equals(getViewer()))
                Bukkit.getScheduler().runTaskLater(InvUI.getInstance().getPlugin(), this::show, 0);
        }
    }
    
    public void handleItemProviderUpdate(Item item) {
        getItemSlotElements(item).forEach((index, slotElement) ->
            redrawItem(index, slotElement, false));
    }
    
    public void handleVirtualInventoryUpdate(VirtualInventory virtualInventory) {
        getVISlotElements(virtualInventory).forEach((index, slotElement) ->
            redrawItem(index, slotElement, false));
    }
    
    protected Map<Integer, SlotElement> getItemSlotElements(Item item) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof SlotElement.ItemSlotElement
            && ((SlotElement.ItemSlotElement) element).getItem() == item);
    }
    
    protected Map<Integer, SlotElement> getVISlotElements(VirtualInventory virtualInventory) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof SlotElement.VISlotElement
            && ((SlotElement.VISlotElement) element).getVirtualInventory() == virtualInventory);
    }
    
    public void remove() {
        remove(true);
    }
    
    public void remove(boolean closeForViewer) {
        if (removed)
            return;
        removed = true;
        
        WindowManager.getInstance().removeWindow(this);
        
        Arrays.stream(elementsDisplayed)
            .filter(Objects::nonNull)
            .map(SlotElement::getHoldingElement)
            .forEach(slotElement -> {
                if (slotElement instanceof SlotElement.ItemSlotElement) {
                    ((SlotElement.ItemSlotElement) slotElement).getItem().removeWindow(this);
                } else if (slotElement instanceof SlotElement.VISlotElement) {
                    ((SlotElement.VISlotElement) slotElement).getVirtualInventory().removeWindow(this);
                }
            });
        
        Arrays.stream(getGuis())
            .forEach(gui -> gui.removeParent(this));
        
        if (closeForViewer) close();
    }
    
    @Override
    public void close() {
        closeable = true;
        
        Player viewer = getCurrentViewer();
        if (viewer != null) {
            viewer.closeInventory();
            handleClosed();
        }
    }
    
    @Override
    public void show() {
        if (removed) throw new IllegalStateException("The Window has already been closed.");
        
        Player viewer = getViewer();
        if (viewer == null) throw new IllegalStateException("The player is not online.");
        InventoryAccess.getInventoryUtils().openCustomInventory(viewer, getInventories()[0], title);
    }
    
    @Override
    public void changeTitle(@NotNull ComponentWrapper title) {
        this.title = title;
        Player currentViewer = getCurrentViewer();
        if (currentViewer != null) {
            InventoryAccess.getInventoryUtils().updateOpenInventoryTitle(currentViewer, title);
        }
    }
    
    @Override
    public void changeTitle(@NotNull BaseComponent[] title) {
        changeTitle(new BaseComponentWrapper(title));
    }
    
    @Override
    public void changeTitle(@NotNull String title) {
        changeTitle(TextComponent.fromLegacyText(title));
    }
    
    @Override
    public void addCloseHandler(@NotNull Runnable closeHandler) {
        closeHandlers.add(closeHandler);
    }
    
    @Override
    public void removeCloseHandler(@NotNull Runnable closeHandler) {
        closeHandlers.remove(closeHandler);
    }
    
    @Override
    public @Nullable Player getCurrentViewer() {
        List<HumanEntity> viewers = getInventories()[0].getViewers();
        return viewers.isEmpty() ? null : (Player) viewers.get(0);
    }
    
    @Override
    public @Nullable Player getViewer() {
        return Bukkit.getPlayer(viewerUUID);
    }
    
    @Override
    public @NotNull UUID getViewerUUID() {
        return viewerUUID;
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
    public boolean isRemoved() {
        return removed;
    }
    
    protected abstract void setInvItem(int slot, ItemStack itemStack);
    
    protected abstract SlotElement getSlotElement(int index);
    
    protected abstract Pair<AbstractGui, Integer> getGuiAt(int index);
    
    protected abstract AbstractGui[] getGuis();
    
    protected abstract Inventory[] getInventories();
    
    protected abstract void handleOpened();
    
    protected abstract void handleClosed();
    
    public abstract void handleClick(InventoryClickEvent event);
    
    public abstract void handleItemShift(InventoryClickEvent event);
    
    public abstract void handleCursorCollect(InventoryClickEvent event);
    
    public abstract void handleViewerDeath(PlayerDeathEvent event);
    
}