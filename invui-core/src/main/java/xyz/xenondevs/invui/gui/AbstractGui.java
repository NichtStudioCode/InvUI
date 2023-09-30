package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.animation.Animation;
import xyz.xenondevs.invui.gui.structure.Marker;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;
import xyz.xenondevs.invui.util.ArrayUtils;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.util.ItemUtils;
import xyz.xenondevs.invui.util.SlotUtils;
import xyz.xenondevs.invui.window.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractGui implements Gui, GuiParent {
    
    private final int width;
    private final int height;
    private final int size;
    private final SlotElement[] slotElements;
    private final Set<GuiParent> parents = new HashSet<>();
    
    private boolean frozen;
    private ItemProvider background;
    private Animation animation;
    private SlotElement[] animationElements;
    
    public AbstractGui(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        slotElements = new SlotElement[size];
    }
    
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        // cancel all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null) {
            event.setCancelled(true);
            return;
        }
        
        SlotElement slotElement = slotElements[slotNumber];
        if (slotElement instanceof SlotElement.LinkedSlotElement) {
            SlotElement.LinkedSlotElement linkedElement = (SlotElement.LinkedSlotElement) slotElement;
            AbstractGui gui = (AbstractGui) linkedElement.getGui();
            gui.handleClick(linkedElement.getSlotIndex(), player, clickType, event);
        } else if (slotElement instanceof SlotElement.ItemSlotElement) {
            event.setCancelled(true); // if it is an Item, don't let the player move it
            SlotElement.ItemSlotElement itemElement = (SlotElement.ItemSlotElement) slotElement;
            itemElement.getItem().handleClick(clickType, player, event);
        } else if (slotElement instanceof SlotElement.InventorySlotElement) {
            handleInvSlotElementClick((SlotElement.InventorySlotElement) slotElement, event);
        } else event.setCancelled(true); // Only InventorySlotElements have allowed interactions
    }
    
    // region inventories
    protected void handleInvSlotElementClick(SlotElement.InventorySlotElement element, InventoryClickEvent event) {
        // these actions are ignored as they don't modify the inventory
        InventoryAction action = event.getAction();
        if (action != InventoryAction.CLONE_STACK
            && action != InventoryAction.DROP_ALL_CURSOR
            && action != InventoryAction.DROP_ONE_CURSOR
        ) {
            event.setCancelled(true);
            
            Inventory inventory = element.getInventory();
            int slot = element.getSlot();
            
            Player player = (Player) event.getWhoClicked();
            
            ItemStack cursor = event.getCursor();
            if (cursor != null && cursor.getType().isAir()) cursor = null;
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType().isAir()) clicked = null;
            
            ItemStack technicallyClicked = inventory.getItem(slot);
            if (inventory.isSynced(slot, clicked) || didClickBackgroundItem(player, element, inventory, slot, clicked)) {
                
                switch (event.getClick()) {
                    case LEFT:
                        handleInvLeftClick(event, inventory, slot, player, technicallyClicked, cursor);
                        break;
                    case RIGHT:
                        handleInvRightClick(event, inventory, slot, player, technicallyClicked, cursor);
                        break;
                    case SHIFT_RIGHT:
                    case SHIFT_LEFT:
                        handleInvItemShift(event, inventory, slot, player, technicallyClicked);
                        break;
                    case NUMBER_KEY:
                        handleInvNumberKey(event, inventory, slot, player, technicallyClicked);
                        break;
                    case SWAP_OFFHAND:
                        handleInvOffHandKey(event, inventory, slot, player, technicallyClicked);
                        break;
                    case DROP:
                        handleInvDrop(false, event, inventory, slot, player, technicallyClicked);
                        break;
                    case CONTROL_DROP:
                        handleInvDrop(true, event, inventory, slot, player, technicallyClicked);
                        break;
                    case DOUBLE_CLICK:
                        handleInvDoubleClick(event, inventory, player, cursor);
                        break;
                }
            }
        }
    }
    
    private boolean didClickBackgroundItem(Player player, SlotElement.InventorySlotElement element, Inventory inventory, int slot, ItemStack clicked) {
        String lang = player.getLocale();
        return !inventory.hasItem(slot) && (isBuilderSimilar(background, lang, clicked) || isBuilderSimilar(element.getBackground(), lang, clicked));
    }
    
    private boolean isBuilderSimilar(ItemProvider builder, String lang, ItemStack expected) {
        return builder != null && builder.get(lang).isSimilar(expected);
    }
    
    @SuppressWarnings("deprecation")
    protected void handleInvLeftClick(InventoryClickEvent event, Inventory inventory, int slot, Player player, ItemStack clicked, ItemStack cursor) {
        // nothing happens if both cursor and clicked stack are empty
        if (clicked == null && cursor == null) return;
        
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
    protected void handleInvRightClick(InventoryClickEvent event, Inventory inventory, int slot, Player player, ItemStack clicked, ItemStack cursor) {
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
    
    protected void handleInvItemShift(InventoryClickEvent event, Inventory inventory, int slot, Player player, ItemStack clicked) {
        if (clicked == null) return;
        
        ItemStack previousStack = clicked.clone();
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Window window = WindowManager.getInstance().getOpenWindow(player);
        ItemPreUpdateEvent updateEvent = inventory.callPreUpdateEvent(updateReason, slot, previousStack, null);
        
        if (!updateEvent.isCancelled()) {
            int leftOverAmount;
            if (window instanceof AbstractDoubleWindow) {
                Gui otherGui;
                if (window instanceof AbstractSplitWindow) {
                    AbstractSplitWindow splitWindow = (AbstractSplitWindow) window;
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
    protected void handleInvNumberKey(InventoryClickEvent event, Inventory inventory, int slot, Player player, ItemStack clicked) {
        Window window = WindowManager.getInstance().getOpenWindow(player);
        if (window instanceof AbstractSingleWindow) {
            org.bukkit.inventory.Inventory playerInventory = player.getInventory();
            int hotbarButton = event.getHotbarButton();
            ItemStack hotbarItem = playerInventory.getItem(hotbarButton);
            if (hotbarItem != null && hotbarItem.getType().isAir()) hotbarItem = null;
            
            UpdateReason updateReason = new PlayerUpdateReason(player, event);
            
            if (inventory.setItem(updateReason, slot, hotbarItem))
                playerInventory.setItem(hotbarButton, clicked);
        }
    }
    
    // TODO: add support for merged windows
    protected void handleInvOffHandKey(InventoryClickEvent event, Inventory inventory, int slot, Player player, ItemStack clicked) {
        Window window = WindowManager.getInstance().getOpenWindow(player);
        if (window instanceof AbstractSingleWindow) {
            PlayerInventory playerInventory = player.getInventory();
            ItemStack offhandItem = playerInventory.getItemInOffHand();
            if (offhandItem != null && offhandItem.getType().isAir()) offhandItem = null;
            
            UpdateReason updateReason = new PlayerUpdateReason(player, event);
            
            if (inventory.setItem(updateReason, slot, offhandItem))
                playerInventory.setItemInOffHand(clicked);
        }
    }
    
    protected void handleInvDrop(boolean ctrl, InventoryClickEvent event, Inventory inventory, int slot, Player player, ItemStack clicked) {
        if (clicked == null) return;
        
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
    
    @SuppressWarnings("deprecation")
    protected void handleInvDoubleClick(InventoryClickEvent event, Inventory inventory, Player player, ItemStack cursor) {
        if (cursor == null) return;
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        cursor.setAmount(inventory.collectSimilar(updateReason, cursor));
        event.setCursor(cursor);
    }
    
    public boolean handleItemDrag(UpdateReason updateReason, int slot, ItemStack oldStack, ItemStack newStack) {
        // cancel all clicks if the gui is frozen or an animation is running
        if (frozen || animation != null)
            return false;
        
        SlotElement element = getSlotElement(slot);
        if (element != null) element = element.getHoldingElement();
        if (element instanceof SlotElement.InventorySlotElement) {
            SlotElement.InventorySlotElement invSlotElement = ((SlotElement.InventorySlotElement) element);
            Inventory inventory = invSlotElement.getInventory();
            int viSlot = invSlotElement.getSlot();
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
        
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        
        int amountLeft = putIntoFirstInventory(updateReason, clicked);
        if (amountLeft != clicked.getAmount()) {
            if (amountLeft != 0) event.getCurrentItem().setAmount(amountLeft);
            else event.getClickedInventory().setItem(event.getSlot(), null);
        }
    }
    
    protected int putIntoFirstInventory(UpdateReason updateReason, ItemStack itemStack, Inventory... ignored) {
        LinkedHashSet<Inventory> inventories = getAllInventories(ignored);
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
    
    public LinkedHashSet<Inventory> getAllInventories(Inventory... ignored) {
        return Arrays.stream(slotElements)
            .filter(Objects::nonNull)
            .map(SlotElement::getHoldingElement)
            .filter(element -> element instanceof SlotElement.InventorySlotElement)
            .map(element -> ((SlotElement.InventorySlotElement) element).getInventory())
            .filter(vi -> Arrays.stream(ignored).noneMatch(vi::equals))
            .sorted((vi1, vi2) -> -Integer.compare(vi1.getGuiPriority(), vi2.getGuiPriority()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    // endregion
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        // find all SlotElements that link to this slotIndex in this child Gui and notify all parents
        for (int index = 0; index < size; index++) {
            SlotElement element = slotElements[index];
            if (element instanceof SlotElement.LinkedSlotElement) {
                SlotElement.LinkedSlotElement linkedSlotElement = (SlotElement.LinkedSlotElement) element;
                if (linkedSlotElement.getGui() == child && linkedSlotElement.getSlotIndex() == slotIndex)
                    for (GuiParent parent : parents) parent.handleSlotElementUpdate(this, index);
            }
        }
    }
    
    public void addParent(@NotNull GuiParent parent) {
        parents.add(parent);
    }
    
    public void removeParent(@NotNull GuiParent parent) {
        parents.remove(parent);
    }
    
    public Set<GuiParent> getParents() {
        return parents;
    }
    
    @Override
    public @NotNull List<@NotNull Window> findAllWindows() {
        List<Window> windows = new ArrayList<>();
        List<GuiParent> unexploredParents = new ArrayList<>(this.parents);
        
        while (!unexploredParents.isEmpty()) {
            List<GuiParent> parents = new ArrayList<>(unexploredParents);
            unexploredParents.clear();
            for (GuiParent parent : parents) {
                if (parent instanceof AbstractGui) unexploredParents.addAll(((AbstractGui) parent).getParents());
                else if (parent instanceof Window) windows.add((Window) parent);
            }
        }
        
        return windows;
    }
    
    @Override
    public @NotNull Set<@NotNull Player> findAllCurrentViewers() {
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
    public void playAnimation(@NotNull Animation animation, @Nullable Predicate<@NotNull SlotElement> filter) {
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
    
    public void updateControlItems() {
        for (SlotElement element : slotElements) {
            if (element instanceof SlotElement.ItemSlotElement) {
                Item item = ((SlotElement.ItemSlotElement) element).getItem();
                if (item instanceof ControlItem<?>)
                    item.notifyWindows();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void setSlotElement(int index, SlotElement slotElement) {
        SlotElement oldElement = slotElements[index];
        
        // set new SlotElement on index
        slotElements[index] = slotElement;
        
        // set the gui if it is a ControlItem
        if (slotElement instanceof SlotElement.ItemSlotElement) {
            Item item = ((SlotElement.ItemSlotElement) slotElement).getItem();
            if (item instanceof ControlItem<?>)
                ((ControlItem<Gui>) item).setGui(this);
        }
        
        // notify parents that a SlotElement has been changed
        parents.forEach(parent -> parent.handleSlotElementUpdate(this, index));
        
        AbstractGui oldLink = oldElement instanceof SlotElement.LinkedSlotElement ? (AbstractGui) ((SlotElement.LinkedSlotElement) oldElement).getGui() : null;
        AbstractGui newLink = slotElement instanceof SlotElement.LinkedSlotElement ? (AbstractGui) ((SlotElement.LinkedSlotElement) slotElement).getGui() : null;
        
        // if newLink is the same as oldLink, there isn't anything to be done
        if (newLink == oldLink) return;
        
        // if the slot previously linked to Gui
        if (oldLink != null) {
            // If no other slot still links to that Gui, remove this Gui from parents
            if (Arrays.stream(slotElements)
                .filter(element -> element instanceof SlotElement.LinkedSlotElement)
                .map(element -> ((SlotElement.LinkedSlotElement) element).getGui())
                .noneMatch(gui -> gui == oldLink)) oldLink.removeParent(this);
        }
        
        // if the slot now links to a Gui add this as parent
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
    public @Nullable SlotElement getSlotElement(int index) {
        return slotElements[index];
    }
    
    @Override
    public boolean hasSlotElement(int index) {
        return slotElements[index] != null;
    }
    
    @Override
    @Nullable
    public SlotElement @NotNull [] getSlotElements() {
        return slotElements.clone();
    }
    
    @Override
    public void setItem(int index, @Nullable Item item) {
        remove(index);
        if (item != null) setSlotElement(index, new SlotElement.ItemSlotElement(item));
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
        
        if (slotElement instanceof SlotElement.ItemSlotElement) {
            return ((SlotElement.ItemSlotElement) slotElement).getItem();
        } else if (slotElement instanceof SlotElement.LinkedSlotElement) {
            SlotElement holdingElement = slotElement.getHoldingElement();
            if (holdingElement instanceof SlotElement.ItemSlotElement)
                return ((SlotElement.ItemSlotElement) holdingElement).getItem();
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
    
    // region coordinate-based methods
    @Override
    public void setSlotElement(int x, int y, SlotElement slotElement) {
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
    public void fillRectangle(int x, int y, @NotNull Gui gui, boolean replaceExisting) {
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, gui.getWidth(), gui.getHeight(), this.width)) {
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new SlotElement.LinkedSlotElement(gui, slotIndex));
            slotIndex++;
        }
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, @NotNull Inventory inventory, boolean replaceExisting) {
        fillRectangle(x, y, width, inventory, null, replaceExisting);
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, @NotNull Inventory inventory, @Nullable ItemProvider background, boolean replaceExisting) {
        int height = (int) Math.ceil((double) inventory.getSize() / (double) width);
        
        int slotIndex = 0;
        for (int slot : SlotUtils.getSlotsRect(x, y, width, height, this.width)) {
            if (slotIndex >= inventory.getSize()) return;
            if (hasSlotElement(slot) && !replaceExisting) continue;
            setSlotElement(slot, new SlotElement.InventorySlotElement(inventory, slotIndex, background));
            slotIndex++;
        }
    }
    // endregion
    
    @SuppressWarnings("unchecked")
    public static abstract class AbstractBuilder<G extends Gui, S extends Gui.Builder<G, S>> implements Gui.Builder<G, S> {
        
        protected Structure structure;
        protected ItemProvider background;
        protected List<Consumer<G>> modifiers;
        protected boolean frozen;
        
        @Override
        public @NotNull S setStructure(int width, int height, @NotNull String structureData) {
            structure = new Structure(width, height, structureData);
            return (S) this;
        }
        
        @Override
        public @NotNull S setStructure(@NotNull String... structureData) {
            structure = new Structure(structureData);
            return (S) this;
        }
        
        @Override
        public @NotNull S setStructure(@NotNull Structure structure) {
            this.structure = structure;
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull ItemStack itemStack) {
            structure.addIngredient(key, itemStack);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull ItemProvider itemProvider) {
            structure.addIngredient(key, itemProvider);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull Item item) {
            structure.addIngredient(key, item);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull Inventory inventory) {
            structure.addIngredient(key, inventory);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull Inventory inventory, @Nullable ItemProvider background) {
            structure.addIngredient(key, inventory, background);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull SlotElement element) {
            structure.addIngredient(key, element);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull Marker marker) {
            structure.addIngredient(key, marker);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
            structure.addIngredient(key, itemSupplier);
            return (S) this;
        }
        
        @Override
        public @NotNull S addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
            structure.addIngredientElementSupplier(key, elementSupplier);
            return (S) this;
        }
        
        @Override
        public @NotNull S setBackground(@NotNull ItemProvider itemProvider) {
            background = itemProvider;
            return (S) this;
        }
        
        @Override
        public @NotNull S setBackground(@NotNull ItemStack itemStack) {
            background = new ItemWrapper(itemStack);
            return (S) this;
        }
        
        @Override
        public @NotNull S setFrozen(boolean frozen) {
            this.frozen = frozen;
            return (S) this;
        }
        
        @Override
        public @NotNull S addModifier(@NotNull Consumer<@NotNull G> modifier) {
            if (modifiers == null)
                modifiers = new ArrayList<>();
            
            modifiers.add(modifier);
            return (S) this;
        }
        
        @Override
        public @NotNull S setModifiers(@NotNull List<@NotNull Consumer<@NotNull G>> modifiers) {
            this.modifiers = modifiers;
            return (S) this;
        }
        
        protected void applyModifiers(@NotNull G gui) {
            gui.setFrozen(frozen);
            if (background != null) gui.setBackground(background);
            if (modifiers != null) modifiers.forEach(modifier -> modifier.accept(gui));
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public @NotNull S clone() {
            try {
                var clone = (AbstractBuilder<G, S>) super.clone();
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
