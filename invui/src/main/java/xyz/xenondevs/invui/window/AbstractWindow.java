package xyz.xenondevs.invui.window;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.ClickEvent;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.Viewer;
import xyz.xenondevs.invui.internal.menu.CustomContainerMenu;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.inventory.CompositeInventory;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.InventorySlot;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.util.ItemUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static xyz.xenondevs.invui.internal.util.CollectionUtils.forEachCatching;

/**
 * @hidden
 */
@ApiStatus.Internal
public sealed abstract class AbstractWindow<M extends CustomContainerMenu>
    implements Window, Viewer
    permits AbstractMergedWindow, AbstractSplitWindow
{
    
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(InvUI.getInstance().getPlugin(), "slot");
    private static final ThreadLocal<Boolean> isInOpeningContext = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> isInCloseHandlerContext = ThreadLocal.withInitial(() -> 0);
    
    protected final M menu;
    private final Player viewer;
    private final List<Runnable> openHandlers = new ArrayList<>(0);
    private final List<Consumer<? super InventoryCloseEvent.Reason>> closeHandlers = new ArrayList<>(0);
    private final List<Consumer<? super ClickEvent>> outsideClickHandlers = new ArrayList<>(0);
    private Supplier<? extends @Nullable Window> fallbackWindow = () -> null;
    private Supplier<Component> titleSupplier;
    private boolean closeable;
    private boolean isOpen;
    
    private final int size;
    private final @Nullable SlotElement[] elementsDisplayed;
    private final BitSet dirtySlots;
    
    private @Nullable Component activeTitle;
    
    AbstractWindow(Player viewer, Supplier<Component> titleSupplier, int size, M menu, boolean closeable) {
        this.menu = menu;
        this.viewer = viewer;
        this.titleSupplier = titleSupplier;
        this.closeable = closeable;
        this.size = size;
        this.elementsDisplayed = new SlotElement[size];
        this.dirtySlots = new BitSet(size);
        
        menu.setWindow(this);
    }
    
    protected void update(int slot) {
        Pair<AbstractGui, Integer> guiSlotPair = getGuiAt(slot);
        if (guiSlotPair == null)
            return;
        AbstractGui gui = guiSlotPair.first();
        int guiSlot = guiSlotPair.second();
        
        SlotElement element = gui.getSlotElement(guiSlot);
        element = element == null ? null : element.getHoldingElement();
        
        // update the slot element's viewers if necessary
        SlotElement previousElement = elementsDisplayed[slot];
        if (previousElement != element) {
            switch (previousElement) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).removeViewer(this, slot);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().removeViewer(this, invElement.slot(), slot);
                case null, default -> {}
            }
            
            switch (element) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).addViewer(this, slot);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().addViewer(this, invElement.slot(), slot);
                case null, default -> {}
            }
            
            elementsDisplayed[slot] = element;
        }
        
        // create and place item stack in inventory
        ItemStack itemStack = null;
        if (element != null) {
            itemStack = element.getItemStack(getViewer());
            if (itemStack != null && element instanceof SlotElement.Item) {
                // This makes every item unique to prevent Shift-DoubleClick "clicking" multiple items at the same time.
                itemStack = itemStack.clone(); // clone ItemStack in order to not modify the original
                itemStack.editMeta(meta ->
                    meta.getPersistentDataContainer().set(SLOT_KEY, PersistentDataType.BYTE, (byte) slot)
                );
            }
        } else { // holding element is null
            // background by gui
            var initialBackgroundProvider = gui.getBackground();
            if (initialBackgroundProvider != null) {
                itemStack = initialBackgroundProvider.get(getLocale());
            }
            // if gui link, choose background of lowest gui
            element = gui.getSlotElement(guiSlot);
            while (element instanceof SlotElement.GuiLink(Gui linkedGui, int linkedSlot)) {
                var backgroundProvider = linkedGui.getBackground();
                if (backgroundProvider != null) {
                    itemStack = backgroundProvider.get(getLocale());
                }
                element = linkedGui.getSlotElement(linkedSlot);
            }
        }
        
        setMenuItem(slot, itemStack);
    }
    
    protected void setMenuItem(int slot, @Nullable ItemStack itemStack) {
        menu.setItem(slot, itemStack);
    }
    
    @Override
    public void notifyUpdate(int slot) {
        if (CustomContainerMenu.isInInteractionHandlingContext()) {
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
        
        menu.sendChangesToRemote();
    }
    
    public void handleClick(int slot, Click click) {
        if (slot != -999) { // inside
            var pair = getGuiAt(slot);
            if (pair != null) {
                pair.first().handleClick(pair.second(), click);
            }
        } else { // outside
            var event = new ClickEvent(click);
            outsideClickHandlers.forEach(handler -> handler.accept(event));
            
            var cursor = viewer.getItemOnCursor();
            if (!event.isCancelled() && !ItemUtils.isEmpty(cursor)) {
                switch (click.clickType()) {
                    case LEFT -> {
                        InventoryUtils.dropItemLikePlayer(viewer, cursor);
                        viewer.setItemOnCursor(null);
                    }
                    
                    case RIGHT -> {
                        var drop = cursor.clone();
                        drop.setAmount(1);
                        InventoryUtils.dropItemLikePlayer(viewer, drop);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                    
                    default -> {}
                }
            }
        }
    }
    
    public void handleBundleSelect(int slot, int bundleSlot) {
        var pair = getGuiAt(slot);
        if (pair == null)
            return;
        pair.first().handleBundleSelect(getViewer(), pair.second(), bundleSlot);
    }
    
    public void handleDrag(IntSet slots, ClickType mode) {
        if (mode == ClickType.MIDDLE && viewer.getGameMode() != GameMode.CREATIVE)
            return;
        
        // fixme: this does not consider in-between gui's frozen/animation state
        List<InventorySlot> invSlots = slots.intStream()
            .mapToObj(this::getGuiAt)
            .filter(Objects::nonNull)
            .filter(pair -> !pair.first().isFrozen() && !pair.first().isAnimationRunning())
            .map(pair -> {
                var element = pair.first().getSlotElement(pair.second());
                if (element != null)
                    element = element.getHoldingElement();
                
                if (element instanceof SlotElement.InventoryLink link)
                    return new InventorySlot(link.inventory(), link.slot());
                
                return null;
            })
            .filter(Objects::nonNull)
            .toList();
        
        if (invSlots.isEmpty())
            return;
        
        ItemStack cursor = viewer.getItemOnCursor();
        var updateReason = new PlayerUpdateReason.Drag(viewer, mode, invSlots);
        switch (mode) {
            // distribute items from cursor equally onto all slots
            case ClickType.LEFT -> {
                //noinspection StatementWithEmptyBody
                while (distributeItems(updateReason, invSlots)) {
                    // repeat until no more items can be distributed
                }
            }
            
            // put one item from cursor onto each slot
            case ClickType.RIGHT -> {
                int amount = cursor.getAmount();
                ItemStack toAdd = cursor.clone();
                toAdd.setAmount(1);
                for (var slot : invSlots) {
                    int leftover = slot.inventory().putItem(updateReason, slot.slot(), toAdd);
                    amount -= 1 - leftover;
                    if (amount <= 0)
                        break;
                }
                
                cursor.setAmount(amount);
            }
            
            // put full stack of cursor onto each slot
            case ClickType.MIDDLE -> {
                ItemStack toAdd = cursor.clone();
                toAdd.setAmount(toAdd.getMaxStackSize());
                for (var slot : invSlots) {
                    slot.inventory().putItem(updateReason, slot.slot(), toAdd);
                }
                
                cursor.setAmount(0);
            }
        }
    }
    
    private boolean distributeItems(UpdateReason updateReason, List<InventorySlot> slots) {
        ItemStack cursor = viewer.getItemOnCursor();
        int amount = cursor.getAmount();
        int itemsPerSlot = amount / slots.size();
        
        if (itemsPerSlot <= 0)
            return false;
        
        var toAdd = cursor.clone();
        toAdd.setAmount(itemsPerSlot);
        
        boolean changed = false;
        for (var slot : slots) {
            int leftover = slot.inventory().putItem(updateReason, slot.slot(), toAdd);
            if (leftover < itemsPerSlot) {
                changed = true;
                amount -= itemsPerSlot - leftover;
            }
        }
        
        cursor.setAmount(amount);
        return changed;
    }
    
    public void handleCursorCollect(Click click) { // TODO: prioritize the content inventory where the cursor is
        Player player = click.player();
        
        // the template item stack that is used to collect similar items
        ItemStack template = player.getItemOnCursor();
        
        // create a composite inventory consisting of all the gui's inventories and the player's inventory
        List<? extends Inventory> inventories = getGuis().stream()
            .flatMap(g -> g.getInventories().stream())
            .toList();
        Inventory inventory = new CompositeInventory(inventories);
        
        // collect items from inventories until the cursor is full
        UpdateReason updateReason = new PlayerUpdateReason.Click(player, click);
        int amount = inventory.collectSimilar(updateReason, template);
        
        // put collected items on cursor
        template.setAmount(amount);
        player.setItemOnCursor(template);
    }
    
    @Override
    public void open() {
        Player viewer = getViewer();
        if (!viewer.isValid())
            throw new IllegalStateException("Viewer is not valid");
        if (isOpen)
            throw new IllegalStateException("Window is already open");
        if (isInOpeningContext.get())
            throw new IllegalStateException("Opening a window is not allowed to trigger opening another window.");
        if (isInCloseHandlerContext.get() > 0)
            throw new IllegalStateException("Opening a window is not allowed while handling window close. Consider setting a fallback window or scheduling a task instead.");
        
        try {
            isInOpeningContext.set(true);
            
            isOpen = true;
            
            // close old window and move cursor to new window
            AbstractWindow<?> oldWindow = (AbstractWindow<?>) WindowManager.getInstance().getOpenWindow(viewer);
            assert oldWindow != this;
            if (oldWindow != null) {
                ItemStack cursor = oldWindow.menu.getCursor();
                oldWindow.menu.setCursor(null);
                menu.setCursor(cursor);
                
                oldWindow.handleClose(InventoryCloseEvent.Reason.OPEN_NEW);
            }
            
            // track window and elements
            WindowManager.getInstance().addWindow(this);
            registerAsViewer();
            
            // init items
            for (int i = 0; i < size; i++) {
                update(i);
            }
            
            // open menu
            var title = getTitle();
            activeTitle = title;
            menu.open(Languages.getInstance().localized(viewer, title));
            
            // open handlers
            forEachCatching(openHandlers, Runnable::run, "Failed to handle window open");
        } finally {
            isInOpeningContext.set(false);
        }
    }
    
    protected void registerAsViewer() {
        for (int i = 0; i < size; i++) {
            SlotElement element = elementsDisplayed[i];
            switch (element) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).addViewer(this, i);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().addViewer(this, invElement.slot(), i);
                case null, default -> {}
            }
            
            var pair = getGuiAt(i);
            if (pair != null)
                pair.first().addViewer(this, pair.second(), i);
        }
    }
    
    @Override
    public void close() {
        if (isOpen()) {
            viewer.closeInventory(); // WindowManager then calls handleClose
        }
    }
    
    public void handleClose(InventoryCloseEvent.Reason cause) {
        // might have already been called by close() or open() if the window was replaced by another one
        if (!isOpen)
            return;
        
        WindowManager.getInstance().removeWindow(this);
        unregisterAsViewer();
        menu.handleClosed();
        isOpen = false;
        
        ItemStack cursor = menu.getCursor();
        menu.setCursor(null);
        if (cause == InventoryCloseEvent.Reason.PLAYER && fallbackWindow.get() instanceof AbstractWindow<?> fallback) {
            fallback.menu.setCursor(cursor);
            fallback.open();
        } else {
            InventoryUtils.addToInventoryOrDrop(viewer, cursor);
        }
        
        try {
            isInCloseHandlerContext.set(isInCloseHandlerContext.get() + 1);
            forEachCatching(closeHandlers, handler -> handler.accept(cause), "Failed to handle window close");
        } finally {
            isInCloseHandlerContext.set(isInCloseHandlerContext.get() - 1);
        }
    }
    
    protected void unregisterAsViewer() {
        for (int i = 0; i < size; i++) {
            SlotElement element = elementsDisplayed[i];
            switch (element) {
                case SlotElement.Item itemElement -> ((AbstractItem) itemElement.item()).removeViewer(this, i);
                case SlotElement.InventoryLink invElement ->
                    invElement.inventory().removeViewer(this, invElement.slot(), i);
                case null, default -> {}
            }
            
            var pair = getGuiAt(i);
            if (pair != null)
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
        if (!isOpen())
            return;
        
        var title = getTitle();
        if (title.equals(activeTitle))
            return;
        activeTitle = title;
        
        menu.sendOpenPacket(Languages.getInstance().localized(viewer, title));
    }
    
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int i) {
        if (i < 0)
            return null;
        
        int off = 0;
        for (Gui gui : getGuis()) {
            int size = gui.getSize();
            if (i < off + size)
                return new Pair<>((AbstractGui) gui, i - off);
            
            off += size;
        }
        
        return null;
    }
    
    public abstract @Nullable Pair<AbstractGui, Integer> getGuiAtHotbar(int i);
    
    @Override
    public void setOpenHandlers(@Nullable List<? extends Runnable> openHandlers) {
        this.openHandlers.clear();
        if (openHandlers != null)
            this.openHandlers.addAll(openHandlers);
    }
    
    @Override
    public void addOpenHandler(Runnable openHandler) {
        openHandlers.add(openHandler);
    }
    
    @Override
    public void setCloseHandlers(@Nullable List<? extends Consumer<? super InventoryCloseEvent.Reason>> closeHandlers) {
        this.closeHandlers.clear();
        if (closeHandlers != null)
            this.closeHandlers.addAll(closeHandlers);
    }
    
    @Override
    public void addCloseHandler(Consumer<? super InventoryCloseEvent.Reason> closeHandler) {
        closeHandlers.add(closeHandler);
    }
    
    @Override
    public void removeCloseHandler(Consumer<? super InventoryCloseEvent.Reason> closeHandler) {
        closeHandlers.remove(closeHandler);
    }
    
    @Override
    public void setFallbackWindow(Supplier<? extends @Nullable Window> fallbackWindow) {
        this.fallbackWindow = fallbackWindow;
    }
    
    @Override
    public void setOutsideClickHandlers(@Nullable List<? extends Consumer<? super ClickEvent>> outsideClickHandlers) {
        this.outsideClickHandlers.clear();
        if (outsideClickHandlers != null)
            this.outsideClickHandlers.addAll(outsideClickHandlers);
    }
    
    @Override
    public void addOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler) {
        outsideClickHandlers.add(outsideClickHandler);
    }
    
    @Override
    public void removeOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler) {
        outsideClickHandlers.remove(outsideClickHandler);
    }
    
    @Override
    public Player getViewer() {
        return viewer;
    }
    
    public Locale getLocale() {
        return Languages.getInstance().getLocale(getViewer());
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
        return isOpen;
    }
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<W extends Window, S extends Window.Builder<W, S>>
        implements Window.Builder<W, S>
        permits AbstractMergedWindow.AbstractBuilder, AbstractSplitWindow.AbstractBuilder
    {
        
        private @Nullable Player viewer;
        protected Supplier<Component> titleSupplier = Component::empty;
        protected boolean closeable = true;
        private List<Runnable> openHandlers = new ArrayList<>(0);
        private List<Consumer<? super InventoryCloseEvent.Reason>> closeHandlers = new ArrayList<>(0);
        private List<Consumer<? super ClickEvent>> outsideClickHandlers = new ArrayList<>(0);
        private List<Consumer<? super W>> modifiers = new ArrayList<>(0);
        private Supplier<? extends @Nullable Window> fallbackWindow = () -> null;
        
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
        public S setOpenHandlers(@Nullable List<? extends Runnable> openHandlers) {
            this.openHandlers.clear();
            if (openHandlers != null)
                this.openHandlers.addAll(openHandlers);
            return (S) this;
        }
        
        @Override
        public S addOpenHandler(Runnable openHandler) {
            openHandlers.add(openHandler);
            return (S) this;
        }
        
        @Override
        public S setFallbackWindow(Supplier<? extends @Nullable Window> fallbackWindow) {
            this.fallbackWindow = fallbackWindow;
            return (S) this;
        }
        
        @Override
        public S setCloseHandlers(@Nullable List<? extends Consumer<? super InventoryCloseEvent.Reason>> closeHandlers) {
            this.closeHandlers.clear();
            if (closeHandlers != null)
                this.closeHandlers.addAll(closeHandlers);
            return (S) this;
        }
        
        @Override
        public S addCloseHandler(Consumer<? super InventoryCloseEvent.Reason> closeHandler) {
            closeHandlers.add(closeHandler);
            return (S) this;
        }
        
        @Override
        public S setOutsideClickHandlers(@Nullable List<? extends Consumer<? super ClickEvent>> outsideClickHandlers) {
            this.outsideClickHandlers.clear();
            if (outsideClickHandlers != null)
                this.outsideClickHandlers.addAll(outsideClickHandlers);
            return (S) this;
        }
        
        @Override
        public S addOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler) {
            outsideClickHandlers.add(outsideClickHandler);
            return (S) this;
        }
        
        @Override
        public S setModifiers(@Nullable List<? extends Consumer<? super W>> modifiers) {
            this.modifiers.clear();
            if (modifiers != null)
                this.modifiers.addAll(modifiers);
            return (S) this;
        }
        
        @Override
        public S addModifier(Consumer<? super W> modifier) {
            modifiers.add(modifier);
            return (S) this;
        }
        
        protected void applyModifiers(W window) {
            window.setOpenHandlers(openHandlers);
            window.setCloseHandlers(closeHandlers);
            window.setOutsideClickHandlers(outsideClickHandlers);
            window.setFallbackWindow(fallbackWindow);
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
                clone.openHandlers = new ArrayList<>(openHandlers);
                clone.closeHandlers = new ArrayList<>(closeHandlers);
                clone.outsideClickHandlers = new ArrayList<>(outsideClickHandlers);
                clone.modifiers = new ArrayList<>(modifiers);
                return (S) clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
        
    }
    
}