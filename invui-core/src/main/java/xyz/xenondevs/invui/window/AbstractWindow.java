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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.component.BungeeComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.GuiParent;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.CompositeInventory;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.util.ArrayUtils;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.util.Pair;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractWindow implements Window, GuiParent {
    
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(InvUI.getInstance().getPlugin(), "slot");
    
    private final Player viewer;
    private final UUID viewerUUID;
    private final SlotElement[] elementsDisplayed;
    private List<Runnable> openHandlers;
    private List<Runnable> closeHandlers;
    private List<Consumer<InventoryClickEvent>> outsideClickHandlers;
    private ComponentWrapper title;
    private boolean closeable;
    private boolean currentlyOpen;
    private boolean hasHandledClose;
    
    public AbstractWindow(Player viewer, ComponentWrapper title, int size, boolean closeable) {
        this.viewer = viewer;
        this.viewerUUID = viewer.getUniqueId();
        this.title = title;
        this.closeable = closeable;
        this.elementsDisplayed = new SlotElement[size];
    }
    
    protected void redrawItem(int index) {
        redrawItem(index, getSlotElement(index), false);
    }
    
    protected void redrawItem(int index, SlotElement element, boolean setItem) {
        // put ItemStack in inventory
        ItemStack itemStack;
        if (element == null || (element instanceof SlotElement.InventorySlotElement && element.getItemStack(getLang()) == null)) {
            ItemProvider background = getGuiAt(index).getFirst().getBackground();
            itemStack = background == null ? null : background.get(getLang());
        } else if (element instanceof SlotElement.LinkedSlotElement && element.getHoldingElement() == null) {
            ItemProvider background = null;
            
            List<Gui> guis = ((SlotElement.LinkedSlotElement) element).getGuiList();
            guis.add(0, getGuiAt(index).getFirst());
            
            for (int i = guis.size() - 1; i >= 0; i--) {
                background = guis.get(i).getBackground();
                if (background != null) break;
            }
            
            itemStack = background == null ? null : background.get(getLang());
        } else {
            SlotElement holdingElement = element.getHoldingElement();
            itemStack = holdingElement.getItemStack(getLang());
            
            if (holdingElement instanceof SlotElement.ItemSlotElement) {
                // This makes every item unique to prevent Shift-DoubleClick "clicking" multiple items at the same time.
                if (itemStack.hasItemMeta()) {
                    // clone ItemStack in order to not modify the original
                    itemStack = itemStack.clone();
                    
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(SLOT_KEY, PersistentDataType.BYTE, (byte) index);
                    itemStack.setItemMeta(itemMeta);
                }
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
            } else if (previousElement instanceof SlotElement.InventorySlotElement) {
                SlotElement.InventorySlotElement invSlotElement = (SlotElement.InventorySlotElement) previousElement;
                Inventory inventory = invSlotElement.getInventory();
                // check if the InvUI-Inventory isn't still present on another index
                if (getInvSlotElements(invSlotElement.getInventory()).size() == 1) {
                    // only if not, remove Window from list in Inventory
                    inventory.removeWindow(this);
                }
            }
            
            if (element != null) {
                // tell the Item or InvUI-Inventory that it is being displayed in this Window
                SlotElement holdingElement = element.getHoldingElement();
                if (holdingElement instanceof SlotElement.ItemSlotElement) {
                    ((SlotElement.ItemSlotElement) holdingElement).getItem().addWindow(this);
                } else if (holdingElement instanceof SlotElement.InventorySlotElement) {
                    ((SlotElement.InventorySlotElement) holdingElement).getInventory().addWindow(this);
                }
                
                elementsDisplayed[index] = holdingElement;
            } else {
                elementsDisplayed[index] = null;
            }
        }
    }
    
    public void handleDragEvent(InventoryDragEvent event) {
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
    
    public void handleClickEvent(InventoryClickEvent event) {
        if (Arrays.asList(getInventories()).contains(event.getClickedInventory())) {
            // The inventory that was clicked is part of the open window
            handleClick(event);
        } else if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            // The player clicked outside the inventory
            if (outsideClickHandlers != null) {
                for (var handler : outsideClickHandlers) {
                    handler.accept(event);
                }
            }
        } else {
            switch (event.getAction()) {
                // The inventory that was clicked is not part of the open window, so it is the player inventory
                case MOVE_TO_OTHER_INVENTORY:
                    handleItemShift(event);
                    break;
                
                // items have been collected by clicking a slot in the player inv
                case COLLECT_TO_CURSOR:
                    handleCursorCollect(event);
                    break;
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    public void handleCursorCollect(InventoryClickEvent event) {
        // cancel event as we do the collection logic ourselves
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        
        // the template item stack that is used to collect similar items
        ItemStack template = event.getCursor();
        int maxStackSize = InventoryUtils.stackSizeProvider.getMaxStackSize(template);
        
        // create a composite inventory consisting of all the gui's inventories and the player's inventory
        List<Inventory> inventories = getContentInventories();
        Inventory inventory = new CompositeInventory(inventories);
        
        // collect items from inventories until the cursor is full
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        int amount = inventory.collectSimilar(updateReason, template);
        
        // put collected items on cursor
        template.setAmount(amount);
        event.setCursor(template);
    }
    
    public void handleItemProviderUpdate(Item item) {
        getItemSlotElements(item).forEach((index, slotElement) ->
            redrawItem(index, slotElement, false));
    }
    
    public void handleInventoryUpdate(Inventory inventory) {
        getInvSlotElements(inventory).forEach((index, slotElement) ->
            redrawItem(index, slotElement, false));
    }
    
    protected Map<Integer, SlotElement> getItemSlotElements(Item item) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof SlotElement.ItemSlotElement
            && ((SlotElement.ItemSlotElement) element).getItem() == item);
    }
    
    protected Map<Integer, SlotElement> getInvSlotElements(Inventory inventory) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof SlotElement.InventorySlotElement
            && ((SlotElement.InventorySlotElement) element).getInventory() == inventory);
    }
    
    @Override
    public void open() {
        Player viewer = getViewer();
        if (viewer == null)
            throw new IllegalStateException("Viewer is not online");
        if (currentlyOpen)
            throw new IllegalStateException("Window is already open");
        
        // call handleCloseEvent() close for currently open window
        AbstractWindow openWindow = (AbstractWindow) WindowManager.getInstance().getOpenWindow(viewer);
        if (openWindow != null) {
            openWindow.handleCloseEvent(true);
        }
        
        currentlyOpen = true;
        hasHandledClose = false;
        initItems();
        WindowManager.getInstance().addWindow(this);
        openInventory(viewer);
    }
    
    protected void openInventory(@NotNull Player viewer) {
        InventoryAccess.getInventoryUtils().openCustomInventory(
            viewer,
            getInventories()[0],
            title.localized(viewer)
        );
    }
    
    public void handleOpenEvent(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(getViewer())) {
            event.setCancelled(true);
        } else {
            handleOpened();
            
            if (openHandlers != null) {
                openHandlers.forEach(Runnable::run);
            }
        }
    }
    
    @Override
    public void close() {
        Player viewer = getCurrentViewer();
        if (viewer != null) {
            handleCloseEvent(true);
            viewer.closeInventory();
        }
    }
    
    public void handleCloseEvent(boolean forceClose) {
        // handleCloseEvent might have already been called by close() or open() if the window was replaced by another one
        if (hasHandledClose)
            return;
        
        if (closeable || forceClose) {
            if (!currentlyOpen)
                throw new IllegalStateException("Window is already closed!");
            
            closeable = true;
            currentlyOpen = false;
            hasHandledClose = true;
            
            remove();
            handleClosed();
            
            if (closeHandlers != null) {
                closeHandlers.forEach(Runnable::run);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(InvUI.getInstance().getPlugin(), () -> openInventory(viewer), 0);
        }
    }
    
    private void remove() {
        WindowManager.getInstance().removeWindow(this);
        
        Arrays.stream(elementsDisplayed)
            .filter(Objects::nonNull)
            .map(SlotElement::getHoldingElement)
            .forEach(slotElement -> {
                if (slotElement instanceof SlotElement.ItemSlotElement) {
                    ((SlotElement.ItemSlotElement) slotElement).getItem().removeWindow(this);
                } else if (slotElement instanceof SlotElement.InventorySlotElement) {
                    ((SlotElement.InventorySlotElement) slotElement).getInventory().removeWindow(this);
                }
            });
        
        Arrays.stream(getGuis())
            .forEach(gui -> gui.removeParent(this));
    }
    
    @Override
    public void changeTitle(@NotNull ComponentWrapper title) {
        this.title = title;
        Player currentViewer = getCurrentViewer();
        if (currentViewer != null) {
            InventoryAccess.getInventoryUtils().updateOpenInventoryTitle(
                currentViewer,
                title.localized(currentViewer)
            );
        }
    }
    
    @Override
    public void changeTitle(@NotNull BaseComponent[] title) {
        changeTitle(new BungeeComponentWrapper(title));
    }
    
    @Override
    public void changeTitle(@NotNull String title) {
        changeTitle(TextComponent.fromLegacyText(title));
    }
    
    @Override
    public void setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers) {
        this.openHandlers = openHandlers;
    }
    
    @Override
    public void addOpenHandler(@NotNull Runnable openHandler) {
        if (openHandlers == null)
            openHandlers = new ArrayList<>();
        
        openHandlers.add(openHandler);
    }
    
    @Override
    public void setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers) {
        this.closeHandlers = closeHandlers;
    }
    
    @Override
    public void addCloseHandler(@NotNull Runnable closeHandler) {
        if (closeHandlers == null)
            closeHandlers = new ArrayList<>();
        
        closeHandlers.add(closeHandler);
    }
    
    @Override
    public void removeCloseHandler(@NotNull Runnable closeHandler) {
        if (closeHandlers != null)
            closeHandlers.remove(closeHandler);
    }
    
    @Override
    public void setOutsideClickHandlers(@Nullable List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers) {
        this.outsideClickHandlers = outsideClickHandlers;
    }
    
    @Override
    public void addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler) {
        if (this.outsideClickHandlers == null)
            this.outsideClickHandlers = new ArrayList<>();
        
        this.outsideClickHandlers.add(outsideClickHandler);
    }
    
    @Override
    public void removeOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler) {
        if (this.outsideClickHandlers != null)
            this.outsideClickHandlers.remove(outsideClickHandler);
    }
    
    @Override
    public @Nullable Player getCurrentViewer() {
        List<HumanEntity> viewers = getInventories()[0].getViewers();
        return viewers.isEmpty() ? null : (Player) viewers.get(0);
    }
    
    @Override
    public @NotNull Player getViewer() {
        return viewer;
    }
    
    public @NotNull String getLang() {
        var player = getViewer();
        if (player == null)
            throw new IllegalStateException("Tried to receive the language from a viewer that is not online.");
        
        return Languages.getInstance().getLanguage(player);
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
    public boolean isOpen() {
        return currentlyOpen;
    }
    
    protected abstract void setInvItem(int slot, ItemStack itemStack);
    
    protected abstract SlotElement getSlotElement(int index);
    
    protected abstract Pair<AbstractGui, Integer> getGuiAt(int index);
    
    protected abstract AbstractGui[] getGuis();
    
    protected abstract org.bukkit.inventory.Inventory[] getInventories();
    
    protected abstract List<xyz.xenondevs.invui.inventory.Inventory> getContentInventories();
    
    protected abstract void initItems();
    
    protected abstract void handleOpened();
    
    protected abstract void handleClosed();
    
    protected abstract void handleClick(InventoryClickEvent event);
    
    protected abstract void handleItemShift(InventoryClickEvent event);
    
    public abstract void handleViewerDeath(PlayerDeathEvent event);
    
    @SuppressWarnings("unchecked")
    public static abstract class AbstractBuilder<W extends Window, S extends Window.Builder<W, S>> implements Window.Builder<W, S> {
        
        protected Player viewer;
        protected ComponentWrapper title;
        protected boolean closeable = true;
        protected List<Runnable> openHandlers;
        protected List<Runnable> closeHandlers;
        protected List<Consumer<InventoryClickEvent>> outsideClickHandlers;
        protected List<Consumer<W>> modifiers;
        
        @Override
        public @NotNull S setViewer(@NotNull Player viewer) {
            this.viewer = viewer;
            return (S) this;
        }
        
        @Override
        public @NotNull S setTitle(@NotNull ComponentWrapper title) {
            this.title = title;
            return (S) this;
        }
        
        @Override
        public @NotNull S setTitle(@NotNull BaseComponent @NotNull [] title) {
            this.title = new BungeeComponentWrapper(title);
            return (S) this;
        }
        
        @Override
        public @NotNull S setTitle(@NotNull String title) {
            this.title = new BungeeComponentWrapper(TextComponent.fromLegacyText(title));
            return (S) this;
        }
        
        @Override
        public @NotNull S setCloseable(boolean closeable) {
            this.closeable = closeable;
            return (S) this;
        }
        
        @Override
        public @NotNull S setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers) {
            this.openHandlers = openHandlers;
            return (S) this;
        }
        
        @Override
        public @NotNull S addOpenHandler(@NotNull Runnable openHandler) {
            if (openHandlers == null)
                openHandlers = new ArrayList<>();
            
            openHandlers.add(openHandler);
            return (S) this;
        }
        
        @Override
        public @NotNull S setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers) {
            this.closeHandlers = closeHandlers;
            return (S) this;
        }
        
        @Override
        public @NotNull S addCloseHandler(@NotNull Runnable closeHandler) {
            if (closeHandlers == null)
                closeHandlers = new ArrayList<>();
            
            closeHandlers.add(closeHandler);
            return (S) this;
        }
        
        @Override
        public @NotNull S setOutsideClickHandlers(@NotNull List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers) {
            this.outsideClickHandlers = outsideClickHandlers;
            return (S) this;
        }
        
        @Override
        public @NotNull S addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler) {
            if (outsideClickHandlers == null)
                outsideClickHandlers = new ArrayList<>();
            
            outsideClickHandlers.add(outsideClickHandler);
            return (S) this;
        }
        
        @Override
        public @NotNull S setModifiers(@Nullable List<@NotNull Consumer<@NotNull W>> modifiers) {
            this.modifiers = modifiers;
            return (S) this;
        }
        
        @Override
        public @NotNull S addModifier(@NotNull Consumer<@NotNull W> modifier) {
            if (modifiers == null)
                modifiers = new ArrayList<>();
            
            modifiers.add(modifier);
            return (S) this;
        }
        
        protected void applyModifiers(W window) {
            if (openHandlers != null)
                window.setOpenHandlers(openHandlers);
            
            if (closeHandlers != null)
                window.setCloseHandlers(closeHandlers);
            
            if (outsideClickHandlers != null)
                window.setOutsideClickHandlers(outsideClickHandlers);
            
            if (modifiers != null)
                modifiers.forEach(modifier -> modifier.accept(window));
        }
        
        @Override
        public @NotNull W build() {
            return build(viewer);
        }
        
        @Override
        public void open(Player viewer) {
            build(viewer).open();
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public @NotNull S clone() {
            try {
                var clone = (AbstractBuilder<W, S>) super.clone();
                if (title != null)
                    clone.title = title.clone();
                if (closeHandlers != null)
                    clone.closeHandlers = new ArrayList<>(closeHandlers);
                if (modifiers != null)
                    clone.modifiers = new ArrayList<>(modifiers);
                return (S) clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
        
    }
    
}