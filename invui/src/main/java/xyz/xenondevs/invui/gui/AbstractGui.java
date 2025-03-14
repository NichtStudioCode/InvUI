package xyz.xenondevs.invui.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.internal.ViewerAtSlot;
import xyz.xenondevs.invui.internal.util.*;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ObscuredInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.util.ItemUtils;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.WindowManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @hidden
 */
@Internal
public sealed abstract class AbstractGui
    implements Gui
    permits NormalGuiImpl, AbstractPagedGui, AbstractScrollGui, TabGuiImpl
{
    
    private final int width;
    private final int height;
    private final int size;
    private final @Nullable SlotElement[] slotElements;
    private final @Nullable Set<ViewerAtSlot>[] viewers;
    
    private boolean frozen;
    private boolean ignoreObscuredInventorySlots = true;
    private @Nullable ItemProvider background;
    private @Nullable AnimationImpl animation;
    private @Nullable SlotElement @Nullable [] animationElements;
    private @Nullable IngredientMatrix ingredientMatrix;
    
    @SuppressWarnings("unchecked")
    AbstractGui(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        slotElements = new SlotElement[size];
        viewers = new Set[size];
    }
    
    public void handleClick(int slot, Click click) {
        // ignore all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null)
            return;
        
        SlotElement slotElement = slotElements[slot];
        switch (slotElement) {
            case SlotElement.GuiLink le -> ((AbstractGui) le.gui()).handleClick(le.slot(), click);
            case SlotElement.Item ie -> ie.item().handleClick(click.clickType(), click.player(), click);
            case SlotElement.InventoryLink ie -> handleInvSlotElementClick(ie, click);
            case null -> {}
        }
    }
    
    public void handleBundleSelect(Player player, int slot, int bundleSlot) {
        // ignore all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null)
            return;
        
        SlotElement slotElement = slotElements[slot];
        switch (slotElement) {
            case SlotElement.GuiLink le -> ((AbstractGui) le.gui()).handleBundleSelect(player, le.slot(), bundleSlot);
            case SlotElement.Item ie -> ie.item().handleBundleSelect(player, bundleSlot);
            case SlotElement.InventoryLink ie -> handleInvBundleSelect(player, ie.inventory(), ie.slot(), bundleSlot);
            case null -> {}
        }
    }
    
    //<editor-fold desc="inventories">
    private void handleInvSlotElementClick(SlotElement.InventoryLink element, Click click) {
        Inventory inventory = element.inventory();
        int slot = element.slot();
        
        if (inventory.callClickEvent(slot, click))
            return;
        
        switch (click.clickType()) {
            case LEFT -> handleInvLeftClick(click, inventory, slot);
            case RIGHT -> handleInvRightClick(click, inventory, slot);
            case SHIFT_LEFT, SHIFT_RIGHT -> handleInvItemShift(click, inventory, slot);
            case NUMBER_KEY -> handleInvNumberKey(click, inventory, slot);
            case SWAP_OFFHAND -> handleInvOffHandKey(click, inventory, slot);
            case DROP -> handleInvDrop(false, click, inventory, slot);
            case CONTROL_DROP -> handleInvDrop(true, click, inventory, slot);
            case DOUBLE_CLICK -> handleInvDoubleClick(click);
            case MIDDLE -> handleInvMiddleClick(click, inventory, slot);
        }
    }
    
    private void handleInvLeftClick(Click click, Inventory inventory, int slot) {
        Player player = click.player();
        ItemStack cursor = ItemUtils.takeUnlessEmpty(player.getItemOnCursor());
        ItemStack clicked = inventory.getItem(slot);
        
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason.Click(player, click);
        
        if (cursor == null) {
            // if the cursor is empty, pick the stack up
            if (inventory.setItem(updateReason, slot, null))
                player.setItemOnCursor(clicked);
        } else if (clicked != null && ItemUtils2.isBundle(cursor)) {
            // insert clicked item into bundle on cursor
            // TODO: react to events, don't set if nothing changed
            ItemUtils2.addToBundle(cursor, clicked);
            inventory.setItem(updateReason, slot, clicked);
            player.setItemOnCursor(cursor);
        } else if (clicked != null && ItemUtils2.isBundle(clicked)) {
            // insert cursor item into clicked bundle
            // TODO: react to events, don't set if nothing changed
            ItemUtils2.addToBundle(clicked, cursor);
            inventory.setItem(updateReason, slot, clicked);
            player.setItemOnCursor(cursor);
        } else if (clicked == null || cursor.isSimilar(clicked)) {
            // if there are no items, or they're similar to the cursor, add the cursor items to the stack
            int remains = inventory.putItem(updateReason, slot, cursor);
            if (remains == 0) {
                player.setItemOnCursor(null);
            } else {
                cursor.setAmount(remains);
                player.setItemOnCursor(cursor);
            }
        } else if (!cursor.isSimilar(clicked)) {
            // if the stacks are not similar, swap them
            if (inventory.setItem(updateReason, slot, cursor))
                player.setItemOnCursor(clicked);
        }
    }
    
    private void handleInvRightClick(Click click, Inventory inventory, int slot) {
        Player player = click.player();
        ItemStack cursor = ItemUtils.takeUnlessEmpty(player.getItemOnCursor());
        ItemStack clicked = inventory.getItem(slot);
        
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason.Click(player, click);
        
        if (cursor == null && ItemUtils2.isBundle(clicked)) {
            // take the selected item from the bundle
            // TODO: react to events
            var taken = ItemUtils2.takeSelectedFromBundle(clicked);
            inventory.setItem(updateReason, slot, clicked);
            player.setItemOnCursor(taken);
        } else if (cursor == null) {
            // if the cursor is empty, split the stack to the cursor
            // if the stack is not divisible by 2, give the cursor the bigger part
            int clickedAmount = clicked.getAmount();
            int newClickedAmount = clickedAmount / 2;
            int newCursorAmount = clickedAmount - newClickedAmount;
            
            cursor = clicked.clone();
            
            clicked.setAmount(newClickedAmount);
            cursor.setAmount(newCursorAmount);
            
            if (inventory.setItem(updateReason, slot, clicked))
                player.setItemOnCursor(cursor);
        } else if (clicked == null && ItemUtils2.isBundle(cursor)) {
            // if the player right-clicked on an empty slot with a bundle, place the first item from the bundle there
            // TODO: react to events, don't set if nothing changed (i.e. taken is null)
            var taken = ItemUtils2.takeFirstFromBundle(cursor);
            inventory.setItem(updateReason, slot, taken);
            player.setItemOnCursor(cursor);
        } else if (clicked == null || cursor.isSimilar(clicked)) {
            // put one item from the cursor in the inventory
            ItemStack toAdd = cursor.clone();
            toAdd.setAmount(1);
            int remains = inventory.putItem(updateReason, slot, toAdd);
            if (remains == 0) {
                cursor.setAmount(cursor.getAmount() - 1);
                player.setItemOnCursor(cursor);
            }
        } else {
            // swap cursor and clicked
            if (inventory.setItem(updateReason, slot, cursor))
                player.setItemOnCursor(clicked);
        }
    }
    
    private void handleInvItemShift(Click click, Inventory inventory, int slot) {
        Player player = click.player();
        ItemStack clicked = inventory.getItem(slot);
        
        if (clicked == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason.Click(player, click);
        ItemPreUpdateEvent updateEvent = inventory.callPreUpdateEvent(updateReason, slot, clicked, null);
        if (updateEvent.isCancelled())
            return;
        
        var window = WindowManager.getInstance().getOpenWindow(player);
        assert window != null;
        
        // move into the first inventory that accepts the item, sorted by priority
        var inventories = window.getGuis().stream()
            .flatMap(gui -> gui.getInventories(inventory).stream())
            .sorted(Comparator.comparingInt(Inventory::getGuiPriority).reversed())
            .toList();
        
        int leftOverAmount = putIntoFirstInventory(updateReason, clicked, inventories);
        
        ItemStack newStack = clicked.clone();
        newStack.setAmount(leftOverAmount);
        
        inventory.forceSetItem(UpdateReason.SUPPRESSED, slot, newStack);
        inventory.callPostUpdateEvent(updateReason, slot, clicked, newStack);
    }
    
    private void handleInvNumberKey(Click click, Inventory inventory, int slot) {
        Player player = click.player();
        ItemStack clicked = inventory.getItem(slot);
        
        AbstractWindow<?> window = (AbstractWindow<?>) WindowManager.getInstance().getOpenWindow(player);
        assert window != null;
        
        SlotElement.GuiLink link = window.getGuiAtHotbar(click.hotbarButton());
        if (link == null)
            return;
        SlotElement hotbarElement = link.gui().getSlotElement(link.slot());
        if (hotbarElement == null)
            return;
        hotbarElement = hotbarElement.getHoldingElement();
        
        if (hotbarElement instanceof SlotElement.InventoryLink(var otherInventory, var otherSlot, var unused)) {
            if (inventory == otherInventory && slot == otherSlot)
                return;
            
            ItemStack hotbar = otherInventory.getItem(otherSlot);
            var updateReason = new PlayerUpdateReason.Click(click);
            
            // check if clicked inventory would allow hotbar swap
            ItemPreUpdateEvent updateEvent = inventory.callPreUpdateEvent(updateReason, slot, clicked, hotbar);
            if (updateEvent.isCancelled())
                return;
            
            // move clicked into hotbar, or abort if cancelled
            if (!otherInventory.setItem(updateReason, otherSlot, clicked))
                return;
            
            // move hotbar into clicked without firing pre update event
            inventory.forceSetItem(UpdateReason.SUPPRESSED, slot, hotbar);
            inventory.callPostUpdateEvent(updateReason, slot, clicked, hotbar);
        }
    }
    
    private void handleInvOffHandKey(Click click, Inventory inventory, int slot) {
        Player player = click.player();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack clicked = inventory.getItem(slot);
        ItemStack offhandItem = ItemUtils.takeUnlessEmpty(playerInventory.getItemInOffHand());
        
        if (inventory.setItem(new PlayerUpdateReason.Click(click), slot, offhandItem))
            playerInventory.setItemInOffHand(clicked);
    }
    
    private void handleInvDrop(boolean ctrl, Click click, Inventory inventory, int slot) {
        Player player = click.player();
        ItemStack clicked = inventory.getItem(slot);
        
        if (clicked == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason.Click(player, click);
        
        if (ctrl) {
            if (inventory.setItem(updateReason, slot, null)) {
                InventoryUtils.dropItemLikePlayer(player, clicked);
            }
        } else if (inventory.addItemAmount(updateReason, slot, -1) == -1) {
            clicked.setAmount(1);
            InventoryUtils.dropItemLikePlayer(player, clicked);
        }
    }
    
    private void handleInvDoubleClick(Click click) {
        Player player = click.player();
        if (ItemUtils.isEmpty(player.getItemOnCursor()))
            return;
        
        // windows handle cursor collect because it is a cross-inventory / cross-gui operation
        Window window = WindowManager.getInstance().getOpenWindow(player);
        assert window != null;
        ((AbstractWindow<?>) window).handleCursorCollect(click);
    }
    
    private void handleInvMiddleClick(Click click, Inventory inventory, int slot) {
        Player player = click.player();
        if (player.getGameMode() != GameMode.CREATIVE || !player.getItemOnCursor().isEmpty())
            return;
        
        ItemStack cursor = inventory.getItem(slot);
        if (cursor != null) {
            cursor.setAmount(cursor.getMaxStackSize());
            player.setItemOnCursor(cursor);
        }
    }
    
    @SuppressWarnings("UnstableApiUsage")
    private void handleInvBundleSelect(Player player, Inventory inventory, int slot, int bundleSlot) {
        var bundle = inventory.getItem(slot);
        if (bundle != null && bundle.hasData(DataComponentTypes.BUNDLE_CONTENTS)) {
            ItemUtils2.setSelectedBundleSlot(bundle, bundleSlot);
            inventory.setItem(new PlayerUpdateReason.BundleSelect(player, bundleSlot), slot, bundle);
        }
    }
    
    /**
     * Puts the given {@link ItemStack} into the first inventory that accepts it, starting with the
     * {@link Inventory#getGuiPriority() highest priority} inventory. If one inventory accepts any amount
     * of items, further inventories will not be queried, meaning that an item stack will not be split
     * across multiple inventories.
     *
     * @param updateReason the update reason to use
     * @param itemStack    the item stack to put
     * @param ignored      the inventories to ignore
     * @return the amount of items that are left over
     */
    protected int putIntoFirstInventory(UpdateReason updateReason, ItemStack itemStack, Inventory... ignored) {
        return putIntoFirstInventory(updateReason, itemStack, getInventories(ignored));
    }
    
    /**
     * Puts the given {@link ItemStack} into the first inventory that accepts it of the given collection of inventories.
     * If one inventory accepts any amount of items, further inventories will not be queried, meaning that an item stack
     * will not be split across multiple inventories.
     *
     * @param updateReason the update reason to use
     * @param itemStack    the item stack to put
     * @param inventories  the inventories to put the item stack into
     * @return the amount of items that are left over
     */
    protected int putIntoFirstInventory(UpdateReason updateReason, ItemStack itemStack, SequencedCollection<? extends Inventory> inventories) {
        int originalAmount = itemStack.getAmount();
        for (Inventory inventory : inventories) {
            int amountLeft = inventory.addItem(updateReason, itemStack);
            if (originalAmount != amountLeft)
                return amountLeft;
        }
        
        return originalAmount;
    }
    
    /**
     * Gets a map of all inventories and their visible slots in this gui, ignoring the specified inventories,
     * sorted by their {@link Inventory#getGuiPriority()}, with the highest priorities coming first.
     *
     * @param ignored the inventories to ignore
     * @return a map of all inventories and their visible slots
     */
    private SequencedMap<Inventory, Set<Integer>> getAllInventorySlots(Inventory... ignored) {
        HashMap<Inventory, Set<Integer>> slots = new HashMap<>();
        Set<Inventory> ignoredSet = Arrays.stream(ignored).collect(Collectors.toSet());
        
        for (SlotElement element : slotElements) {
            if (element == null)
                continue;
            
            element = element.getHoldingElement();
            if (element instanceof SlotElement.InventoryLink invElement) {
                Inventory inventory = invElement.inventory();
                if (ignoredSet.contains(inventory))
                    continue;
                
                slots.computeIfAbsent(inventory, i -> new HashSet<>()).add(invElement.slot());
            }
        }
        
        return slots.entrySet().stream()
            .sorted(Comparator.<Map.Entry<Inventory, Set<Integer>>>comparingInt(entry -> entry.getKey().getGuiPriority()).reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
    
    @Override
    public SequencedCollection<? extends Inventory> getInventories(Inventory... ignored) {
        if (!ignoreObscuredInventorySlots)
            return Collections.unmodifiableSequencedCollection(getAllInventorySlots(ignored).sequencedKeySet());
        
        ArrayList<Inventory> inventories = new ArrayList<>();
        for (Map.Entry<Inventory, Set<Integer>> entry : getAllInventorySlots(ignored).entrySet()) {
            Inventory inventory = entry.getKey();
            Set<Integer> slots = entry.getValue();
            inventories.add(new ObscuredInventory(inventory, slot -> !slots.contains(slot)));
        }
        
        return Collections.unmodifiableList(inventories);
    }
    //</editor-fold>
    
    @Override
    public void notifyWindows() {
        synchronized (viewers) {
            for (var viewerSet : viewers) {
                if (viewerSet != null) {
                    for (var viewerAtSlot : viewerSet) {
                        viewerAtSlot.notifyUpdate();
                    }
                }
            }
        }
    }
    
    @Override
    public void notifyWindows(int index) {
        var element = getSlotElement(index);
        if (element == null)
            return;
        
        synchronized (viewers) {
            var viewerSet = viewers[index];
            if (viewerSet == null)
                return;
            
            for (var viewerAtSlot : viewerSet) {
                viewerAtSlot.notifyUpdate();
            }
        }
    }
    
    public void addViewer(AbstractWindow<?> who, int what, int how) {
        synchronized (viewers) {
            var viewerSet = this.viewers[what];
            if (viewerSet == null) {
                viewerSet = new HashSet<>();
                this.viewers[what] = viewerSet;
            }
            viewerSet.add(new ViewerAtSlot(who, how));
        }
    }
    
    public void removeViewer(AbstractWindow<?> who, int what, int how) {
        synchronized (viewers) {
            var viewerSet = this.viewers[what];
            if (viewerSet != null) {
                viewerSet.remove(new ViewerAtSlot(who, how));
                if (viewerSet.isEmpty())
                    this.viewers[what] = null;
            }
        }
    }
    
    @Override
    public Collection<Window> getWindows() {
        synchronized (viewers) {
            var windows = new HashSet<Window>();
            for (var viewerSet : viewers) {
                if (viewerSet != null) {
                    for (var viewerAtSlot : viewerSet) {
                        windows.add(viewerAtSlot.window());
                    }
                }
            }
            
            return Collections.unmodifiableSet(windows);
        }
    }
    
    @Override
    public Collection<Player> getCurrentViewers() {
        return getWindows().stream()
            .filter(Window::isOpen)
            .map(Window::getViewer)
            .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public void closeForAllViewers() {
        getCurrentViewers().forEach(Player::closeInventory);
    }
    
    @Override
    public void playAnimation(Animation animation) {
        if (this.animation != null)
            cancelAnimation();
        
        var animationImpl = (AnimationImpl) animation;
        this.animation = animationImpl;
        this.animationElements = slotElements.clone();
        animationImpl.bind(this);
        for (Slot slot : animationImpl.getRemainingSlots()) {
            setSlotElement(slot.x(), slot.y(), null);
        }
        animationImpl.addShowHandler(slots -> {
            for (Slot slot : slots) {
                int i = convToIndex(slot.x(), slot.y());
                assert animationElements != null;
                setSlotElement(i, animationElements[i]);
            }
        });
        animationImpl.addFinishHandler(() -> {
            this.animation = null;
            this.animationElements = null;
        });
        
        animationImpl.start();
    }
    
    @Override
    public boolean isAnimationRunning() {
        return animation != null;
    }
    
    @Override
    public void cancelAnimation() {
        if (this.animation != null) {
            // cancel the scheduler task and set animation to null
            animation.cancel();
            animation = null;
            
            // show all SlotElements again
            assert animationElements != null;
            for (int i = 0; i < size; i++) {
                setSlotElement(i, animationElements[i]);
            }
            animationElements = null;
        }
    }
    
    @Override
    public void setSlotElement(int index, @Nullable SlotElement slotElement) {
        slotElements[index] = slotElement;
        
        // set the gui if it is a bound item
        if (slotElement instanceof SlotElement.Item(Item item)) {
            if (item instanceof BoundItem boundItem && !boundItem.isBound()) {
                boundItem.bind(this);
            }
        }
        
        // notify parents that a slot element has been changed
        var viewers = this.viewers[index];
        if (viewers != null) {
            for (var viewer : viewers) {
                viewer.notifyUpdate();
            }
        }
    }
    
    @Override
    public void remove(int index) {
        setSlotElement(index, null);
    }
    
    @Override
    public @Nullable SlotElement getSlotElement(int index) {
        return slotElements[index];
    }
    
    @Override
    public boolean hasSlotElement(int index) {
        return slotElements[index] != null;
    }
    
    @Override
    @Nullable
    public SlotElement[] getSlotElements() {
        return slotElements.clone();
    }
    
    @Override
    public void setItem(int index, @Nullable Item item) {
        if (item == null) {
            remove(index);
            return;
        }
        setSlotElement(index, new SlotElement.Item(item));
    }
    
    @Override
    public void addSlotElements(SlotElement... slotElements) {
        int elementIndex = 0;
        for (int i = 0; i < getSize(); i++) {
            if (elementIndex >= slotElements.length)
                break;
            
            if (this.slotElements[i] == null) {
                setSlotElement(i, slotElements[elementIndex++]);
            }
        }
    }
    
    @Override
    public void addItems(Item... items) {
        int elementIndex = 0;
        for (int i = 0; i < getSize(); i++) {
            if (elementIndex >= items.length)
                break;
            
            if (this.slotElements[i] == null) {
                setSlotElement(i, new SlotElement.Item(items[elementIndex++]));
            }
        }
    }
    
    @Override
    public @Nullable Item getItem(int index) {
        SlotElement slotElement = slotElements[index];
        
        if (slotElement instanceof SlotElement.Item) {
            return ((SlotElement.Item) slotElement).item();
        } else if (slotElement instanceof SlotElement.GuiLink) {
            SlotElement holdingElement = slotElement.getHoldingElement();
            if (holdingElement instanceof SlotElement.Item)
                return ((SlotElement.Item) holdingElement).item();
        }
        
        return null;
    }
    
    @Override
    public @Nullable ItemProvider getBackground() {
        return background;
    }
    
    @Override
    public void setBackground(@Nullable ItemProvider itemProvider) {
        this.background = itemProvider;
    }
    
    @Override
    public void applyStructure(Structure structure) {
        if (structure.getWidth() != width)
            throw new IllegalArgumentException("Structure width (" + structure.getWidth() + " does not match gui width (" + width + ")");
        if (structure.getHeight() != height)
            throw new IllegalArgumentException("Structure height (" + structure.getHeight() + " does not match gui height (" + height + ")");
        
        var matrix = structure.getIngredientMatrix();
        this.ingredientMatrix = matrix;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setSlotElement(x, y, matrix.getSlotElement(x, y));
            }
        }
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
    
    @Override
    public boolean isFrozen() {
        return frozen;
    }
    
    @Override
    public void setIgnoreObscuredInventorySlots(boolean ignoreObscuredInventorySlots) {
        this.ignoreObscuredInventorySlots = ignoreObscuredInventorySlots;
    }
    
    @Override
    public boolean isIgnoreObscuredInventorySlots() {
        return ignoreObscuredInventorySlots;
    }
    
    //<editor-fold desc="ingredient-key-based methods">
    @Override
    public void notifyWindows(char key, char... keys) {
        var matrix = ingredientMatrix;
        if (matrix == null)
            return;
        
        for (char c : ArrayUtils.concat(key, keys)) {
            for (Slot slot : matrix.getSlots(key)) {
                notifyWindows(slot);
            }
        }
    }
    
    @Override
    public void setSlotElement(char key, @Nullable SlotElement slotElement) {
        setSlotElement(key, () -> slotElement);
    }
    
    @Override
    public void setSlotElement(char key, Supplier<? extends @Nullable SlotElement> elementSupplier) {
        var matrix = ingredientMatrix;
        if (matrix == null)
            return;
        
        for (Slot slot : matrix.getSlots(key)) {
            setSlotElement(slot, elementSupplier.get());
        }
    }
    
    @Override
    public void setItem(char key, @Nullable Item item) {
        setItem(key, () -> item);
    }
    
    @Override
    public void setItem(char key, Item.Builder<?> itemBuilder) {
        setItem(key, itemBuilder::build);
    }
    
    @Override
    public void setItem(char key, Supplier<? extends @Nullable Item> itemSupplier) {
        var matrix = ingredientMatrix;
        if (matrix == null)
            return;
        
        for (Slot slot : matrix.getSlots(key)) {
            setItem(slot, itemSupplier.get());
        }
    }
    
    @Override
    public void setInventory(char key, Inventory inventory) {
        setSlotElement(key, new InventorySlotElementSupplier(inventory));
    }
    
    @Override
    public void setInventory(char key, Inventory inventory, ItemProvider background) {
        setSlotElement(key, new InventorySlotElementSupplier(inventory, background));
    }
    
    @Override
    public void setGui(char key, Gui gui) {
        setSlotElement(key, new GuiSlotElementSupplier(gui));
    }
    
    @Override
    public SequencedCollection<? extends Slot> getSlots(char key) {
        var matrix = ingredientMatrix;
        if (matrix == null)
            return List.of();
        
        return matrix.getSlots(key);
    }
    
    @Override
    public boolean isTagged(int i, char key) {
        var matrix = ingredientMatrix;
        if (matrix == null)
            return false;
        
        return matrix.getKey(i) == key;
    }
    
    @Override
    public @Nullable Character getKey(int i) {
        var matrix = ingredientMatrix;
        if (matrix == null)
            return null;
        
        return matrix.getKey(i);
    }
    //</editor-fold>
    
    //<editor-fold desc="coordinate-based methods">
    @Override
    public void notifyWindows(Slot slot) {
        notifyWindows(slot.x(), slot.y());
    }
    
    @Override
    public void notifyWindows(int x, int y) {
        notifyWindows(convToIndex(x, y));
    }
    
    @Override
    public void setSlotElement(Slot slot, @Nullable SlotElement slotElement) {
        setSlotElement(slot.x(), slot.y(), slotElement);
    }
    
    @Override
    public void setSlotElement(int x, int y, @Nullable SlotElement slotElement) {
        setSlotElement(convToIndex(x, y), slotElement);
    }
    
    @Override
    public @Nullable SlotElement getSlotElement(Slot slot) {
        return getSlotElement(slot.x(), slot.y());
    }
    
    @Override
    public @Nullable SlotElement getSlotElement(int x, int y) {
        return getSlotElement(convToIndex(x, y));
    }
    
    @Override
    public boolean hasSlotElement(Slot slot) {
        return hasSlotElement(slot.x(), slot.y());
    }
    
    @Override
    public boolean hasSlotElement(int x, int y) {
        return hasSlotElement(convToIndex(x, y));
    }
    
    @Override
    public void setItem(Slot slot, @Nullable Item item) {
        setItem(slot.x(), slot.y(), item);
    }
    
    @Override
    public void setItem(int x, int y, @Nullable Item item) {
        setItem(convToIndex(x, y), item);
    }
    
    @Override
    public @Nullable Item getItem(Slot slot) {
        return getItem(slot.x(), slot.y());
    }
    
    @Override
    public @Nullable Item getItem(int x, int y) {
        return getItem(convToIndex(x, y));
    }
    
    @Override
    public void remove(Slot slot) {
        remove(slot.x(), slot.y());
    }
    
    @Override
    public void remove(int x, int y) {
        remove(convToIndex(x, y));
    }
    
    @Override
    public boolean isTagged(int x, int y, char key) {
        return isTagged(convToIndex(x, y), key);
    }
    
    @Override
    public boolean isTagged(Slot slot, char key) {
        return isTagged(slot.x(), slot.y(), key);
    }
    
    @Override
    public @Nullable Character getKey(int x, int y) {
        return getKey(convToIndex(x, y));
    }
    
    @Override
    public @Nullable Character getKey(Slot slot) {
        return getKey(slot.x(), slot.y());
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    private int convToIndex(int x, int y) {
        if (x >= width || y >= height) throw new IllegalArgumentException("Coordinates out of bounds");
        return SlotUtils.convertToIndex(x, y, width);
    }
    //</editor-fold>
    
    //<editor-fold desc="filling methods">
    private void fill(Set<Integer> slots, @Nullable Item item, boolean replaceExisting) {
        for (int slot : slots) {
            if (!replaceExisting && hasSlotElement(slot))
                continue;
            setItem(slot, item);
        }
    }
    
    @Override
    public void fill(int start, int end, @Nullable Item item, boolean replaceExisting) {
        for (int i = start; i < end; i++) {
            if (!replaceExisting && hasSlotElement(i))
                continue;
            setItem(i, item);
        }
    }
    
    @Override
    public void fill(@Nullable Item item, boolean replaceExisting) {
        fill(0, getSize(), item, replaceExisting);
    }
    
    @Override
    public void fillRow(int row, @Nullable Item item, boolean replaceExisting) {
        if (row >= height)
            throw new IllegalArgumentException("Row out of bounds");
        fill(SlotUtils.getSlotsRow(row, width), item, replaceExisting);
    }
    
    @Override
    public void fillColumn(int column, @Nullable Item item, boolean replaceExisting) {
        if (column >= width)
            throw new IllegalArgumentException("Column out of bounds");
        fill(SlotUtils.getSlotsColumn(column, width, height), item, replaceExisting);
    }
    
    @Override
    public void fillBorders(@Nullable Item item, boolean replaceExisting) {
        fill(SlotUtils.getSlotsBorders(width, height), item, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, int height, @Nullable Item item, boolean replaceExisting) {
        fill(SlotUtils.getSlotsRect(x, y, width, height, this.width), item, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, Gui gui, boolean replaceExisting) {
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, gui.getWidth(), gui.getHeight(), this.width)) {
            if (hasSlotElement(slot) && !replaceExisting)
                continue;
            setSlotElement(slot, new SlotElement.GuiLink(gui, slotIndex));
            slotIndex++;
        }
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, Inventory inventory, boolean replaceExisting) {
        fillRectangle(x, y, width, inventory, null, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, Inventory inventory, @Nullable ItemProvider background, boolean replaceExisting) {
        int height = (int) Math.ceil((double) inventory.getSize() / (double) width);
        
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, width, height, this.width)) {
            if (slotIndex >= inventory.getSize()) return;
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new SlotElement.InventoryLink(inventory, slotIndex, background));
            slotIndex++;
        }
    }
    //</editor-fold>
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<G extends Gui, S extends Builder<G, S>>
        implements Builder<G, S>
        permits NormalGuiImpl.Builder, AbstractPagedGui.AbstractBuilder, AbstractScrollGui.AbstractBuilder, TabGuiImpl.Builder
    {
        
        protected @Nullable Structure structure;
        protected @Nullable ItemProvider background;
        protected @Nullable List<Consumer<G>> modifiers;
        protected boolean frozen;
        protected boolean ignoreObscuredInventorySlots = true;
        
        @Override
        public S setStructure(int width, int height, String structureData) {
            structure = new Structure(width, height, structureData);
            return (S) this;
        }
        
        @Override
        public S setStructure(String... structureData) {
            structure = new Structure(structureData);
            return (S) this;
        }
        
        @Override
        public S setStructure(Structure structure) {
            this.structure = structure;
            return (S) this;
        }
        
        @Override
        public S applyPreset(IngredientPreset preset) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.applyPreset(preset);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, ItemStack itemStack) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, itemStack);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, ItemProvider itemProvider) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, itemProvider);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Item item) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, item);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Item.Builder<?> builder) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, builder);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Supplier<? extends Item> itemSupplier) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, itemSupplier);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Inventory inventory) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, inventory);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Inventory inventory, @Nullable ItemProvider background) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, inventory, background);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Gui gui) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, gui);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, SlotElement element) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, element);
            return (S) this;
        }
        
        @Override
        public S addIngredientElementSupplier(char key, Supplier<? extends SlotElement> elementSupplier) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredientElementSupplier(key, elementSupplier);
            return (S) this;
        }
        
        @Override
        public S addIngredient(char key, Marker marker) {
            if (structure == null)
                throw new IllegalStateException("Structure is not set");
            structure.addIngredient(key, marker);
            return (S) this;
        }
        
        @Override
        public S setBackground(ItemProvider itemProvider) {
            background = itemProvider;
            return (S) this;
        }
        
        @Override
        public S setBackground(ItemStack itemStack) {
            background = new ItemWrapper(itemStack);
            return (S) this;
        }
        
        @Override
        public S setFrozen(boolean frozen) {
            this.frozen = frozen;
            return (S) this;
        }
        
        @Override
        public S setIgnoreObscuredInventorySlots(boolean ignoreObscuredInventorySlots) {
            this.ignoreObscuredInventorySlots = ignoreObscuredInventorySlots;
            return (S) this;
        }
        
        @Override
        public S addModifier(Consumer<G> modifier) {
            if (modifiers == null)
                modifiers = new ArrayList<>();
            
            modifiers.add(modifier);
            return (S) this;
        }
        
        @Override
        public S setModifiers(List<Consumer<G>> modifiers) {
            this.modifiers = modifiers;
            return (S) this;
        }
        
        /**
         * Applies the {@link AbstractBuilder#modifiers} to the given {@link AbstractGui}.
         *
         * @param gui The {@link AbstractGui} to apply the modifiers to
         */
        protected void applyModifiers(G gui) {
            gui.setFrozen(frozen);
            gui.setIgnoreObscuredInventorySlots(ignoreObscuredInventorySlots);
            if (background != null) gui.setBackground(background);
            if (modifiers != null) modifiers.forEach(modifier -> modifier.accept(gui));
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public S clone() {
            try {
                var clone = (AbstractBuilder<G, S>) super.clone();
                if (structure != null)
                    clone.structure = structure.clone();
                if (modifiers != null)
                    clone.modifiers = new ArrayList<>(modifiers);
                return (S) clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
        
    }
    
}
