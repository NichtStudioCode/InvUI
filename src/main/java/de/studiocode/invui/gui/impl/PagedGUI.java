package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.PagedGUIBuilder;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.pagedgui.BackItem;
import de.studiocode.invui.item.impl.pagedgui.ForwardItem;

import java.util.List;

/**
 * A {@link GUI} with pages.
 *
 * @see PagedGUIBuilder
 * @see SimplePagedItemsGUI
 * @see SimplePagedGUIsGUI
 */
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
    
    public void goBack() {
        if (hasPageBefore()) {
            currentPage--;
            update();
        }
    }
    
    public boolean hasNextPage() {
        return currentPage < getPageAmount() - 1 || infinitePages;
    }
    
    public boolean hasPageBefore() {
        return currentPage > 0;
    }
    
    protected void update() {
        correctPage();
        updateControlItems();
        updatePageContent();
    }
    
    private void correctPage() {
        if (currentPage == 0) return;
        int pageAmount = getPageAmount();
        if (currentPage < 0) currentPage = 0;
        else if (currentPage >= pageAmount) currentPage = pageAmount - 1;
    }
    
    private void updateControlItems() {
        backItem.notifyWindows();
        forwardItem.notifyWindows();
    }
    
    private void updatePageContent() {
        List<SlotElement> slotElements = getPageElements(currentPage);
        
        for (int i = 0; i < itemListSlots.length; i++) {
            if (slotElements.size() > i) setSlotElement(itemListSlots[i], slotElements.get(i));
            else remove(itemListSlots[i]);
        }
    }
    
    public int getCurrentPageIndex() {
        return currentPage;
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
    
    public abstract int getPageAmount();
    
    protected abstract List<SlotElement> getPageElements(int page);
    
}
