package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.Viewer;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.inventory.CompositeInventory;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.AbstractItem;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
public sealed abstract class AbstractWindow
    implements Window, Viewer
    permits AbstractSingleWindow, AbstractDoubleWindow
{
    
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(InvUI.getInstance().getPlugin(), "slot");
    
    private final Player viewer;
    private final UUID viewerUUID;
    private @Nullable List<Runnable> openHandlers;
    private @Nullable List<Runnable> closeHandlers;
    private @Nullable List<Consumer<InventoryClickEvent>> outsideClickHandlers;
    private Supplier<Component> titleSupplier;
    private boolean closeable;
    private boolean currentlyOpen;
    private boolean hasHandledClose;
    
    private final int size;
    private final @Nullable SlotElement[] elementsDisplayed;
    private final BitSet dirtySlots;
    
    private @Nullable Component activeTitle;
    
    AbstractWindow(Player viewer, Supplier<Component> titleSupplier, int size, boolean closeable) {
        this.viewer = viewer;
        this.viewerUUID = viewer.getUniqueId();
        this.titleSupplier = titleSupplier;
        this.closeable = closeable;
        this.size = size;
        this.elementsDisplayed = new SlotElement[size];
        this.dirtySlots = new BitSet(size);
    }
    
    protected void update(int index) {
        Pair<AbstractGui, Integer> guiSlotPair = getGuiAt(index);
        if (guiSlotPair == null)
            return;
        AbstractGui gui = guiSlotPair.first();
        int guiSlot = guiSlotPair.second();
        
        SlotElement element = gui.getSlotElement(guiSlot);
        element = element == null ? null : element.getHoldingElement();
        
        // update the slot element's viewers if necessary
        SlotElement previousElement = elementsDisplayed[index];
        if (previousElement != element) {
            switch (previousElement) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).removeViewer(this, index);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().removeViewer(this, invElement.slot(), index);
                case null, default -> {}
            }
            
            switch (element) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).addViewer(this, index);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().addViewer(this, invElement.slot(), index);
                case null, default -> {}
            }
            
            elementsDisplayed[index] = element;
        }
        
        // create and place item stack in inventory
        ItemStack itemStack = null;
        if (element != null) {
            itemStack = element.getItemStack(getViewer());
            if (itemStack != null && element instanceof SlotElement.Item) {
                // This makes every item unique to prevent Shift-DoubleClick "clicking" multiple items at the same time.
                itemStack = itemStack.clone(); // clone ItemStack in order to not modify the original
                itemStack.editMeta(meta ->
                    meta.getPersistentDataContainer().set(SLOT_KEY, PersistentDataType.BYTE, (byte) index)
                );
            }
        } else {
            // background by gui
            element = gui.getSlotElement(guiSlot);
            while (element instanceof SlotElement.GuiLink(Gui linkedGui, int slot)) {
                var backgroundProvider = linkedGui.getBackground();
                if (backgroundProvider != null) {
                    itemStack = backgroundProvider.get(getLocale());
                }
                element = linkedGui.getSlotElement(slot);
            }
        }
        
        setInvItem(index, itemStack);
    }
    
    @Override
    public void notifyUpdate(int slot) {
        if (WindowManager.getInstance().isInInteractionHandlingContext()) {
            update(slot);
        } else {
            synchronized (dirtySlots) {
                dirtySlots.set(slot);
            }
        }
    }
    
    public void handleTick() {
        synchronized (dirtySlots) {
            int slot = 0;
            while ((slot = dirtySlots.nextSetBit(slot)) != -1) {
                update(slot);
                slot++;
            }
            dirtySlots.clear();
        }
        
        if (titleSupplier instanceof AnimatedTitle)
            updateTitle();
    }
    
    public void handleDragEvent(InventoryDragEvent event) {
        Player player = ((Player) event.getWhoClicked()).getPlayer();
        assert player != null;
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Map<Integer, ItemStack> newItems = event.getNewItems();
        
        int itemsLeft = event.getCursor() == null ? 0 : event.getCursor().getAmount();
        for (int rawSlot : event.getRawSlots()) { // loop over all affected slots
            ItemStack currentStack = event.getView().getItem(rawSlot);
            if (currentStack != null && currentStack.getType() == Material.AIR) currentStack = null;
            
            // get the Gui at that slot and ask for permission to drag an Item there
            Pair<AbstractGui, Integer> pair = getGuiAt(rawSlot);
            if (pair != null && !pair.first().handleItemDrag(updateReason, pair.second(), currentStack, newItems.get(rawSlot))) {
                // the drag was cancelled
                int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
                int newAmount = newItems.get(rawSlot).getAmount();
                
                itemsLeft += newAmount - currentAmount;
            }
        }
        
        // Redraw all items after the event so there won't be any Items that aren't actually there
        Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(),
            () -> event.getRawSlots().forEach(rawSlot -> {
                if (getGuiAt(rawSlot) != null) update(rawSlot);
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
    
    @Override
    public void open() {
        Player viewer = getViewer();
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
        for (int i = 0; i < size; i++) {
            SlotElement element = elementsDisplayed[i];
            switch (element) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).addViewer(this, i);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().addViewer(this, invElement.slot(), i);
                case null, default -> {}
            }
            
            var pair = getGuiAt(i);
            assert pair != null;
            pair.first().addViewer(this, pair.second(), i);
        }
        openInventory(viewer);
    }
    
    protected void openInventory(Player viewer) {
        var title = getTitle();
        activeTitle = title;
        InventoryUtils.openCustomInventory(
            viewer,
            getInventories()[0],
            Languages.getInstance().localized(viewer, title)
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
        
        for (int i = 0; i < size; i++) {
            SlotElement element = elementsDisplayed[i];
            switch (element) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).removeViewer(this, i);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().removeViewer(this, invElement.slot(), i);
                case null, default -> {}
            }
            
            var pair = getGuiAt(i);
            assert pair != null;
            pair.first().removeViewer(this, pair.second(), i);
        }
    }
    
    @Override
    public void setTitleSupplier(Supplier<Component> titleSupplier) {
        this.titleSupplier = titleSupplier;
        updateTitle();
    }
    
    @Override
    public void setTitle(Component title) {
        setTitleSupplier(() -> title);
    }
    
    @Override
    public void setTitle(String title) {
        setTitle(MiniMessage.miniMessage().deserialize(title));
    }
    
    protected Component getTitle() {
        return titleSupplier.get();
    }
    
    @Override
    public void updateTitle() {
        Player currentViewer = getCurrentViewer();
        if (currentViewer == null)
            return;
        
        var title = getTitle();
        if (title.equals(activeTitle))
            return;
        activeTitle = title;
        
        InventoryUtils.updateOpenInventoryTitle(
            currentViewer,
            Languages.getInstance().localized(currentViewer, title)
        );
    }
    
    @Override
    public void setOpenHandlers(@Nullable List<Runnable> openHandlers) {
        this.openHandlers = openHandlers;
    }
    
    @Override
    public void addOpenHandler(Runnable openHandler) {
        if (openHandlers == null)
            openHandlers = new ArrayList<>();
        
        openHandlers.add(openHandler);
    }
    
    @Override
    public void setCloseHandlers(@Nullable List<Runnable> closeHandlers) {
        this.closeHandlers = closeHandlers;
    }
    
    @Override
    public void addCloseHandler(Runnable closeHandler) {
        if (closeHandlers == null)
            closeHandlers = new ArrayList<>();
        
        closeHandlers.add(closeHandler);
    }
    
    @Override
    public void removeCloseHandler(Runnable closeHandler) {
        if (closeHandlers != null)
            closeHandlers.remove(closeHandler);
    }
    
    @Override
    public void setOutsideClickHandlers(@Nullable List<Consumer<InventoryClickEvent>> outsideClickHandlers) {
        this.outsideClickHandlers = outsideClickHandlers;
    }
    
    @Override
    public void addOutsideClickHandler(Consumer<InventoryClickEvent> outsideClickHandler) {
        if (this.outsideClickHandlers == null)
            this.outsideClickHandlers = new ArrayList<>();
        
        this.outsideClickHandlers.add(outsideClickHandler);
    }
    
    @Override
    public void removeOutsideClickHandler(Consumer<InventoryClickEvent> outsideClickHandler) {
        if (this.outsideClickHandlers != null)
            this.outsideClickHandlers.remove(outsideClickHandler);
    }
    
    @Override
    public @Nullable Player getCurrentViewer() {
        List<HumanEntity> viewers = getInventories()[0].getViewers();
        return viewers.isEmpty() ? null : (Player) viewers.getFirst();
    }
    
    @Override
    public Player getViewer() {
        return viewer;
    }
    
    public Locale getLocale() {
        return Languages.getInstance().getLocale(getViewer());
    }
    
    @Override
    public UUID getViewerUUID() {
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
    
    protected abstract void setInvItem(int slot, @Nullable ItemStack itemStack);
    
    protected abstract @Nullable Pair<AbstractGui, Integer> getGuiAt(int index);
    
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
    static sealed abstract class AbstractBuilder<W extends Window, S extends Window.Builder<W, S>>
        implements Window.Builder<W, S>
        permits AbstractSingleWindow.AbstractBuilder, AbstractSplitWindow.AbstractBuilder
    {
        
        protected @Nullable Player viewer;
        protected Supplier<Component> titleSupplier = Component::empty;
        protected boolean closeable = true;
        protected @Nullable List<Runnable> openHandlers;
        protected @Nullable List<Runnable> closeHandlers;
        protected @Nullable List<Consumer<InventoryClickEvent>> outsideClickHandlers;
        protected @Nullable List<Consumer<W>> modifiers;
        
        @Override
        public S setViewer(Player viewer) {
            this.viewer = viewer;
            return (S) this;
        }
        
        @Override
        public S setTitleSupplier(Supplier<Component> title) {
            this.titleSupplier = title;
            return (S) this;
        }
        
        @Override
        public S setTitle(Component title) {
            this.titleSupplier = () -> title;
            return (S) this;
        }
        
        @Override
        public S setTitle(String title) {
            this.titleSupplier = () -> MiniMessage.miniMessage().deserialize(title);
            return (S) this;
        }
        
        @Override
        public S setCloseable(boolean closeable) {
            this.closeable = closeable;
            return (S) this;
        }
        
        @Override
        public S setOpenHandlers(@Nullable List<Runnable> openHandlers) {
            this.openHandlers = openHandlers;
            return (S) this;
        }
        
        @Override
        public S addOpenHandler(Runnable openHandler) {
            if (openHandlers == null)
                openHandlers = new ArrayList<>();
            
            openHandlers.add(openHandler);
            return (S) this;
        }
        
        @Override
        public S setCloseHandlers(@Nullable List<Runnable> closeHandlers) {
            this.closeHandlers = closeHandlers;
            return (S) this;
        }
        
        @Override
        public S addCloseHandler(Runnable closeHandler) {
            if (closeHandlers == null)
                closeHandlers = new ArrayList<>();
            
            closeHandlers.add(closeHandler);
            return (S) this;
        }
        
        @Override
        public S setOutsideClickHandlers(List<Consumer<InventoryClickEvent>> outsideClickHandlers) {
            this.outsideClickHandlers = outsideClickHandlers;
            return (S) this;
        }
        
        @Override
        public S addOutsideClickHandler(Consumer<InventoryClickEvent> outsideClickHandler) {
            if (outsideClickHandlers == null)
                outsideClickHandlers = new ArrayList<>();
            
            outsideClickHandlers.add(outsideClickHandler);
            return (S) this;
        }
        
        @Override
        public S setModifiers(@Nullable List<Consumer<W>> modifiers) {
            this.modifiers = modifiers;
            return (S) this;
        }
        
        @Override
        public S addModifier(Consumer<W> modifier) {
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
        public W build() {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            return build(viewer);
        }
        
        @Override
        public void open(Player viewer) {
            build(viewer).open();
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public S clone() {
            try {
                var clone = (AbstractBuilder<W, S>) super.clone();
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