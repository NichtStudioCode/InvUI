package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.Controllable;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Marker;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.controlitem.ControlItem;
import de.studiocode.invui.item.impl.controlitem.PageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link GUI} with pages.
 *
 * @see GUIBuilder
 * @see SimplePagedItemsGUI
 * @see SimplePagedGUIsGUI
 */
public abstract class PagedGUI extends BaseGUI implements Controllable {
    
    private final List<Item> controlItems = new ArrayList<>();
    private final boolean infinitePages;
    private final int[] itemListSlots;
    protected int currentPage;
    
    public PagedGUI(int width, int height, boolean infinitePages, int... itemListSlots) {
        super(width, height);
        this.infinitePages = infinitePages;
        this.itemListSlots = itemListSlots;
    }
    
    public PagedGUI(int width, int height, boolean infinitePages, Structure structure) {
        this(width, height, infinitePages, structure.createIngredientList().findIndicesOfMarker(Marker.ITEM_LIST_SLOT));
        applyStructure(structure);
    }
    
    @Override
    public void addControlItem(int index, ControlItem<?> controlItem) {
        if (!(controlItem instanceof PageItem))
            throw new IllegalArgumentException("controlItem is not an instance of PageItem");
        
        ((PageItem) controlItem).setGui(this);
        setItem(index, controlItem);
        controlItems.add(controlItem);
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
        if (currentPage == 0 || infinitePages) return;
        
        int pageAmount = getPageAmount();
        if (currentPage < 0) currentPage = 0;
        else if (currentPage >= pageAmount) currentPage = pageAmount - 1;
    }
    
    private void updateControlItems() {
        controlItems.forEach(Item::notifyWindows);
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
    
    public boolean hasInfinitePages() {
        return infinitePages;
    }
    
    public int[] getItemListSlots() {
        return itemListSlots;
    }
    
    public abstract int getPageAmount();
    
    protected abstract List<SlotElement> getPageElements(int page);
    
}
