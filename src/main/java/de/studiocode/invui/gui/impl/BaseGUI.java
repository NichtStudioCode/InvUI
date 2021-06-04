package de.studiocode.invui.gui.impl;

import de.studiocode.invui.animation.Animation;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.GUIParent;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.gui.SlotElement.LinkedSlotElement;
import de.studiocode.invui.gui.SlotElement.VISlotElement;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import de.studiocode.invui.util.ArrayUtils;
import de.studiocode.invui.util.SlotUtils;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import de.studiocode.invui.virtualinventory.event.ItemUpdateEvent;
import de.studiocode.invui.virtualinventory.event.PlayerUpdateReason;
import de.studiocode.invui.virtualinventory.event.UpdateReason;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.WindowManager;
import de.studiocode.invui.window.impl.merged.MergedWindow;
import de.studiocode.invui.window.impl.merged.split.SplitWindow;
import de.studiocode.invui.window.impl.single.SingleWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseGUI implements GUI {
    
    private final int width;
    private final int height;
    private final int size;
    private final SlotElement[] slotElements;
    private final Set<GUIParent> parents = new HashSet<>();
    
    private SlotElement[] animationElements;
    private Animation animation;
    
    private ItemBuilder background;
    
    public BaseGUI(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        slotElements = new SlotElement[size];
    }
    
    @Override
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        if (animation != null) {
            // cancel all clicks if an animation is running
            event.setCancelled(true);
            return;
        }
        
        SlotElement slotElement = slotElements[slotNumber];
        if (slotElement instanceof LinkedSlotElement) {
            LinkedSlotElement linkedElement = (LinkedSlotElement) slotElement;
            linkedElement.getGui().handleClick(linkedElement.getSlotIndex(), player, clickType, event);
        } else if (slotElement instanceof ItemSlotElement) {
            event.setCancelled(true); // if it is an Item, don't let the player move it
            ItemSlotElement itemElement = (ItemSlotElement) slotElement;
            itemElement.getItem().handleClick(clickType, player, event);
        } else if (slotElement instanceof VISlotElement) {
            handleVISlotElementClick((VISlotElement) slotElement, event);
        } else event.setCancelled(true); // Only VISlotElements have allowed interactions
    }
    
    @SuppressWarnings("deprecation")
    private void handleVISlotElementClick(VISlotElement element, InventoryClickEvent event) {
        VirtualInventory virtualInventory = element.getVirtualInventory();
        int slot = element.getSlot();
        
        Player player = (Player) event.getWhoClicked();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        if (virtualInventory.isSynced(slot, clicked)) {
            boolean cancel = false;
            
            switch (event.getAction()) {
                
                case CLONE_STACK:
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    // empty, this does not affect anything
                    break;
                
                case DROP_ONE_SLOT:
                case PICKUP_ONE:
                    cancel = virtualInventory.setItemAmount(updateReason, slot, -1) != -1;
                    break;
                
                case DROP_ALL_SLOT:
                case PICKUP_ALL:
                    cancel = !virtualInventory.setItemStack(updateReason, slot, null);
                    // set null
                    break;
                
                case PICKUP_HALF:
                    int amount = virtualInventory.getAmount(slot);
                    int halfAmount = amount / 2;
                    int newAmount = virtualInventory.setItemAmount(updateReason, slot, halfAmount);
                    
                    // amount did not change as predicted
                    if (newAmount != halfAmount) {
                        cancel = true;
                        
                        // action wasn't completely cancelled
                        if (newAmount != amount) {
                            int cursorAmount = amount - newAmount;
                            cancel = true;
                            ItemStack newCursorStack = clicked.clone();
                            newCursorStack.setAmount(cursorAmount);
                            event.setCursor(newCursorStack);
                        }
                    }
                    
                    break;
                
                case PLACE_SOME:
                case PLACE_ALL:
                    int amountLeft = virtualInventory.putItemStack(updateReason, slot, cursor);
                    if (amountLeft > 0) {
                        cancel = true;
                        if (amountLeft != cursor.getAmount())
                            cursor.setAmount(amountLeft);
                    }
                    break;
                
                case PLACE_ONE:
                    ItemStack itemStack = cursor.clone();
                    itemStack.setAmount(1);
                    cancel = virtualInventory.putItemStack(updateReason, slot, itemStack) != 0;
                    break;
                
                case SWAP_WITH_CURSOR:
                    cancel = !virtualInventory.setItemStack(updateReason, slot, event.getCursor());
                    break;
                
                case COLLECT_TO_CURSOR:
                    cancel = true;
                    ItemStack newCursorStack = cursor.clone();
                    newCursorStack.setAmount(virtualInventory.collectToCursor(updateReason, newCursorStack));
                    event.setCursor(newCursorStack);
                    break;
                
                case MOVE_TO_OTHER_INVENTORY:
                    cancel = true;
                    handleMoveToOtherInventory(player, event, virtualInventory, slot, updateReason);
                    break;
                
                case HOTBAR_MOVE_AND_READD:
                case HOTBAR_SWAP:
                    cancel = handleHotbarSwap(player, event, virtualInventory, slot, updateReason);
                    break;
                
                default:
                    // action not supported
                    cancel = true;
                    break;
            }
            
            event.setCancelled(cancel);
        } else event.setCancelled(true);
    }
    
    private void handleMoveToOtherInventory(Player player, InventoryClickEvent event, VirtualInventory inventory, int slot, UpdateReason reason) {
        Window window = WindowManager.getInstance().findOpenWindow(player).orElse(null);
        
        ItemStack invStack = inventory.getItemStack(slot);
        ItemUpdateEvent updateEvent = inventory.callUpdateEvent(reason, slot, invStack, null);
        
        if (!updateEvent.isCancelled()) {
            int leftOverAmount;
            if (window instanceof MergedWindow) {
                GUI otherGui;
                if (window instanceof SplitWindow) {
                    SplitWindow splitWindow = (SplitWindow) window;
                    GUI[] guis = splitWindow.getGuis();
                    otherGui = guis[0] == this ? guis[1] : guis[0];
                } else {
                    otherGui = this;
                }
                
                leftOverAmount = ((BaseGUI) otherGui).putIntoVirtualInventories(reason, invStack, inventory);
            } else {
                leftOverAmount = 0;
                HashMap<Integer, ItemStack> leftover = event.getWhoClicked().getInventory().addItem(inventory.getItemStack(slot));
                if (!leftover.isEmpty()) leftOverAmount = leftover.get(0).getAmount();
            }
            
            invStack.setAmount(leftOverAmount);
            inventory.setItemStackSilently(slot, invStack);
        }
    }
    
    private boolean handleHotbarSwap(Player player, InventoryClickEvent event, VirtualInventory inventory, int slot, UpdateReason reason) {
        Window window = WindowManager.getInstance().findOpenWindow(player).orElse(null);
        if (window instanceof SingleWindow) {
            int hotbarButton = event.getHotbarButton();
            ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
            if (hotbarItem != null) hotbarItem = hotbarItem.clone();
            
            return !inventory.setItemStack(reason, slot, hotbarItem);
        } // TODO: add support for merged windows
        
        return true;
    }
    
    @Override
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
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (animation != null) return; // cancel all clicks if an animation is running
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        int amountLeft = putIntoVirtualInventories(updateReason, clicked);
        if (amountLeft != clicked.getAmount()) {
            if (amountLeft != 0) event.getCurrentItem().setAmount(amountLeft);
            else event.getClickedInventory().setItem(event.getSlot(), null);
        }
    }
    
    private int putIntoVirtualInventories(UpdateReason updateReason, ItemStack itemStack, VirtualInventory... ignored) {
        if (itemStack.getAmount() <= 0) throw new IllegalArgumentException("Illegal ItemStack amount");
        
        List<VirtualInventory> virtualInventories = getAllVirtualInventories(ignored);
        if (virtualInventories.size() > 0) {
            int amountLeft = itemStack.getAmount();
            for (VirtualInventory virtualInventory : virtualInventories) {
                ItemStack toAdd = itemStack.clone();
                toAdd.setAmount(amountLeft);
                amountLeft = virtualInventory.addItem(updateReason, toAdd);
                
                if (amountLeft == 0) break;
            }
            
            return amountLeft;
        }
        
        return itemStack.getAmount();
    }
    
    private List<VirtualInventory> getAllVirtualInventories(VirtualInventory... ignored) {
        List<VirtualInventory> virtualInventories = new ArrayList<>();
        Arrays.stream(slotElements)
            .filter(Objects::nonNull)
            .map(SlotElement::getHoldingElement)
            .filter(element -> element instanceof VISlotElement)
            .map(element -> ((VISlotElement) element).getVirtualInventory())
            .filter(vi -> Arrays.stream(ignored).noneMatch(vi::equals))
            .forEach(vi -> {
                if (!virtualInventories.contains(vi)) virtualInventories.add(vi);
            });
        
        return virtualInventories;
    }
    
    @Override
    public void handleSlotElementUpdate(GUI child, int slotIndex) {
        // find all SlotElements that link to this slotIndex in this child GUI and notify all parents
        for (int index = 0; index < size; index++) {
            SlotElement element = slotElements[index];
            if (element instanceof LinkedSlotElement) {
                LinkedSlotElement linkedSlotElement = (LinkedSlotElement) element;
                if (linkedSlotElement.getGui() == child && linkedSlotElement.getSlotIndex() == slotIndex)
                    for (GUIParent parent : parents) parent.handleSlotElementUpdate(this, index);
            }
        }
    }
    
    @Override
    public void addParent(@NotNull GUIParent parent) {
        parents.add(parent);
    }
    
    @Override
    public void removeParent(@NotNull GUIParent parent) {
        parents.remove(parent);
    }
    
    @Override
    public Set<GUIParent> getParents() {
        return parents;
    }
    
    @Override
    public List<Window> findAllWindows() {
        List<Window> windows = new ArrayList<>();
        List<GUIParent> parents = new ArrayList<>(this.parents);
        
        while (!parents.isEmpty()) {
            List<GUIParent> parents1 = new ArrayList<>(parents);
            parents.clear();
            for (GUIParent parent : parents1) {
                if (parent instanceof GUI) parents.addAll(((GUI) parent).getParents());
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
    
    @Override
    public void setSlotElement(int index, SlotElement slotElement) {
        SlotElement oldElement = slotElements[index];
        GUI oldLink = oldElement instanceof LinkedSlotElement ? ((LinkedSlotElement) oldElement).getGui() : null;
        
        // set new SlotElement on index
        slotElements[index] = slotElement;
        
        GUI newLink = slotElement instanceof LinkedSlotElement ? ((LinkedSlotElement) slotElement).getGui() : null;
        
        // notify parents that a SlotElement has been changed
        parents.forEach(parent -> parent.handleSlotElementUpdate(this, index));
        
        // if newLink is the same as oldLink, there isn't anything to be done
        if (newLink == oldLink) return;
        
        // if the slot previously linked to GUI
        if (oldLink != null) {
            // If no other slot still links to that GUI, remove this GUI from parents
            if (Arrays.stream(slotElements)
                .filter(element -> element instanceof LinkedSlotElement)
                .map(element -> ((LinkedSlotElement) element).getGui())
                .noneMatch(gui -> gui == oldLink)) oldLink.removeParent(this);
        }
        
        // if the slot now links to a GUI add this as parent
        if (newLink != null) {
            newLink.addParent(this);
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
    public SlotElement[] getSlotElements() {
        return slotElements.clone();
    }
    
    @Override
    public void setItem(int index, Item item) {
        remove(index);
        if (item != null) setSlotElement(index, new ItemSlotElement(item));
    }
    
    @Override
    public void addItems(@NotNull Item... items) {
        for (Item item : items) {
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(items);
            if (emptyIndex == -1) break;
            setItem(emptyIndex, item);
        }
    }
    
    @Override
    public Item getItem(int index) {
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
    public void setBackground(ItemBuilder itemBuilder) {
        this.background = itemBuilder;
    }
    
    @Override
    public ItemBuilder getBackground() {
        return background;
    }
    
    @Override
    public void remove(int index) {
        setSlotElement(index, null);
    }
    
    @Override
    public void applyStructure(Structure structure) {
        structure.createIngredientList().insertIntoGUI(this);
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    // -- coordinate-based methods --
    
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
    public void setItem(int x, int y, Item item) {
        setItem(convToIndex(x, y), item);
    }
    
    @Override
    public Item getItem(int x, int y) {
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
        int height = (int) Math.ceil((double) virtualInventory.getSize() / (double) width);
        
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, width, height, this.width)) {
            if (slotIndex >= virtualInventory.getSize()) return;
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new VISlotElement(virtualInventory, slotIndex));
            slotIndex++;
        }
    }
    
}
