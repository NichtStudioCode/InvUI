package de.studiocode.invui.gui;

import de.studiocode.invui.animation.Animation;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.SlotElement.LinkedSlotElement;
import de.studiocode.invui.gui.SlotElement.VISlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.item.impl.controlitem.ControlItem;
import de.studiocode.invui.util.InventoryUtils;
import de.studiocode.invui.util.SlotUtils;
import de.studiocode.invui.util.ArrayUtils;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import de.studiocode.invui.virtualinventory.event.ItemUpdateEvent;
import de.studiocode.invui.virtualinventory.event.PlayerUpdateReason;
import de.studiocode.invui.virtualinventory.event.UpdateReason;
import de.studiocode.invui.window.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractGUI implements GUI, GUIParent {
    
    private final int width;
    private final int height;
    private final int size;
    private final SlotElement[] slotElements;
    private final Set<GUIParent> parents = new HashSet<>();
    
    private SlotElement[] animationElements;
    private Animation animation;
    
    private ItemProvider background;
    
    public AbstractGUI(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        slotElements = new SlotElement[size];
    }
    
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        if (animation != null) {
            // cancel all clicks if an animation is running
            event.setCancelled(true);
            return;
        }
        
        SlotElement slotElement = slotElements[slotNumber];
        if (slotElement instanceof LinkedSlotElement) {
            LinkedSlotElement linkedElement = (LinkedSlotElement) slotElement;
            AbstractGUI gui = (AbstractGUI) linkedElement.getGUI();
            gui.handleClick(linkedElement.getSlotIndex(), player, clickType, event);
        } else if (slotElement instanceof ItemSlotElement) {
            event.setCancelled(true); // if it is an Item, don't let the player move it
            ItemSlotElement itemElement = (ItemSlotElement) slotElement;
            itemElement.getItem().handleClick(clickType, player, event);
        } else if (slotElement instanceof VISlotElement) {
            handleVISlotElementClick((VISlotElement) slotElement, event);
        } else event.setCancelled(true); // Only VISlotElements have allowed interactions
    }
    
    // region virtual inventories
    protected void handleVISlotElementClick(VISlotElement element, InventoryClickEvent event) {
        // these actions are ignored as they don't modify the inventory
        InventoryAction action = event.getAction();
        if (action != InventoryAction.CLONE_STACK
            && action != InventoryAction.DROP_ALL_CURSOR
            && action != InventoryAction.DROP_ONE_CURSOR
        ) {
            event.setCancelled(true);
            
            VirtualInventory inventory = element.getVirtualInventory();
            int slot = element.getSlot();
            
            Player player = (Player) event.getWhoClicked();
            
            ItemStack cursor = event.getCursor();
            if (cursor != null && cursor.getType().isAir()) cursor = null;
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType().isAir()) clicked = null;
            
            ItemStack technicallyClicked = inventory.getItemStack(slot);
            if (inventory.isSynced(slot, clicked) || didClickBackgroundItem(player, element, inventory, slot, clicked)) {
                
                switch (event.getClick()) {
                    case LEFT:
                        handleVILeftClick(event, inventory, slot, player, technicallyClicked, cursor);
                        break;
                    case RIGHT:
                        handleVIRightClick(event, inventory, slot, player, technicallyClicked, cursor);
                        break;
                    case SHIFT_RIGHT:
                    case SHIFT_LEFT:
                        handleVIItemShift(event, inventory, slot, player, technicallyClicked);
                        break;
                    case NUMBER_KEY:
                        handleVINumberKey(event, inventory, slot, player, technicallyClicked);
                        break;
                    case SWAP_OFFHAND:
                        handleVIOffHandKey(event, inventory, slot, player, technicallyClicked);
                        break;
                    case DROP:
                        handleVIDrop(false, event, inventory, slot, player, technicallyClicked);
                        break;
                    case CONTROL_DROP:
                        handleVIDrop(true, event, inventory, slot, player, technicallyClicked);
                        break;
                    case DOUBLE_CLICK:
                        handleVIDoubleClick(event, inventory, player, cursor);
                        break;
                }
            }
        }
    }
    
    private boolean didClickBackgroundItem(Player player, VISlotElement element, VirtualInventory inventory, int slot, ItemStack clicked) {
        UUID uuid = player.getUniqueId();
        return inventory.getUnsafeItemStack(slot) == null
            && (isBuilderSimilar(background, uuid, clicked) || isBuilderSimilar(element.getBackground(), uuid, clicked));
    }
    
    private boolean isBuilderSimilar(ItemProvider builder, UUID uuid, ItemStack expected) {
        return builder != null && builder.getFor(uuid).isSimilar(expected);
    }
    
    @SuppressWarnings("deprecation")
    protected void handleVILeftClick(InventoryClickEvent event, VirtualInventory inventory, int slot, Player player, ItemStack clicked, ItemStack cursor) {
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null) return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        if (cursor == null) {
            // if the cursor is empty, pick the stack up
            if (inventory.setItemStack(updateReason, slot, null))
                event.setCursor(clicked);
        } else if (clicked == null || cursor.isSimilar(clicked)) {
            // if there are no items, or they're similar to the cursor, add the cursor items to the stack
            int remains = inventory.putItemStack(updateReason, slot, cursor);
            if (remains == 0) {
                event.setCursor(null);
            } else {
                cursor.setAmount(remains);
                event.setCursor(cursor);
            }
        } else if (!cursor.isSimilar(clicked)) {
            // if the stacks are not similar, swap them
            if (inventory.setItemStack(updateReason, slot, cursor))
                event.setCursor(clicked);
        }
    }
    
    @SuppressWarnings("deprecation")
    protected void handleVIRightClick(InventoryClickEvent event, VirtualInventory inventory, int slot, Player player, ItemStack clicked, ItemStack cursor) {
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null) return;
        
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
            
            if (inventory.setItemStack(updateReason, slot, clicked))
                event.setCursor(cursor);
        } else {
            // put one item from the cursor in the inventory
            ItemStack toAdd = cursor.clone();
            toAdd.setAmount(1);
            int remains = inventory.putItemStack(updateReason, slot, toAdd);
            if (remains == 0) {
                cursor.setAmount(cursor.getAmount() - 1);
                event.setCursor(cursor);
            }
        }
    }
    
    protected void handleVIItemShift(InventoryClickEvent event, VirtualInventory inventory, int slot, Player player, ItemStack clicked) {
        if (clicked == null) return;
        
        ItemStack previousStack = clicked.clone();
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Window window = WindowManager.getInstance().getOpenWindow(player);
        ItemUpdateEvent updateEvent = inventory.callPreUpdateEvent(updateReason, slot, previousStack, null);
        
        if (!updateEvent.isCancelled()) {
            int leftOverAmount;
            if (window instanceof AbstractDoubleWindow) {
                GUI otherGui;
                if (window instanceof AbstractSplitWindow) {
                    AbstractSplitWindow splitWindow = (AbstractSplitWindow) window;
                    GUI[] guis = splitWindow.getGUIs();
                    otherGui = guis[0] == this ? guis[1] : guis[0];
                } else {
                    otherGui = this;
                }
                
                leftOverAmount = ((AbstractGUI) otherGui).putIntoFirstVirtualInventory(updateReason, clicked, inventory);
            } else {
                leftOverAmount = InventoryUtils.addItemCorrectly(event.getWhoClicked().getInventory(), inventory.getItemStack(slot));
            }
            
            clicked.setAmount(leftOverAmount);
            inventory.setItemStackSilently(slot, clicked);
            
            inventory.callAfterUpdateEvent(updateReason, slot, previousStack, clicked);
        }
    }
    
    // TODO: add support for merged windows
    protected void handleVINumberKey(InventoryClickEvent event, VirtualInventory inventory, int slot, Player player, ItemStack clicked) {
        Window window = WindowManager.getInstance().getOpenWindow(player);
        if (window instanceof AbstractSingleWindow) {
            Inventory playerInventory = player.getInventory();
            int hotbarButton = event.getHotbarButton();
            ItemStack hotbarItem = playerInventory.getItem(hotbarButton);
            if (hotbarItem != null && hotbarItem.getType().isAir()) hotbarItem = null;
            
            UpdateReason updateReason = new PlayerUpdateReason(player, event);
            
            if (inventory.setItemStack(updateReason, slot, hotbarItem))
                playerInventory.setItem(hotbarButton, clicked);
        }
    }
    
    // TODO: add support for merged windows
    protected void handleVIOffHandKey(InventoryClickEvent event, VirtualInventory inventory, int slot, Player player, ItemStack clicked) {
        Window window = WindowManager.getInstance().getOpenWindow(player);
        if (window instanceof AbstractSingleWindow) {
            PlayerInventory playerInventory = player.getInventory();
            ItemStack offhandItem = playerInventory.getItemInOffHand();
            if (offhandItem != null && offhandItem.getType().isAir()) offhandItem = null;
            
            UpdateReason updateReason = new PlayerUpdateReason(player, event);
            
            if (inventory.setItemStack(updateReason, slot, offhandItem))
                playerInventory.setItemInOffHand(clicked);
        }
    }
    
    protected void handleVIDrop(boolean ctrl, InventoryClickEvent event, VirtualInventory inventory, int slot, Player player, ItemStack clicked) {
        if (clicked == null) return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        if (ctrl) {
            if (inventory.setItemStack(updateReason, slot, null)) {
                InventoryUtils.dropItemLikePlayer(player, clicked);
            }
        } else if (inventory.addItemAmount(updateReason, slot, -1) == -1) {
            clicked.setAmount(1);
            InventoryUtils.dropItemLikePlayer(player, clicked);
        }
        
    }
    
    @SuppressWarnings("deprecation")
    protected void handleVIDoubleClick(InventoryClickEvent event, VirtualInventory inventory, Player player, ItemStack cursor) {
        if (cursor == null) return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        cursor.setAmount(inventory.collectSimilar(updateReason, cursor));
        event.setCursor(cursor);
    }
    
    public boolean handleItemDrag(UpdateReason updateReason, int slot, ItemStack oldStack, ItemStack newStack) {
        SlotElement element = getSlotElement(slot);
        if (element != null) element = element.getHoldingElement();
        if (element instanceof VISlotElement) {
            VISlotElement viSlotElement = ((VISlotElement) element);
            VirtualInventory virtualInventory = viSlotElement.getVirtualInventory();
            int viSlot = viSlotElement.getSlot();
            if (virtualInventory.isSynced(viSlot, oldStack)) {
                return virtualInventory.setItemStack(updateReason, viSlot, newStack);
            }
        }
        
        return false;
    }
    
    public void handleItemShift(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (animation != null) return; // cancel all clicks if an animation is running
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        int amountLeft = putIntoFirstVirtualInventory(updateReason, clicked);
        if (amountLeft != clicked.getAmount()) {
            if (amountLeft != 0) event.getCurrentItem().setAmount(amountLeft);
            else event.getClickedInventory().setItem(event.getSlot(), null);
        }
    }
    
    protected int putIntoFirstVirtualInventory(UpdateReason updateReason, ItemStack itemStack, VirtualInventory... ignored) {
        LinkedHashSet<VirtualInventory> inventories = getAllVirtualInventories(ignored);
        int originalAmount = itemStack.getAmount();
        
        if (inventories.size() > 0) {
            for (VirtualInventory inventory : inventories) {
                int amountLeft = inventory.addItem(updateReason, itemStack);
                if (originalAmount != amountLeft)
                    return amountLeft;
            }
        }
        
        return originalAmount;
    }
    
    protected LinkedHashSet<VirtualInventory> getAllVirtualInventories(VirtualInventory... ignored) {
        return Arrays.stream(slotElements)
            .filter(Objects::nonNull)
            .map(SlotElement::getHoldingElement)
            .filter(element -> element instanceof VISlotElement)
            .map(element -> ((VISlotElement) element).getVirtualInventory())
            .filter(vi -> Arrays.stream(ignored).noneMatch(vi::equals))
            .sorted((vi1, vi2) -> -Integer.compare(vi1.getGuiShiftPriority(), vi2.getGuiShiftPriority()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    // endregion
    
    @Override
    public void handleSlotElementUpdate(GUI child, int slotIndex) {
        // find all SlotElements that link to this slotIndex in this child GUI and notify all parents
        for (int index = 0; index < size; index++) {
            SlotElement element = slotElements[index];
            if (element instanceof LinkedSlotElement) {
                LinkedSlotElement linkedSlotElement = (LinkedSlotElement) element;
                if (linkedSlotElement.getGUI() == child && linkedSlotElement.getSlotIndex() == slotIndex)
                    for (GUIParent parent : parents) parent.handleSlotElementUpdate(this, index);
            }
        }
    }
    
    public void addParent(@NotNull GUIParent parent) {
        parents.add(parent);
    }
    
    public void removeParent(@NotNull GUIParent parent) {
        parents.remove(parent);
    }
    
    public Set<GUIParent> getParents() {
        return parents;
    }
    
    @Override
    public List<Window> findAllWindows() {
        List<Window> windows = new ArrayList<>();
        List<GUIParent> unexploredParents = new ArrayList<>(this.parents);
        
        while (!unexploredParents.isEmpty()) {
            List<GUIParent> parents = new ArrayList<>(unexploredParents);
            unexploredParents.clear();
            for (GUIParent parent : parents) {
                if (parent instanceof AbstractGUI) unexploredParents.addAll(((AbstractGUI) parent).getParents());
                else if (parent instanceof Window) windows.add((Window) parent);
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
    public void playAnimation(@NotNull Animation animation, @Nullable Predicate<SlotElement> filter) {
        if (animation != null) cancelAnimation();
        
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
        animation.setGUI(this);
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
    
    public void updateControlItems() {
        for (SlotElement element : slotElements) {
            if (element instanceof ItemSlotElement) {
                Item item = ((ItemSlotElement) element).getItem();
                if (item instanceof ControlItem<?>)
                    item.notifyWindows();
            }
        }
    }
    
    @Override
    public void setSlotElement(int index, SlotElement slotElement) {
        SlotElement oldElement = slotElements[index];
        
        // set new SlotElement on index
        slotElements[index] = slotElement;
        
        // set the gui if it is a ControlItem
        if (slotElement instanceof ItemSlotElement) {
            Item item = ((ItemSlotElement) slotElement).getItem();
            if (item instanceof ControlItem<?>)
                ((ControlItem<?>) item).setGUI(this);
        }
        
        // notify parents that a SlotElement has been changed
        parents.forEach(parent -> parent.handleSlotElementUpdate(this, index));
        
        AbstractGUI oldLink = oldElement instanceof LinkedSlotElement ? (AbstractGUI) ((LinkedSlotElement) oldElement).getGUI() : null;
        AbstractGUI newLink = slotElement instanceof LinkedSlotElement ? (AbstractGUI) ((LinkedSlotElement) slotElement).getGUI() : null;
        
        // if newLink is the same as oldLink, there isn't anything to be done
        if (newLink == oldLink) return;
        
        // if the slot previously linked to GUI
        if (oldLink != null) {
            // If no other slot still links to that GUI, remove this GUI from parents
            if (Arrays.stream(slotElements)
                .filter(element -> element instanceof LinkedSlotElement)
                .map(element -> ((LinkedSlotElement) element).getGUI())
                .noneMatch(gui -> gui == oldLink)) oldLink.removeParent(this);
        }
        
        // if the slot now links to a GUI add this as parent
        if (newLink != null) {
            newLink.addParent(this);
        }
    }
    
    @Override
    public void addSlotElements(@NotNull SlotElement... slotElements) {
        for (SlotElement element : slotElements) {
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(this.slotElements);
            if (emptyIndex == -1) break;
            setSlotElement(emptyIndex, element);
        }
    }
    
    @Override
    public SlotElement getSlotElement(int index) {
        return slotElements[index];
    }
    
    @Override
    public boolean hasSlotElement(int index) {
        return slotElements[index] != null;
    }
    
    @Override
    @Nullable
    public SlotElement @NotNull[] getSlotElements() {
        return slotElements.clone();
    }
    
    @Override
    public void setItem(int index, @Nullable Item item) {
        remove(index);
        if (item != null) setSlotElement(index, new ItemSlotElement(item));
    }
    
    @Override
    public void addItems(@NotNull Item... items) {
        for (Item item : items) {
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(slotElements);
            if (emptyIndex == -1) break;
            setItem(emptyIndex, item);
        }
    }
    
    @Override
    public @Nullable Item getItem(int index) {
        SlotElement slotElement = slotElements[index];
        
        if (slotElement instanceof ItemSlotElement) {
            return ((ItemSlotElement) slotElement).getItem();
        } else if (slotElement instanceof LinkedSlotElement) {
            SlotElement holdingElement = slotElement.getHoldingElement();
            if (holdingElement instanceof ItemSlotElement) return ((ItemSlotElement) holdingElement).getItem();
        }
        
        return null;
    }
    
    @Override
    public @Nullable ItemProvider getBackground() {
        return background;
    }
    
    @Override
    public void setBackground(ItemProvider itemProvider) {
        this.background = itemProvider;
    }
    
    @Override
    public void remove(int index) {
        setSlotElement(index, null);
    }
    
    @Override
    public void applyStructure(@NotNull Structure structure) {
        structure.getIngredientList().insertIntoGUI(this);
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    // region coordinate-based methods
    @Override
    public void setSlotElement(int x, int y, SlotElement slotElement) {
        setSlotElement(convToIndex(x, y), slotElement);
    }
    
    @Override
    public SlotElement getSlotElement(int x, int y) {
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
    
    public void fill(@NotNull Set<Integer> slots, @Nullable Item item, boolean replaceExisting) {
        for (int slot : slots) {
            if (!replaceExisting && hasSlotElement(slot)) continue;
            setItem(slot, item);
        }
    }
    
    @Override
    public void fill(int start, int end, @Nullable Item item, boolean replaceExisting) {
        for (int i = start; i < end; i++) {
            if (!replaceExisting && hasSlotElement(i)) continue;
            setItem(i, item);
        }
    }
    
    @Override
    public void fill(@Nullable Item item, boolean replaceExisting) {
        fill(0, getSize(), item, replaceExisting);
    }
    
    @Override
    public void fillRow(int row, @Nullable Item item, boolean replaceExisting) {
        if (row >= height) throw new IllegalArgumentException("Row out of bounds");
        fill(SlotUtils.getSlotsRow(row, width), item, replaceExisting);
    }
    
    @Override
    public void fillColumn(int column, @Nullable Item item, boolean replaceExisting) {
        if (column >= width) throw new IllegalArgumentException("Column out of bounds");
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
    public void fillRectangle(int x, int y, @NotNull GUI gui, boolean replaceExisting) {
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, gui.getWidth(), gui.getHeight(), this.width)) {
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new LinkedSlotElement(gui, slotIndex));
            slotIndex++;
        }
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, @NotNull VirtualInventory virtualInventory, boolean replaceExisting) {
        fillRectangle(x, y, width, virtualInventory, null, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, @NotNull VirtualInventory virtualInventory, @Nullable ItemProvider background, boolean replaceExisting) {
        int height = (int) Math.ceil((double) virtualInventory.getSize() / (double) width);
        
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, width, height, this.width)) {
            if (slotIndex >= virtualInventory.getSize()) return;
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new VISlotElement(virtualInventory, slotIndex, background));
            slotIndex++;
        }
    }
    // endregion
    
}
