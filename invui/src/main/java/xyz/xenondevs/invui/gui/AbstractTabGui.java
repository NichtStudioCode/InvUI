package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractTabGui extends AbstractGui implements TabGui {
    
    private final int tabAmount;
    private final int[] listSlots;
    
    private int currentTab = -1;
    
    private List<BiConsumer<Integer, Integer>> tabChangeHandlers;
    
    public AbstractTabGui(int width, int height, int tabAmount, int... listSlots) {
        super(width, height);
        this.tabAmount = tabAmount;
        this.listSlots = listSlots;
    }
    
    public AbstractTabGui(int width, int height, int tabAmount, Structure structure) {
        this(width, height, tabAmount, structure.getIngredientList().findContentListSlots());
        applyStructure(structure);
    }
    
    public void setTab(int tab) {
        if (tab < 0 || tab >= tabAmount)
            throw new IllegalArgumentException("Tab out of bounds");
        if (!isTabAvailable(tab))
            return;
        
        int previous = currentTab;
        currentTab = tab;
        update();
        
        if (tabChangeHandlers != null) {
            tabChangeHandlers.forEach(handler -> handler.accept(previous, tab));
        }
    }
    
    protected void update() {
        if (currentTab == -1) currentTab = getFirstAvailableTab();
        
        updateControlItems();
        updateContent();
    }
    
    private void updateContent() {
        List<SlotElement> slotElements = getSlotElements(currentTab);
        for (int i = 0; i < listSlots.length; i++) {
            int slot = listSlots[i];
            if (slotElements.size() > i) setSlotElement(listSlots[i], slotElements.get(i));
            else remove(slot);
        }
    }
    
    public int getFirstAvailableTab() {
        for (int tab = 0; tab < tabAmount; tab++) {
            if (isTabAvailable(tab)) return tab;
        }
        
        throw new UnsupportedOperationException("At least one tab needs to be available");
    }
    
    public int getCurrentTab() {
        return currentTab;
    }
    
    
    @Override
    public @Nullable List<@NotNull BiConsumer<Integer, Integer>> getTabChangeHandlers() {
        return tabChangeHandlers;
    }
    
    @Override
    public void setTabChangeHandlers(@Nullable List<@NotNull BiConsumer<Integer, Integer>> tabChangeHandlers) {
        this.tabChangeHandlers = tabChangeHandlers;
    }
    
    public void addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> tabChangeHandler) {
        if (tabChangeHandlers == null) tabChangeHandlers = new ArrayList<>();
        tabChangeHandlers.add(tabChangeHandler);
    }
    
    @Override
    public void removeTabChangeHandler(@NotNull BiConsumer<Integer, Integer> tabChangeHandler) {
        if (tabChangeHandlers != null) tabChangeHandlers.remove(tabChangeHandler);
    }
    
    public abstract boolean isTabAvailable(int tab);
    
    protected abstract List<SlotElement> getSlotElements(int tab);
    
    public static abstract class AbstractBuilder
        extends AbstractGui.AbstractBuilder<TabGui, TabGui.Builder>
        implements TabGui.Builder
    {
        
        protected List<Gui> tabs;
        protected List<BiConsumer<Integer, Integer>> tabChangeHandlers;
        
        @Override
        public TabGui.@NotNull Builder setTabs(@NotNull List<@Nullable Gui> tabs) {
            this.tabs = tabs;
            return this;
        }
        
        @Override
        public TabGui.@NotNull Builder addTab(@Nullable Gui tab) {
            if (this.tabs == null)
                this.tabs = new ArrayList<>();
            
            this.tabs.add(tab);
            return this;
        }
        
        @Override
        public TabGui.@NotNull Builder addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler) {
            if (tabChangeHandlers == null)
                tabChangeHandlers = new ArrayList<>(1);
            
            tabChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public TabGui.@NotNull Builder setTabChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers) {
            tabChangeHandlers = handlers;
            return this;
        }
        
        @Override
        protected void applyModifiers(@NotNull TabGui gui) {
            super.applyModifiers(gui);
            gui.setTabChangeHandlers(tabChangeHandlers);
        }
        
        @Override
        public @NotNull TabGui.Builder clone() {
            var clone = (AbstractBuilder) super.clone();
            clone.tabs = new ArrayList<>(tabs);
            clone.tabChangeHandlers = new ArrayList<>(tabChangeHandlers);
            return clone;
        }
        
    }
    
}
