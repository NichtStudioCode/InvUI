package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.item.impl.BaseItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PagedGUI extends BaseGUI {
    
    private final boolean infinitePages;
    private final int[] itemListSlots;
    protected int currentPage;
    private Item forwardItem;
    private Item backItem;
    
    public PagedGUI(int width, int height, boolean infinitePages, int... itemListSlots) {
        super(width, height);
        this.infinitePages = infinitePages;
        this.itemListSlots = itemListSlots;
    }
    
    public void setControlItems(int backItemSlot, BackItem backItem, int forwardItemSlot, ForwardItem forwardItem) {
        setItem(backItemSlot, this.backItem = backItem);
        setItem(forwardItemSlot, this.forwardItem = forwardItem);
    }
    
    public void goForward() {
        if (hasNextPage()) {
            currentPage++;
            update();
        }
    }
    
    public boolean hasNextPage() {
        return currentPage < getPageAmount() - 1 || infinitePages;
    }
    
    public void goBack() {
        if (hasPageBefore()) {
            currentPage--;
            update();
        }
    }
    
    public boolean hasPageBefore() {
        return currentPage > 0;
    }
    
    protected void update() {
        updateControlItems();
        updatePageContent();
    }
    
    private void updateControlItems() {
        backItem.notifyWindows();
        forwardItem.notifyWindows();
    }
    
    private void updatePageContent() {
        if (getCurrentPage() < getPageAmount()) {
            List<SlotElement> slotElements = getPageItems(currentPage);
            
            for (int i = 0; i < itemListSlots.length; i++) {
                if (slotElements.size() > i) setSlotElement(itemListSlots[i], slotElements.get(i));
                else remove(itemListSlots[i]);
            }
        } else setCurrentPage(getPageAmount() - 1);
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    private void setCurrentPage(int page) {
        currentPage = page;
        update();
    }
    
    public Item getForwardItem() {
        return forwardItem;
    }
    
    public Item getBackItem() {
        return backItem;
    }
    
    public boolean hasInfinitePages() {
        return infinitePages;
    }
    
    public int[] getItemListSlots() {
        return itemListSlots;
    }
    
    protected abstract int getPageAmount();
    
    protected abstract List<SlotElement> getPageItems(int page);
    
    class ForwardItem extends BaseItem {
        
        private final ItemBuilder itemBuilder;
        
        public ForwardItem(@NotNull ItemBuilder itemBuilder) {
            this.itemBuilder = itemBuilder;
        }
        
        @Override
        public ItemBuilder getItemBuilder() {
            itemBuilder.clearLore();
            
            if (hasNextPage())
                itemBuilder.addLoreLines("§7Go to page §e" + (getCurrentPage() + 1)
                    + (infinitePages ? "" : "§7/§e" + getPageAmount()));
            else itemBuilder.addLoreLines("§7There are no more pages");
            
            return itemBuilder;
        }
        
        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            if (clickType == ClickType.LEFT) goForward();
        }
        
    }
    
    class BackItem extends BaseItem {
        
        private final ItemBuilder itemBuilder;
        
        public BackItem(@NotNull ItemBuilder itemBuilder) {
            this.itemBuilder = itemBuilder;
        }
        
        @Override
        public ItemBuilder getItemBuilder() {
            itemBuilder.clearLore();
            
            if (hasPageBefore())
                itemBuilder.addLoreLines("§7Go to page §e" + getCurrentPage() + "§7/§e" + getPageAmount());
            else itemBuilder.addLoreLines("§7You can't go further back");
            
            return itemBuilder;
        }
        
        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            if (clickType == ClickType.LEFT) goBack();
        }
        
    }
    
}
