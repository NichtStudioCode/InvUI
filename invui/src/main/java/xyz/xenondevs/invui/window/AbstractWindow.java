package xyz.xenondevs.invui.window;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.ClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.menu.CustomContainerMenu;
import xyz.xenondevs.invui.internal.menu.WindowEventListener;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.inventory.InventorySlot;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.util.ItemUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static xyz.xenondevs.invui.internal.util.CollectionUtils.forEachCatching;

non-sealed abstract class AbstractWindow<M extends CustomContainerMenu> implements Window, WindowEventListener {
    
    private static final ThreadLocal<Boolean> isInOpeningContext = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> isInCloseHandlerContext = ThreadLocal.withInitial(() -> 0);
    
    private static final @Nullable Window DEFAULT_FALLBACK_WINDOW = null;
    private static final Component DEFAULT_TITLE = Component.empty();
    private static final boolean DEFAULT_CLOSEABLE = true;
    private static final int DEFAULT_WINDOW_STATE = 0;
    
    protected final M menu;
    private final Player viewer;
    private final List<Runnable> openHandlers = new ArrayList<>(0);
    private final List<Consumer<? super Reason>> closeHandlers = new ArrayList<>(0);
    private final List<Consumer<? super ClickEvent>> outsideClickHandlers = new ArrayList<>(0);
    private final List<Consumer<? super Integer>> windowStateChangeHandlers = new ArrayList<>(0);
    private Supplier<? extends @Nullable Window> fallbackWindow = () -> null;
    private Supplier<? extends Component> titleSupplier;
    private final MutableProperty<Boolean> closeable;
    private final MutableProperty<Integer> serverWindowState;
    private boolean isOpen;
    private int clientWindowState;
    
    private final int size;
    private final List<List<SlotElement>> elementsDisplayed;
    private final BitSet dirtySlots;
    
    private @Nullable Component activeTitle;
    
    AbstractWindow(
        Player viewer,
        Supplier<? extends Component> titleSupplier,
        int size,
        M menu,
        MutableProperty<Boolean> closeable,
        MutableProperty<Integer> windowState
    ) {
        this.menu = menu;
        this.viewer = viewer;
        this.titleSupplier = titleSupplier;
        this.closeable = closeable;
        this.serverWindowState = windowState;
        this.size = size;
        this.dirtySlots = new BitSet(size);
        this.elementsDisplayed = IntStream.range(0, size)
            .<List<SlotElement>>mapToObj(i -> new ArrayList<>())
            .collect(Collectors.toCollection(ArrayList::new));
        
        serverWindowState.observeWeak(this, thisRef -> {
            thisRef.handleTick(); // important: flush item updates and send packets
            thisRef.menu.sendPing(serverWindowState.get());
        });
        
        menu.setWindow(this);
    }
    
    protected void update(int slot) {
        SlotElement.GuiLink root = getGuiAt(slot);
        if (root == null)
            return;
        
        List<SlotElement> newPath = root.traverse();
        List<SlotElement> oldPath = elementsDisplayed.get(slot);
        
        // if path has changed, viewers need to be updated
        if (!newPath.equals(oldPath)) {
            unregisterAsViewer(slot, oldPath);
            registerAsViewer(slot, newPath);
            elementsDisplayed.set(slot, newPath);
        }
        
        // create and place item stack in inventory
        ItemStack itemStack;
        SlotElement lastElement = newPath.getLast();
        if (!(lastElement instanceof SlotElement.GuiLink)) {
            itemStack = lastElement.getItemStack(getViewer());
        } else { // there is no holding element
            // background by gui
            itemStack = newPath.reversed().stream()
                .filter(e -> e instanceof SlotElement.GuiLink)
                .map(e -> ((SlotElement.GuiLink) e).gui().getBackground())
                .filter(Objects::nonNull)
                .findFirst()
                .map(background -> background.get(getLocale()))
                .orElse(null);
        }
        
        setMenuItem(slot, itemStack);
    }
    
    /**
     * Registers this window as a viewer using slot from all elements in the path.
     *
     * @param slot The slot to register as viewer
     * @param path The elements where the window should be registered as viewer
     */
    private void registerAsViewer(int slot, List<? extends SlotElement> path) {
        for (SlotElement newElement : path) {
            newElement.addObserver(this, slot);
        }
    }
    
    /**
     * Unregisters this window as a viewer using slot from all elements in the path.
     *
     * @param slot The slot to unregister as viewer
     * @param path The elements where the window should be unregistered as viewer
     */
    private void unregisterAsViewer(int slot, List<? extends SlotElement> path) {
        for (SlotElement oldElement : path) {
            oldElement.removeObserver(this, slot);
        }
    }
    
    /**
     * Registers this window as a viewer for all elements.
     */
    protected void registerAsViewer() {
        // individual slot element viewers are registered through item init
    }
    
    /**
     * Unregisters this window as viewer from all elements and clears elementsDisplayed accordingly.
     */
    protected void unregisterAsViewer() {
        for (int i = 0; i < size; i++) {
            unregisterAsViewer(i, elementsDisplayed.get(i));
            elementsDisplayed.set(i, List.of());
        }
    }
    
    protected void setMenuItem(int slot, @Nullable ItemStack itemStack) {
        menu.setItem(slot, itemStack);
    }
    
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
    
    @Override
    public void handleClick(int slot, Click click) {
        if (slot != -999) { // inside
            var link = getGuiAt(slot);
            if (link != null) {
                link.gui().handleClick(link.slot(), click);
            }
        } else { // outside
            var event = new ClickEvent(click);
            CollectionUtils.forEachCatching(
                outsideClickHandlers,
                handler -> handler.accept(event),
                "Failed to handle outside click: " + click
            );
            
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
    
    @Override
    public void handleBundleSelect(int slot, int bundleSlot) {
        var link = getGuiAt(slot);
        if (link == null)
            return;
        link.gui().handleBundleSelect(link.slot(), getViewer(), bundleSlot);
    }
    
    @Override
    public void handleDrag(IntSet slots, ClickType mode) {
        if (mode == ClickType.MIDDLE && viewer.getGameMode() != GameMode.CREATIVE)
            return;
        
        List<InventorySlot> invSlots = getActiveInventorySlots(slots);
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
    
    /**
     * Gets a list of all non-frozen inventory slots that are linked to by
     * the specified window slots.
     *
     * @param slots The window slots
     * @return A list of all active inventory slots referenced by the window slots
     */
    private List<InventorySlot> getActiveInventorySlots(IntSet slots) {
        List<InventorySlot> invSlots = new ArrayList<>();
        
        slotLoop:
        for (int slot : slots) {
            List<SlotElement> path = elementsDisplayed.get(slot);
            if (path.isEmpty())
                continue;
            
            // skip frozen slots
            for (SlotElement element : path) {
                if (element instanceof SlotElement.GuiLink guiLink) {
                    if (guiLink.gui().isFrozen())
                        continue slotLoop;
                }
            }
            
            SlotElement bottom = path.getLast();
            if (bottom instanceof SlotElement.InventoryLink link) {
                invSlots.add(new InventorySlot(link.inventory(), link.slot()));
            }
        }
        
        return invSlots;
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
                
                oldWindow.handleClose(Reason.OPEN_NEW);
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
    
    @Override
    public void close() {
        if (isOpen()) {
            viewer.closeInventory(); // WindowManager then calls handleClose
        }
    }
    
    @Override
    public void handleClose(Reason cause) {
        // might have already been called by close() or open() if the window was replaced by another one
        if (!isOpen)
            return;
        
        WindowManager.getInstance().removeWindow(this);
        unregisterAsViewer();
        menu.handleClosed();
        isOpen = false;
        
        ItemStack cursor = menu.getCursor();
        menu.setCursor(null);
        if (cause == Reason.PLAYER && FuncUtils.getSafely(fallbackWindow, DEFAULT_FALLBACK_WINDOW) instanceof AbstractWindow<?> fallback) {
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
    
    @Override
    public void setTitleSupplier(Supplier<? extends Component> titleSupplier) {
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
        return FuncUtils.getSafely(titleSupplier, DEFAULT_TITLE);
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
    
    @Override
    public SlotElement.@Nullable GuiLink getGuiAt(int i) {
        if (i < 0)
            return null;
        
        int off = 0;
        for (Gui gui : getGuis()) {
            int size = gui.getSize();
            if (i < off + size)
                return new SlotElement.GuiLink(gui, i - off);
            
            off += size;
        }
        
        return null;
    }
    
    public abstract SlotElement.@Nullable GuiLink getGuiAtHotbar(int i);
    
    @Override
    public void setOpenHandlers(List<? extends Runnable> openHandlers) {
        this.openHandlers.clear();
        this.openHandlers.addAll(openHandlers);
    }
    
    @Override
    public @UnmodifiableView List<Runnable> getOpenHandlers() {
        return Collections.unmodifiableList(openHandlers);
    }
    
    @Override
    public void addOpenHandler(Runnable openHandler) {
        openHandlers.add(openHandler);
    }
    
    @Override
    public void removeOpenHandler(Runnable openHandler) {
        openHandlers.remove(openHandler);
    }
    
    @Override
    public void setCloseHandlers(List<? extends Consumer<Reason>> closeHandlers) {
        this.closeHandlers.clear();
        this.closeHandlers.addAll(closeHandlers);
    }
    
    @Override
    public @UnmodifiableView List<Consumer<Reason>> getCloseHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(closeHandlers);
    }
    
    @Override
    public void addCloseHandler(Consumer<? super Reason> closeHandler) {
        closeHandlers.add(closeHandler);
    }
    
    @Override
    public void removeCloseHandler(Consumer<? super Reason> closeHandler) {
        closeHandlers.remove(closeHandler);
    }
    
    @Override
    public void setFallbackWindow(Supplier<? extends @Nullable Window> fallbackWindow) {
        this.fallbackWindow = fallbackWindow;
    }
    
    @Override
    public void setOutsideClickHandlers(List<? extends Consumer<ClickEvent>> outsideClickHandlers) {
        this.outsideClickHandlers.clear();
        this.outsideClickHandlers.addAll(outsideClickHandlers);
    }
    
    @Override
    public @UnmodifiableView List<Consumer<ClickEvent>> getOutsideClickHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(outsideClickHandlers);
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
    public void setWindowStateChangeHandlers(List<? extends Consumer<Integer>> handlers) {
        this.windowStateChangeHandlers.clear();
        this.windowStateChangeHandlers.addAll(handlers);
    }
    
    @Override
    public @UnmodifiableView List<Consumer<Integer>> getWindowStateChangeHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(windowStateChangeHandlers);
    }
    
    @Override
    public void addWindowStateChangeHandler(Consumer<? super Integer> handler) {
        windowStateChangeHandlers.add(handler);
    }
    
    @Override
    public void removeWindowStateChangeHandler(Consumer<? super Integer> handler) {
        windowStateChangeHandlers.remove(handler);
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
        return FuncUtils.getSafely(closeable, DEFAULT_CLOSEABLE);
    }
    
    @Override
    public void setCloseable(boolean closeable) {
        this.closeable.set(closeable);
    }
    
    @Override
    public void setWindowState(int windowState) {
        serverWindowState.set(windowState);
    }
    
    @Override
    public int getServerWindowState() {
        return FuncUtils.getSafely(serverWindowState, DEFAULT_WINDOW_STATE);
    }
    
    @Override
    public int getClientWindowState() {
        return clientWindowState;
    }
    
    @Override
    public void handlePong(int id) {
        clientWindowState = id;
        forEachCatching(windowStateChangeHandlers, handler -> handler.accept(id), "Failed to handle window state change");
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
        protected Supplier<? extends Component> titleSupplier = () -> DEFAULT_TITLE;
        protected MutableProperty<Boolean> closeable = MutableProperty.of(DEFAULT_CLOSEABLE);
        protected MutableProperty<Integer> windowState = MutableProperty.of(DEFAULT_WINDOW_STATE);
        private List<Runnable> openHandlers = new ArrayList<>(0);
        private List<Consumer<? super Reason>> closeHandlers = new ArrayList<>(0);
        private List<Consumer<? super ClickEvent>> outsideClickHandlers = new ArrayList<>(0);
        private List<Consumer<? super Integer>> windowStateChangeHandlers = new ArrayList<>(0);
        private List<Consumer<? super W>> modifiers = new ArrayList<>(0);
        private Supplier<? extends @Nullable Window> fallbackWindow = () -> DEFAULT_FALLBACK_WINDOW;
        
        @Override
        public S setViewer(Player viewer) {
            this.viewer = viewer;
            return (S) this;
        }
        
        @Override
        public S setTitleSupplier(Supplier<? extends Component> title) {
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
        public S setCloseable(MutableProperty<Boolean> closeable) {
            this.closeable = closeable;
            return (S) this;
        }
        
        @Override
        public S setOpenHandlers(List<? extends Runnable> openHandlers) {
            this.openHandlers.clear();
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
        public S setCloseHandlers(List<? extends Consumer<? super Reason>> closeHandlers) {
            this.closeHandlers.clear();
            this.closeHandlers.addAll(closeHandlers);
            return (S) this;
        }
        
        @Override
        public S addCloseHandler(Consumer<? super Reason> closeHandler) {
            closeHandlers.add(closeHandler);
            return (S) this;
        }
        
        @Override
        public S setOutsideClickHandlers(List<? extends Consumer<? super ClickEvent>> outsideClickHandlers) {
            this.outsideClickHandlers.clear();
            this.outsideClickHandlers.addAll(outsideClickHandlers);
            return (S) this;
        }
        
        @Override
        public S addOutsideClickHandler(Consumer<? super ClickEvent> outsideClickHandler) {
            outsideClickHandlers.add(outsideClickHandler);
            return (S) this;
        }
        
        @Override
        public S setWindowState(MutableProperty<Integer> windowState) {
            this.windowState = windowState;
            return (S) this;
        }
        
        @Override
        public S setWindowStateChangeHandlers(List<? extends Consumer<? super Integer>> handlers) {
            this.windowStateChangeHandlers.clear();
            this.windowStateChangeHandlers.addAll(handlers);
            return (S) this;
        }
        
        @Override
        public S addWindowStateChangeHandler(Consumer<? super Integer> handler) {
            windowStateChangeHandlers.add(handler);
            return (S) this;
        }
        
        @Override
        public S setModifiers(List<? extends Consumer<? super W>> modifiers) {
            this.modifiers.clear();
            this.modifiers.addAll(modifiers);
            return (S) this;
        }
        
        @Override
        public S addModifier(Consumer<? super W> modifier) {
            modifiers.add(modifier);
            return (S) this;
        }
        
        @SuppressWarnings("rawtypes")
        protected void applyModifiers(W window) {
            window.setOpenHandlers(openHandlers);
            window.setCloseHandlers((List) closeHandlers);
            window.setOutsideClickHandlers((List) outsideClickHandlers);
            window.setWindowStateChangeHandlers((List) windowStateChangeHandlers);
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
                clone.windowStateChangeHandlers = new ArrayList<>(windowStateChangeHandlers);
                clone.modifiers = new ArrayList<>(modifiers);
                return (S) clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
        
    }
    
}