package xyz.xenondevs.invui.gui;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.animation.Animation;
import xyz.xenondevs.invui.internal.Viewer;
import xyz.xenondevs.invui.internal.ViewerAtSlot;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ObscuredInventory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.*;
import xyz.xenondevs.invui.util.ItemUtils;
import xyz.xenondevs.invui.window.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @hidden
 */
@Internal
public sealed abstract class AbstractGui
    implements Gui, Viewer
    permits NormalGuiImpl, AbstractPagedGui, AbstractScrollGui, TabGuiImpl
{
    
    private final int width;
    private final int height;
    private final int size;
    private final @Nullable SlotElement[] slotElements;
    private final @Nullable Set<ViewerAtSlot<?>>[] viewers;
    
    private boolean frozen;
    private boolean ignoreObscuredInventorySlots = true;
    private @Nullable ItemProvider background;
    private @Nullable Animation animation;
    private @Nullable SlotElement @Nullable [] animationElements;
    
    @SuppressWarnings("unchecked")
    AbstractGui(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        slotElements = new SlotElement[size];
        viewers = new Set[size];
    }
    
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        // cancel all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null) {
            event.setCancelled(true);
            return;
        }
        
        SlotElement slotElement = slotElements[slotNumber];
        switch (slotElement) {
            case SlotElement.GuiLink linkedElement -> {
                AbstractGui gui = (AbstractGui) linkedElement.gui();
                gui.handleClick(linkedElement.slot(), player, clickType, event);
            }
            
            case SlotElement.Item itemElement -> {
                event.setCancelled(true); // if it is an Item, don't let the player move it
                itemElement.item().handleClick(clickType, player, new Click(event));
            }
            
            case SlotElement.InventoryLink inventorySlotElement ->
                handleInvSlotElementClick(inventorySlotElement, event);
            
            case null, default -> event.setCancelled(true); // Only InventorySlotElements have allowed interactions
        }
    }
    
    //<editor-fold desc="inventories">
    protected void handleInvSlotElementClick(SlotElement.InventoryLink element, InventoryClickEvent event) {
        // these actions are ignored as they don't modify the inventory
        InventoryAction action = event.getAction();
        if (action != InventoryAction.DROP_ALL_CURSOR && action != InventoryAction.DROP_ONE_CURSOR) {
            event.setCancelled(true);
            
            Inventory inventory = element.inventory();
            int slot = element.slot();
            
            Player player = (Player) event.getWhoClicked();
            
            ItemStack cursor = ItemUtils.takeUnlessEmpty(event.getCursor());
            ItemStack clicked = ItemUtils.takeUnlessEmpty(event.getCurrentItem());
            
            ItemStack technicallyClicked = inventory.getItem(slot);
            if (inventory.isSynced(slot, clicked) || didClickBackgroundItem(player, element, inventory, slot, clicked)) {
                
                // using enum names because SWAP_OFFHAND does not exist on earlier versions 
                switch (event.getClick().name()) {
                    case "LEFT":
                        handleInvLeftClick(event, inventory, slot, player, technicallyClicked, cursor);
                        break;
                    case "RIGHT":
                        handleInvRightClick(event, inventory, slot, player, technicallyClicked, cursor);
                        break;
                    case "SHIFT_RIGHT":
                    case "SHIFT_LEFT":
                        handleInvItemShift(event, inventory, slot, player, technicallyClicked);
                        break;
                    case "NUMBER_KEY":
                        handleInvNumberKey(event, inventory, slot, player, technicallyClicked);
                        break;
                    case "SWAP_OFFHAND":
                        handleInvOffHandKey(event, inventory, slot, player, technicallyClicked);
                        break;
                    case "DROP":
                        handleInvDrop(false, event, inventory, slot, player, technicallyClicked);
                        break;
                    case "CONTROL_DROP":
                        handleInvDrop(true, event, inventory, slot, player, technicallyClicked);
                        break;
                    case "DOUBLE_CLICK":
                        handleInvDoubleClick(event, player, cursor);
                        break;
                    case "MIDDLE":
                        handleInvMiddleClick(event, inventory, slot, player);
                        break;
                    default:
                        InvUI.getInstance().getLogger().warning("Unknown click type: " + event.getClick().name());
                        break;
                }
            }
        }
    }
    
    private boolean didClickBackgroundItem(Player player, SlotElement.InventoryLink element, Inventory inventory, int slot, @Nullable ItemStack clicked) {
        Locale lang = player.locale();
        return !inventory.hasItem(slot) && (isBuilderSimilar(background, lang, clicked) || isBuilderSimilar(element.background(), lang, clicked));
    }
    
    private boolean isBuilderSimilar(@Nullable ItemProvider builder, Locale lang, @Nullable ItemStack expected) {
        return builder != null && builder.get(lang).isSimilar(expected);
    }
    
    @SuppressWarnings("deprecation")
    protected void handleInvLeftClick(InventoryClickEvent event, Inventory inventory, int slot, Player player, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        if (cursor == null) {
            // if the cursor is empty, pick the stack up
            if (inventory.setItem(updateReason, slot, null))
                event.setCursor(clicked);
        } else if (clicked == null || cursor.isSimilar(clicked)) {
            // if there are no items, or they're similar to the cursor, add the cursor items to the stack
            int remains = inventory.putItem(updateReason, slot, cursor);
            if (remains == 0) {
                event.setCursor(null);
            } else {
                cursor.setAmount(remains);
                event.setCursor(cursor);
            }
        } else if (!cursor.isSimilar(clicked)) {
            // if the stacks are not similar, swap them
            if (inventory.setItem(updateReason, slot, cursor))
                event.setCursor(clicked);
        }
    }
    
    @SuppressWarnings("deprecation")
    protected void handleInvRightClick(InventoryClickEvent event, Inventory inventory, int slot, Player player, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        if (cursor == null) {
            // if the cursor is empty, split the stack to the cursor
            // if the stack is not divisible by 2, give the cursor the bigger part
            int clickedAmount = clicked.getAmount();
            int newClickedAmount = clickedAmount / 2;
            int newCursorAmount = clickedAmount - newClickedAmount;
            
            cursor = clicked.clone();
            
            clicked.setAmount(newClickedAmount);
            cursor.setAmount(newCursorAmount);
            
            if (inventory.setItem(updateReason, slot, clicked))
                event.setCursor(cursor);
        } else {
            // put one item from the cursor in the inventory
            ItemStack toAdd = cursor.clone();
            toAdd.setAmount(1);
            int remains = inventory.putItem(updateReason, slot, toAdd);
            if (remains == 0) {
                cursor.setAmount(cursor.getAmount() - 1);
                event.setCursor(cursor);
            }
        }
    }
    
    protected void handleInvItemShift(InventoryClickEvent event, Inventory inventory, int slot, Player player, @Nullable ItemStack clicked) {
        if (clicked == null)
            return;
        
        ItemStack previousStack = clicked.clone();
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Window window = WindowManager.getInstance().getOpenWindow(player);
        ItemPreUpdateEvent updateEvent = inventory.callPreUpdateEvent(updateReason, slot, previousStack, null);
        
        if (!updateEvent.isCancelled()) {
            int leftOverAmount;
            if (window instanceof AbstractDoubleWindow) {
                Gui otherGui;
                if (window instanceof AbstractSplitWindow splitWindow) {
                    Gui[] guis = splitWindow.getGuis();
                    otherGui = guis[0] == this ? guis[1] : guis[0];
                } else {
                    otherGui = this;
                }
                
                leftOverAmount = ((AbstractGui) otherGui).putIntoFirstInventory(updateReason, clicked, inventory);
            } else {
                Inventory playerInventory = ReferencingInventory.fromReversedPlayerStorageContents(player.getInventory());
                leftOverAmount = playerInventory.addItem(null, inventory.getItem(slot));
            }
            
            clicked.setAmount(leftOverAmount);
            if (ItemUtils.isEmpty(clicked))
                clicked = null;
            
            inventory.setItemSilently(slot, clicked);
            
            inventory.callPostUpdateEvent(updateReason, slot, previousStack, clicked);
        }
    }
    
    // TODO: add support for merged windows
    protected void handleInvNumberKey(InventoryClickEvent event, Inventory inventory, int slot, Player player, @Nullable ItemStack clicked) {
        Window window = WindowManager.getInstance().getOpenWindow(player);
        if (window instanceof AbstractSingleWindow) {
            org.bukkit.inventory.Inventory playerInventory = player.getInventory();
            int hotbarButton = event.getHotbarButton();
            ItemStack hotbarItem = ItemUtils.takeUnlessEmpty(playerInventory.getItem(hotbarButton));
            
            UpdateReason updateReason = new PlayerUpdateReason(player, event);
            
            if (inventory.setItem(updateReason, slot, hotbarItem))
                playerInventory.setItem(hotbarButton, clicked);
        }
    }
    
    // TODO: add support for merged windows
    protected void handleInvOffHandKey(InventoryClickEvent event, Inventory inventory, int slot, Player player, @Nullable ItemStack clicked) {
        Window window = WindowManager.getInstance().getOpenWindow(player);
        if (window instanceof AbstractSingleWindow) {
            PlayerInventory playerInventory = player.getInventory();
            ItemStack offhandItem = ItemUtils.takeUnlessEmpty(playerInventory.getItemInOffHand());
            
            UpdateReason updateReason = new PlayerUpdateReason(player, event);
            
            if (inventory.setItem(updateReason, slot, offhandItem))
                playerInventory.setItemInOffHand(clicked);
        }
    }
    
    protected void handleInvDrop(boolean ctrl, InventoryClickEvent event, Inventory inventory, int slot, Player player, @Nullable ItemStack clicked) {
        if (clicked == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        if (ctrl) {
            if (inventory.setItem(updateReason, slot, null)) {
                InventoryUtils.dropItemLikePlayer(player, clicked);
            }
        } else if (inventory.addItemAmount(updateReason, slot, -1) == -1) {
            clicked.setAmount(1);
            InventoryUtils.dropItemLikePlayer(player, clicked);
        }
        
    }
    
    protected void handleInvDoubleClick(InventoryClickEvent event, Player player, @Nullable ItemStack cursor) {
        if (cursor == null)
            return;
        
        // windows handle cursor collect because it is a cross-inventory / cross-gui operation
        Window window = WindowManager.getInstance().getOpenWindow(player);
        ((AbstractWindow) window).handleCursorCollect(event);
    }
    
    @SuppressWarnings("deprecation")
    protected void handleInvMiddleClick(InventoryClickEvent event, Inventory inventory, int slot, Player player) {
        if (player.getGameMode() != GameMode.CREATIVE)
            return;
        
        ItemStack cursor = inventory.getItem(slot);
        if (cursor != null)
            cursor.setAmount(cursor.getMaxStackSize());
        event.setCursor(cursor);
    }
    
    public boolean handleItemDrag(UpdateReason updateReason, int slot, @Nullable ItemStack oldStack, ItemStack newStack) {
        // cancel all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null)
            return false;
        
        SlotElement element = getSlotElement(slot);
        if (element != null) element = element.getHoldingElement();
        if (element instanceof SlotElement.InventoryLink invSlotElement) {
            Inventory inventory = invSlotElement.inventory();
            int viSlot = invSlotElement.slot();
            if (inventory.isSynced(viSlot, oldStack)) {
                return inventory.setItem(updateReason, viSlot, newStack);
            }
        }
        
        return false;
    }
    
    public void handleItemShift(InventoryClickEvent event) {
        event.setCancelled(true);
        
        // cancel all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null)
            return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null)
            return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        int amountLeft = putIntoFirstInventory(updateReason, clicked);
        if (amountLeft != clicked.getAmount()) {
            if (amountLeft != 0) event.getCurrentItem().setAmount(amountLeft);
            else event.getClickedInventory().setItem(event.getSlot(), null);
        }
    }
    
    protected int putIntoFirstInventory(UpdateReason updateReason, ItemStack itemStack, Inventory... ignored) {
        Collection<Inventory> inventories = getAllInventories(ignored);
        int originalAmount = itemStack.getAmount();
        
        if (!inventories.isEmpty()) {
            for (Inventory inventory : inventories) {
                int amountLeft = inventory.addItem(updateReason, itemStack);
                if (originalAmount != amountLeft)
                    return amountLeft;
            }
        }
        
        return originalAmount;
    }
    
    public Map<Inventory, Set<Integer>> getAllInventorySlots(Inventory... ignored) {
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
    
    public Collection<Inventory> getAllInventories(Inventory... ignored) {
        if (!ignoreObscuredInventorySlots)
            return getAllInventorySlots(ignored).keySet();
        
        ArrayList<Inventory> inventories = new ArrayList<>();
        for (Map.Entry<Inventory, Set<Integer>> entry : getAllInventorySlots(ignored).entrySet()) {
            Inventory inventory = entry.getKey();
            Set<Integer> slots = entry.getValue();
            inventories.add(new ObscuredInventory(inventory, slot -> !slots.contains(slot)));
        }
        
        return inventories;
    }
    //</editor-fold>
    
    @Override
    public void notifyUpdate(int slot) {
        // no need for synchronization on viewers because this method is called on-main and read-only
        var viewers = this.viewers[slot];
        if (viewers != null) {
            for (var viewer : viewers) {
                viewer.notifyUpdate();
            }
        }
    }
    
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
    
    public void addViewer(Viewer who, int what, int how) {
        synchronized (viewers) {
            var viewerSet = this.viewers[what];
            if (viewerSet == null) {
                viewerSet = new HashSet<>();
                this.viewers[what] = viewerSet;
            }
            viewerSet.add(new ViewerAtSlot<>(who, how));
        }
    }
    
    public void removeViewer(Viewer who, int what, int how) {
        synchronized (viewers) {
            var viewerSet = this.viewers[what];
            if (viewerSet != null) {
                viewerSet.remove(new ViewerAtSlot<>(who, how));
                if (viewerSet.isEmpty())
                    this.viewers[what] = null;
            }
        }
    }
    
    @Override
    public List<Window> findAllWindows() {
        // no need for synchronization on viewers because this method is called on-main and read-only
        var windows = new ArrayList<Window>();
        
        var explored = new HashSet<Viewer>();
        var queue = new LinkedList<Viewer>();
        queue.add(this);
        
        while (!queue.isEmpty()) {
            var current = queue.poll();
            explored.add(current);
            
            if (current instanceof AbstractGui viewable) {
                for (var viewers : viewable.viewers) {
                    if (viewers == null)
                        continue;
                    
                    for (var viewerAtSlot : viewers) {
                        var viewer = viewerAtSlot.viewer();
                        if (!explored.contains(viewer)) {
                            queue.add(viewer);
                        }
                    }
                }
            } else if (current instanceof Window window) {
                windows.add(window);
            }
        }
        
        return windows;
    }
    
    @Override
    public Set<Player> findAllCurrentViewers() {
        return findAllWindows().stream()
            .map(Window::getCurrentViewer)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
    
    @Override
    public void closeForAllViewers() {
        findAllCurrentViewers().forEach(Player::closeInventory);
    }
    
    @Override
    public void playAnimation(Animation animation, @Nullable Predicate<SlotElement> filter) {
        if (this.animation != null)
            cancelAnimation();
        
        this.animation = animation;
        this.animationElements = slotElements.clone();
        
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SlotElement element = getSlotElement(i);
            if (element != null && (filter == null || filter.test(element))) {
                slots.add(i);
                setSlotElement(i, null);
            }
        }
        
        animation.setSlots(slots);
        animation.setGui(this);
        animation.setWindows(findAllWindows());
        animation.addShowHandler((frame, index) -> setSlotElement(index, animationElements[index]));
        animation.addFinishHandler(() -> {
            this.animation = null;
            this.animationElements = null;
        });
        
        animation.start();
    }
    
    @Override
    public void cancelAnimation() {
        if (this.animation != null) {
            // cancel the scheduler task and set animation to null
            animation.cancel();
            animation = null;
            
            // show all SlotElements again
            for (int i = 0; i < size; i++) setSlotElement(i, animationElements[i]);
            animationElements = null;
        }
    }
    
    @Override
    public void setSlotElement(int index, @Nullable SlotElement slotElement) {
        SlotElement oldElement = slotElements[index];
        slotElements[index] = slotElement;
        
        // set the gui if it is a bound item
        if (slotElement instanceof SlotElement.Item(Item item)) {
            if (item instanceof BoundItem boundItem && !boundItem.isBound()) {
                boundItem.bind(this);
            }
        }
        
        // remove this gui as a viewer from the old slot element's gui
        if (oldElement instanceof SlotElement.GuiLink(Gui gui, int slot)) {
            ((AbstractGui) gui).removeViewer(this, slot, index);
        }
        
        // add this gui as a viewer to the new slot element's viewable
        if (slotElement instanceof SlotElement.GuiLink(Gui gui, int slot)) {
            ((AbstractGui) gui).addViewer(this, slot, index);
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
        structure.getIngredientList().insertIntoGui(this);
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
    
    //<editor-fold desc="coordinate-based methods">
    @Override
    public void setSlotElement(int x, int y, @Nullable SlotElement slotElement) {
        setSlotElement(convToIndex(x, y), slotElement);
    }
    
    @Override
    public @Nullable SlotElement getSlotElement(int x, int y) {
        return getSlotElement(convToIndex(x, y));
    }
    
    @Override
    public boolean hasSlotElement(int x, int y) {
        return hasSlotElement(convToIndex(x, y));
    }
    
    @Override
    public void setItem(int x, int y, @Nullable Item item) {
        setItem(convToIndex(x, y), item);
    }
    
    @Override
    public @Nullable Item getItem(int x, int y) {
        return getItem(convToIndex(x, y));
    }
    
    @Override
    public void remove(int x, int y) {
        remove(convToIndex(x, y));
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
    static sealed abstract class AbstractBuilder<G extends Gui, S extends Gui.Builder<G, S>>
        implements Gui.Builder<G, S>
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
